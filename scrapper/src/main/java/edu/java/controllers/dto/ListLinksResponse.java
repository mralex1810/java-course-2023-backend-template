package edu.java.controllers.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {
}
