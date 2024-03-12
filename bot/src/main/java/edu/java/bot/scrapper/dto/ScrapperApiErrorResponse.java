package edu.java.bot.scrapper.dto;

import java.util.List;

public record ScrapperApiErrorResponse(String description, String code, String exceptionName, String exceptionMessage,
                                       List<String> stacktrace) {
}
