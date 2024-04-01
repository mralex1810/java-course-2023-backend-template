package edu.java.scrapper.model;

import java.time.LocalDateTime;

public record Link(Long id, String uri, LocalDateTime linkUpdatedAt, LocalDateTime linkCheckedAt,
                   LocalDateTime createdAt, String createdBy) {

    public static Link create(String uri, LocalDateTime linkUpdatedAt, LocalDateTime linkCheckedAt, String createdBy) {
        return new Link(null, uri, linkUpdatedAt, linkCheckedAt, LocalDateTime.now(), createdBy);
    }
}
