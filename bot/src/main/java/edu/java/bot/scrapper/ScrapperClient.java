package edu.java.bot.scrapper;

import edu.java.bot.scrapper.dto.ScrapperAddLinkRequest;
import edu.java.bot.scrapper.dto.ScrapperLinkResponse;
import edu.java.bot.scrapper.dto.ScrapperListLinksResponse;
import edu.java.bot.scrapper.dto.ScrapperRemoveLinkRequest;

public interface ScrapperClient {
    Void postChat(long id);

    Void deleteChat(long id);

    ScrapperLinkResponse postLink(long chatId, ScrapperAddLinkRequest addLinkRequest);

    ScrapperLinkResponse deleteLink(long chatId, ScrapperRemoveLinkRequest removeLinkRequest);

    ScrapperListLinksResponse getAllLinks(long chatId);
}
