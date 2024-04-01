package edu.java.scrapper.controllers.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {
}
