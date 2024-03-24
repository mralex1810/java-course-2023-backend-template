package edu.java.scrapper.services.impls.jpa;

import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.model.jpa.JpaChat;
import edu.java.scrapper.model.jpa.JpaLink;
import edu.java.scrapper.repository.JpaChatRepository;
import edu.java.scrapper.repository.JpaLinkRepository;
import edu.java.scrapper.services.LinkService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JpaLinkServiceImpl implements LinkService {
    private final JpaLinkRepository jpaLinkRepository;
    private final JpaChatRepository jpaChatRepository;
    private final LinkUpdateCheckerService linkUpdateCheckerService;

    @Override
    @Transactional
    public Link add(long tgChatId, URI url) throws UnsupportedLinkException {
        JpaChat chat = jpaChatRepository.findById(tgChatId).orElseGet(() ->
            new JpaChat(tgChatId, new ArrayList<>())
        );

        Optional<JpaLink> linkO = jpaLinkRepository.findByUri(url.toString());
        if (linkO.isEmpty()) {
            var lastUpdatedAt = linkUpdateCheckerService.lastUpdatedAtForLink(url);
            JpaLink newLink = JpaLink.builder()
                .uri(url.toString())
                .linkUpdatedAt(lastUpdatedAt)
                .linkCheckedAt(LocalDateTime.now())
                .createdBy("admin")
                .createdAt(LocalDateTime.now())
                .chats(new ArrayList<>())
                .build();

            newLink = jpaLinkRepository.save(newLink);
            chat.addLink(newLink);
            jpaChatRepository.save(chat);

            return newLink.convertToModel();
        }

        JpaLink link = linkO.get();
        if (!chat.getLinks().contains(link)) {
            chat.addLink(link);
            jpaChatRepository.save(chat);
        }

        return link.convertToModel();
    }

    @Override
    @Transactional
    public Link remove(long tgChatId, URI url) throws LinkNotFoundException, ChatNotFoundException {
        JpaChat chat = jpaChatRepository.findById(tgChatId).orElseThrow(() ->
            new ChatNotFoundException(String.format("Chat ID %d not found", tgChatId)));

        JpaLink link = jpaLinkRepository.findByUri(url.toString()).orElseThrow(() ->
            new LinkNotFoundException(String.format("URL %s not found", url)));

        if (chat.getLinks().stream().map(JpaLink::getId).anyMatch(link.getId()::equals)) {
            chat.getLinks().removeIf(it -> it.getId().equals(link.getId()));
            link.getChats().removeIf(it -> it.getId().equals(chat.getId()));
            jpaChatRepository.save(chat);
        } else {
            throw new LinkNotFoundException(String.format("URL %s isn't registered for %d", url, tgChatId));
        }

        return link.convertToModel();
    }

    @Override
    @Transactional
    public List<Link> listAll(long tgChatId) {
        return jpaChatRepository.findAllLinksByChatId(tgChatId).stream().map(JpaLink::convertToModel).toList();
    }
}
