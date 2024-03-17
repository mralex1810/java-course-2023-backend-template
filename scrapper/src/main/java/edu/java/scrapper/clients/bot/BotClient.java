package edu.java.scrapper.clients.bot;

import edu.java.scrapper.clients.bot.dto.BotLinkUpdateRequest;

public interface BotClient {

    Void postLinkUpdate(BotLinkUpdateRequest linkUpdate);
}
