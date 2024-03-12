package edu.java.bot.scrapper.dto;

import java.util.List;

public record ScrapperListLinksResponse(List<ScrapperLinkResponse> links, Integer size) {
}
