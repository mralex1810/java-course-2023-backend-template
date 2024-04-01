package edu.java.scrapper.services;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.repository.JpaChatRepository;
import edu.java.scrapper.repository.JpaLinkRepository;
import edu.java.scrapper.services.impls.jpa.JpaLinkServiceImpl;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
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
public class JpaLinkServiceImplTest extends IntegrationTest {

    @Autowired
    JpaChatRepository jpaChatRepository;
    @Autowired
    JpaLinkRepository jpaLinkRepository;

    LinkUpdateCheckerService linkUpdateCheckerService = mock(LinkUpdateCheckerService.class);

    JpaLinkServiceImpl jpaLinkService;

    @BeforeEach
    public void setUp() {
        jpaLinkService = new JpaLinkServiceImpl(jpaLinkRepository, jpaChatRepository, linkUpdateCheckerService);
    }

    @AfterEach
    public void tearDown() {
        jpaChatRepository.deleteAll();
        jpaLinkRepository.deleteAll();
    }

    @Test
    public void addLink_NewLink_Successful() throws UnsupportedLinkException, URISyntaxException {
        // given
        long newChatId = 100;
        URI newUri = new URI("http://example.org");
        when(linkUpdateCheckerService.lastUpdatedAtForLink(newUri)).thenReturn(LocalDateTime.now());

        // when
        Link addedLink = jpaLinkService.add(newChatId, newUri);

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
        jpaLinkService.add(newChatId, newUri);
        Link removedLink = jpaLinkService.remove(newChatId, newUri);

        // then
        assertEquals(newUri.toString(), removedLink.uri());
        assertThrows(LinkNotFoundException.class, () -> jpaLinkService.remove(newChatId, newUri));
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

        Link link1 = jpaLinkService.add(newChatId, uri1);
        Link link2 = jpaLinkService.add(newChatId, uri2);

        // when
        List<Link> links = jpaLinkService.listAll(newChatId);

        // then
        assertEquals(2, links.size());
        assertTrue(links.stream().map(Link::id).anyMatch(link1.id()::equals));
        assertTrue(links.stream().map(Link::id).anyMatch(link2.id()::equals));
    }
}
