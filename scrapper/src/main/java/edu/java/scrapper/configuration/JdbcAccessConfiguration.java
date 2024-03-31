package edu.java.scrapper.configuration;

import edu.java.scrapper.dao.ChatDAO;
import edu.java.scrapper.dao.ChatLinkSettingDAO;
import edu.java.scrapper.dao.LinkDAO;
import edu.java.scrapper.services.BotNotifyService;
import edu.java.scrapper.services.LinkService;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import edu.java.scrapper.services.LinkUpdaterService;
import edu.java.scrapper.services.TgChatService;
import edu.java.scrapper.services.impls.jdbc.JdbcLinkServiceImpl;
import edu.java.scrapper.services.impls.jdbc.JdbcLinkUpdaterServiceImpl;
import edu.java.scrapper.services.impls.jdbc.JdbcTgChatServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
public class JdbcAccessConfiguration {
    @Bean
    public LinkService linkService(
        LinkDAO linkDAO,
        ChatDAO chatDAO,
        ChatLinkSettingDAO chatLinkSettingDAO,
        LinkUpdateCheckerService linkUpdateCheckerService

    ) {
        return new JdbcLinkServiceImpl(linkDAO, chatDAO, chatLinkSettingDAO, linkUpdateCheckerService);
    }

    @Bean
    public LinkUpdaterService linkUpdaterService(
        LinkDAO linkDAO,
        ApplicationConfig.LinkCheckProperties linkCheckProperties,
        LinkUpdateCheckerService linkUpdateCheckerService,
        BotNotifyService botNotifyService
    ) {
        return new JdbcLinkUpdaterServiceImpl(linkDAO, linkCheckProperties, linkUpdateCheckerService, botNotifyService);
    }

    @Bean
    public TgChatService tgChatService(
        ChatDAO chatDAO,
        ChatLinkSettingDAO chatLinkSettingDAO

    ) {
        return new JdbcTgChatServiceImpl(chatDAO, chatLinkSettingDAO);
    }
}
