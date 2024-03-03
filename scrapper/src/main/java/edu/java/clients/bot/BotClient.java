package edu.java.clients.bot;

import edu.java.clients.bot.dto.BotLinkUpdateRequest;

public interface BotClient {

    Void postLinkUpdate(BotLinkUpdateRequest linkUpdate);
}
