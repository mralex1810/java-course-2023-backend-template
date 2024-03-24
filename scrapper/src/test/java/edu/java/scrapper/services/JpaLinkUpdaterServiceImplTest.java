package edu.java.scrapper.services;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.clients.bot.BotClient;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.jpa.JpaChat;
import edu.java.scrapper.model.jpa.JpaLink;
import edu.java.scrapper.repository.JpaChatRepository;
import edu.java.scrapper.repository.JpaLinkRepository;
import edu.java.scrapper.services.impls.jpa.JpaLinkUpdaterServiceImpl;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class JpaLinkUpdaterServiceImplTest extends IntegrationTest {

    @Autowired
    JpaLinkRepository jpaLinkRepository;

    @Autowired
    JpaChatRepository jpaChatRepository;

    @Mock
    ApplicationConfig.LinkCheckProperties linkCheckProperties;

    @Mock
    LinkUpdateCheckerService linkUpdateCheckerService;

    @Mock
    BotClient botClient;

    JpaLinkUpdaterServiceImpl jpaLinkUpdaterService;

    @BeforeEach
    void setUp() {
        jpaLinkUpdaterService = new JpaLinkUpdaterServiceImpl(
            jpaLinkRepository,
            linkCheckProperties,
            linkUpdateCheckerService,
            botClient
        );
        when(linkCheckProperties.linkCheckInterval()).thenReturn(Duration.ofHours(24));
    }

    @AfterEach
    public void tearDown() {
        jpaChatRepository.deleteAll();
        jpaLinkRepository.deleteAll();
    }

    @Test
    void update() throws UnsupportedLinkException {
        var startTest = LocalDateTime.now();
        String uri = "http://example.org";

        JpaChat chat = new JpaChat();
        chat.setId(101L);
        jpaChatRepository.save(chat);

        JpaLink link = new JpaLink();
        link.setUri(uri);
        link.setLinkCheckedAt(LocalDateTime.now().minusHours(25)); // link checked 25 hours ago
        link.setLinkUpdatedAt(LocalDateTime.now().minusDays(2));   // link was last updated 2 days ago
        link.setCreatedAt(LocalDateTime.now());
        link.setCreatedBy("admin");
        link.setChats(new ArrayList<>());
        link.getChats().add(chat); // add chat for the link

        jpaLinkRepository.save(link);

        when(linkUpdateCheckerService.lastUpdatedAtForLink(URI.create(uri))).thenReturn(LocalDateTime.now());

        int updateCount = jpaLinkUpdaterService.update();

        assertEquals(1, updateCount);
        // assert that link in database is indeed updated
        Optional<JpaLink> updatedLinkOpt = jpaLinkRepository.findByUri(uri);
        assertTrue(updatedLinkOpt.isPresent());
        assertTrue(startTest.isBefore(updatedLinkOpt.get().getLinkCheckedAt()));
        assertTrue(LocalDateTime.now().isAfter(updatedLinkOpt.get().getLinkCheckedAt()));
    }

    @Test
    void updateLink_ShouldIgnoreUnsupportedLink() throws UnsupportedLinkException {
        String uri = "http://unsupported.org";
        JpaLink link = new JpaLink();
        link.setUri(uri);
        link.setLinkCheckedAt(LocalDateTime.now().minusHours(25)); // link checked 25 hours ago
        link.setLinkUpdatedAt(LocalDateTime.now().minusDays(2));   // link was last updated 2 days ago
        link.setCreatedAt(LocalDateTime.now());
        link.setCreatedBy("admin");
        link.setChats(new ArrayList<>());
        jpaLinkRepository.save(link);

        when(linkUpdateCheckerService.lastUpdatedAtForLink(URI.create(uri))).thenThrow(new UnsupportedLinkException(
            "Unsupported link"));

        Optional<JpaLink> updatedLink = jpaLinkUpdaterService.updateLink(link);

        assertFalse(updatedLink.isPresent());
    }
}
