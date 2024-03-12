package edu.java.clients.bot.dto;

import java.util.List;

public record BotApiErrorResponse(String description, String code, String exceptionName, String exceptionMessage,
                                  List<String> stacktrace) {
}
