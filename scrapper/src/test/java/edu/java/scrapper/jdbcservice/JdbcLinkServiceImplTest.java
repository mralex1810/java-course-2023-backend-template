package edu.java.scrapper.jdbcservice;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.dao.ChatDAO;
import edu.java.scrapper.dao.ChatLinkSettingDAO;
import edu.java.scrapper.dao.LinkDAO;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.services.LinkService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.impls.jdbc.JdbcLinkServiceImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Collection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class JdbcLinkServiceImplTest extends IntegrationTest {

    @Autowired
    ChatDAO chatDAO;
    @Autowired
    ChatLinkSettingDAO chatLinkSettingDAO;
    @Autowired
    LinkDAO linkDAO;

    LinkUpdateCheckerService linkUpdateCheckerService = mock(LinkUpdateCheckerService.class);

    LinkService linkService;

    @BeforeEach
    public void setUp() {
        linkService = new JdbcLinkServiceImpl(linkDAO, chatDAO, chatLinkSettingDAO, linkUpdateCheckerService);
    }

    @AfterEach
    public void tearDown() {
        chatLinkSettingDAO.findAll()
            .forEach(chatLinkSetting -> chatLinkSettingDAO.remove(chatLinkSetting.linkId(), chatLinkSetting.chatId()));
        chatDAO.findAll().forEach(chat -> chatDAO.remove(chat.id()));
        linkDAO.findAll().forEach(link -> linkDAO.remove(link.id()));
    }

    @Test
    public void addLink_NewLink_Successful() throws UnsupportedLinkException, URISyntaxException {
        // given
        long newChatId = 100;
        URI newUri = new URI("http://example.org");
        when(linkUpdateCheckerService.lastUpdatedAtForLink(newUri)).thenReturn(LocalDateTime.now());

        // when
        Link addedLink = linkService.add(newChatId, newUri);

        // then
        assertEquals(newUri.toString(), addedLink.uri());
    }

    @Test
    public void removeLink_ExistingLink_Successful()
        throws UnsupportedLinkException, URISyntaxException, LinkNotFoundException, ChatNotFoundException {
        // given
        long newChatId = 101;
        URI newUri = new URI("http://example.com");
        when(linkUpdateCheckerService.lastUpdatedAtForLink(newUri)).thenReturn(LocalDateTime.now());

        // when
        linkService.add(newChatId, newUri);
        Link removedLink = linkService.remove(newChatId, newUri);

        // then
        assertEquals(newUri.toString(), removedLink.uri());
        assertThrows(LinkNotFoundException.class, () -> linkService.remove(newChatId, newUri));
    }

    @Test
    public void listAllLinks_ForGivenChat_ReturnsAllLinks() throws UnsupportedLinkException, URISyntaxException {
        // given
        long newChatId = 102;
        URI uri1 = new URI("https://example1.com");
        URI uri2 = new URI("https://example2.com");
        LocalDateTime time = LocalDateTime.now();
        when(linkUpdateCheckerService.lastUpdatedAtForLink(uri1)).thenReturn(time);
        when(linkUpdateCheckerService.lastUpdatedAtForLink(uri2)).thenReturn(time);

        Link link1 = linkService.add(newChatId, uri1);
        Link link2 = linkService.add(newChatId, uri2);

        // when
        Collection<Link> links = linkService.listAll(newChatId);

        // then
        assertEquals(2, links.size());
        assertTrue(links.stream().map(Link::id).anyMatch(link1.id()::equals));
        assertTrue(links.stream().map(Link::id).anyMatch(link2.id()::equals));
    }
}
