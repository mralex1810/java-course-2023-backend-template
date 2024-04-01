package edu.java.scrapper.configuration;

import edu.java.scrapper.clients.bot.BotClient;
import edu.java.scrapper.repository.JpaChatRepository;
import edu.java.scrapper.repository.JpaLinkRepository;
import edu.java.scrapper.services.LinkService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.LinkUpdaterService;
import edu.java.scrapper.services.TgChatService;
import edu.java.scrapper.services.impls.jpa.JpaLinkServiceImpl;
import edu.java.scrapper.services.impls.jpa.JpaLinkUpdaterServiceImpl;
import edu.java.scrapper.services.impls.jpa.JpaTgChatServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jpa")
public class JpaAccessConfiguration {
    @Bean
    public LinkService linkService(
        JpaLinkRepository jpaLinkRepository,
        JpaChatRepository jpaChatRepository,
        LinkUpdateCheckerService linkUpdateCheckerService

    ) {
        return new JpaLinkServiceImpl(jpaLinkRepository, jpaChatRepository, linkUpdateCheckerService);
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(
        JpaLinkRepository jpaLinkRepository,
        ApplicationConfig.LinkCheckProperties linkCheckProperties,
        LinkUpdateCheckerService linkUpdateCheckerService,
        BotClient botClient
    ) {
        return new JpaLinkUpdaterServiceImpl(
            jpaLinkRepository,
            linkCheckProperties,
            linkUpdateCheckerService,
            botClient
        );
    }

    @Bean
    public TgChatService tgChatService(
        JpaChatRepository jpaChatRepository
    ) {
        return new JpaTgChatServiceImpl(jpaChatRepository);
    }
}
