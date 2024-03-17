package edu.java.scrapper.model;

import java.time.LocalDateTime;

public record Link(Long id, String uri, LocalDateTime linkUpdatedAt, LocalDateTime linkCheckedAt,
                   LocalDateTime createdAt, String createdBy) {
    public static Long EMPTY_ID = 0L;

    public static Link create(String uri, LocalDateTime linkUpdatedAt, LocalDateTime linkCheckedAt, String createdBy) {
        return new Link(EMPTY_ID, uri, linkUpdatedAt, linkCheckedAt, LocalDateTime.now(), createdBy);
    }
}
