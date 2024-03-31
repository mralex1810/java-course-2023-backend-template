package edu.java.scrapper.services.impls;

import edu.java.scrapper.clients.bot.BotClient;
import edu.java.scrapper.clients.bot.dto.BotLinkUpdateRequest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.services.TgChatService;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BotNotifyServiceImpl implements edu.java.scrapper.services.BotNotifyService {
    private final BotClient botClient;
    private final TgChatService tgChatService;

    @Override
    public void tryNotifyBot(
        LocalDateTime previousLastUpdatedAt,
        LocalDateTime lastUpdatedAt,
        Long linkId,
        String uri
    ) {
        if (previousLastUpdatedAt.equals(lastUpdatedAt)) {
            return;
        }
        botClient.postLinkUpdate(new BotLinkUpdateRequest(
            linkId,
            uri,
            "New update time: " + lastUpdatedAt,
            tgChatService.findAllRegisteredLink(linkId).stream().map(Chat::id).toList()
        ));
    }
}
