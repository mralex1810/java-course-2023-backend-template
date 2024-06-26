package edu.java.scrapper.services.impls.jdbc;

import edu.java.scrapper.dao.ChatDAO;
import edu.java.scrapper.dao.ChatLinkSettingDAO;
import edu.java.scrapper.dao.LinkDAO;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.ChatLinkSetting;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.services.LinkService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JdbcLinkServiceImpl implements LinkService {
    private final LinkDAO linkDAO;
    private final ChatDAO chatDAO;
    private final ChatLinkSettingDAO chatLinkSettingDAO;
    private final LinkUpdateCheckerService linkUpdateCheckerService;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) throws UnsupportedLinkException {
        Chat chat = chatDAO.findById(tgChatId).orElseGet(() -> {
            chatDAO.add(new Chat(tgChatId));
            return new Chat(tgChatId);
        });

        Optional<Link> linkO = linkDAO.findByUrl(url.toString());
        if (linkO.isEmpty()) {
            var lastUpdatedAt = linkUpdateCheckerService.lastUpdatedAtForLink(url);
            Link newLink = Link.create(url.toString(), lastUpdatedAt, LocalDateTime.now(), "admin");
            linkDAO.add(newLink);
            linkO = linkDAO.findByUrl(url.toString());
        }

        Link link = linkO.get();
        chatLinkSettingDAO.add(new ChatLinkSetting(link.id(), chat.id()));

        return link;
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) throws LinkNotFoundException, ChatNotFoundException {
        Chat chat = chatDAO.findById(tgChatId).orElseThrow(() ->
            new ChatNotFoundException(String.format("Chat ID %d not found", tgChatId)));
        Link link = linkDAO.findByUrl(url.toString()).orElseThrow(() ->
            new LinkNotFoundException(String.format("URL %s not found", url)));

        if (!chatLinkSettingDAO.existsByLinkIdAndChatId(link.id(), chat.id())) {
            throw new LinkNotFoundException(String.format("URL %s isn't registered for %d", url, tgChatId));
        }
        chatLinkSettingDAO.remove(link.id(), chat.id());

        return link;
    }

    @Override
    @Transactional
    public Collection<Link> listAll(long tgChatId) {
        return chatLinkSettingDAO.findAllByChatId(tgChatId);
    }
}
