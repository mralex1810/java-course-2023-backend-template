package edu.java.scrapper.clients.bot.dto;

import java.util.List;

public record BotLinkUpdateRequest(Long id, String url, String description, List<Long> tgChatIds) {
}
