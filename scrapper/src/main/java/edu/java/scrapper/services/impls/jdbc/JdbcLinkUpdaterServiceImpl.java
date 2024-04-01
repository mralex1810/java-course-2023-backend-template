package edu.java.scrapper.services.impls.jdbc;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.LinkDAO;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.services.BotNotifyService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.LinkUpdaterService;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class JdbcLinkUpdaterServiceImpl implements LinkUpdaterService {
    private final LinkDAO linkDAO;
    private final ApplicationConfig.LinkCheckProperties linkCheckProperties;
    private final LinkUpdateCheckerService linkUpdateCheckerService;
    private final BotNotifyService botNotifyService;

    @Override
    public int update() {
        return (int) linkDAO.findAllCheckedBefore(LocalDateTime.now().minus(linkCheckProperties.linkCheckInterval()))
            .stream()
            .filter(this::updateLink)
            .count();
    }

    @Transactional
    protected boolean updateLink(Link link) {
        try {
            var lastUpdatedAt = linkUpdateCheckerService.lastUpdatedAtForLink(URI.create(link.uri()));
            linkDAO.updateLinkMeta(link.id(), lastUpdatedAt, LocalDateTime.now());
            botNotifyService.tryNotifyBot(link.linkUpdatedAt(), lastUpdatedAt, link.id(), link.uri());
            return true;
        } catch (UnsupportedLinkException e) {
            log.warn("Unsupported link: " + link.uri(), e);
            return false;
        }
    }
}
