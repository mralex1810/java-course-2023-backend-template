package edu.java.scrapper.jdbcservice;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.ChatDAO;
import edu.java.scrapper.dao.ChatLinkSettingDAO;
import edu.java.scrapper.dao.LinkDAO;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.services.BotNotifyService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.LinkUpdaterService;
import edu.java.scrapper.services.impls.jdbc.JdbcLinkUpdaterServiceImpl;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class JdbcLinkUpdaterServiceImplTest extends IntegrationTest {

    @Autowired
    ChatDAO chatDAO;
    @Autowired
    ChatLinkSettingDAO chatLinkSettingDAO;
    @Autowired
    LinkDAO linkDAO;

    @Mock
    ApplicationConfig.LinkCheckProperties linkCheckProperties;

    @Mock
    LinkUpdateCheckerService linkUpdateCheckerService;
    @Mock BotNotifyService botNotifyService;

    LinkUpdaterService jpaLinkUpdaterService;

    @BeforeEach
    void setUp() {
        jpaLinkUpdaterService = new JdbcLinkUpdaterServiceImpl(
            linkDAO,
            linkCheckProperties,
            linkUpdateCheckerService,
            botNotifyService

        );
        when(linkCheckProperties.linkCheckInterval()).thenReturn(Duration.ofHours(24));
    }

    @AfterEach
    public void tearDown() {
        linkDAO.findAll().forEach(link -> linkDAO.remove(link.id()));
    }

    @Test
    void update() throws UnsupportedLinkException {
        var startTest = LocalDateTime.now();
        String uri = "http://example.org";

        Chat chat = new Chat(101L);
        chatDAO.add(chat);

        Link link = Link.create(
            uri,
            LocalDateTime.now().minusHours(25),
            LocalDateTime.now().minusDays(2),
            "admin"
        );
        linkDAO.add(link);

        when(linkUpdateCheckerService.lastUpdatedAtForLink(URI.create(uri))).thenReturn(LocalDateTime.now());

        int updateCount = jpaLinkUpdaterService.update();

        assertEquals(1, updateCount);
        // assert that link in database is indeed updated
        Optional<Link> updatedLinkOpt = linkDAO.findByUrl(uri);
        assertTrue(updatedLinkOpt.isPresent());
        assertTrue(startTest.isBefore(updatedLinkOpt.get().linkCheckedAt()));
        assertTrue(LocalDateTime.now().isAfter(updatedLinkOpt.get().linkUpdatedAt()));
    }
}
