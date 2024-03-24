package edu.java.scrapper.services.impls.jdbc;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.dao.LinkDAO;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Link;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.LinkUpdaterService;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public class JdbcLinkUpdaterServiceImpl implements LinkUpdaterService {
    private final LinkDAO linkDAO;
    private final ApplicationConfig.LinkCheckProperties linkCheckProperties;
    private final LinkUpdateCheckerService linkUpdateCheckerService;

    @Override
    public int update() {
        return (int) linkDAO.findAllCheckedBefore(LocalDateTime.now().minus(linkCheckProperties.linkCheckInterval()))
            .stream()
            .map(this::updateLink)
            .filter(it -> it)
            .count();
    }

    @Transactional
    protected boolean updateLink(Link link) {
        try {
            var lastUpdatedAt = linkUpdateCheckerService.lastUpdatedAtForLink(URI.create(link.uri()));
            linkDAO.updateLinkMeta(link.id(), lastUpdatedAt, LocalDateTime.now());
            return true;
        } catch (UnsupportedLinkException e) {
            log.warn("Unsupported link: " + link.uri(), e);
            return false;
        }
    }
}
