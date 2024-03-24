package edu.java.scrapper.services.impls.jpa;

import edu.java.scrapper.clients.bot.BotClient;
import edu.java.scrapper.clients.bot.dto.BotLinkUpdateRequest;
import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.jpa.JpaChat;
import edu.java.scrapper.model.jpa.JpaLink;
import edu.java.scrapper.repository.JpaLinkRepository;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.LinkUpdaterService;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public class JpaLinkUpdaterServiceImpl implements LinkUpdaterService {
    private final JpaLinkRepository jpaLinkRepository;
    private final ApplicationConfig.LinkCheckProperties linkCheckProperties;
    private final LinkUpdateCheckerService linkUpdateCheckerService;
    private final BotClient botClient;

    @Transactional
    public int update() {
        return (int) jpaLinkRepository.findAllByLinkCheckedAtBefore(LocalDateTime.now()
                .minus(linkCheckProperties.linkCheckInterval()))
            .stream()
            .map(this::updateLink)
            .filter(Optional::isPresent)
            .count();
    }

    public Optional<JpaLink> updateLink(JpaLink link) {
        try {
            var lastUpdatedAt = linkUpdateCheckerService.lastUpdatedAtForLink(URI.create(link.getUri()));
            if (lastUpdatedAt.isAfter(link.getLinkUpdatedAt())) {
                link.setLinkUpdatedAt(lastUpdatedAt);
                botClient.postLinkUpdate(
                    new BotLinkUpdateRequest(
                        link.getId(),
                        link.getUri(),
                        "Updated at " + lastUpdatedAt,
                        link.getChats().stream().map(JpaChat::getId).toList()
                    )
                );
            }
            link.setLinkCheckedAt(LocalDateTime.now());
            jpaLinkRepository.save(link);

            return Optional.of(link);
        } catch (UnsupportedLinkException e) {
            log.warn("Unsupported link: " + link.getUri(), e);
            return Optional.empty();
        }
    }
}
