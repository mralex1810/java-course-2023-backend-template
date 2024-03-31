package edu.java.scrapper.links;

import edu.java.scrapper.services.LinkUpdaterService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "app.scheduler.enable", havingValue = "true", matchIfMissing = true)
@EnableScheduling
@AllArgsConstructor
public class LinkUpdaterScheduler {
    private final LinkUpdaterService linkUpdaterService;

    @Scheduled(fixedDelayString = "#{@scheduler.interval.toMillis()}")
    private void update() {
        try {
            linkUpdaterService.update();
        } catch (RuntimeException e) {
            log.error("Error on updating links: " + e.getMessage(), e);
        }

    }
}
