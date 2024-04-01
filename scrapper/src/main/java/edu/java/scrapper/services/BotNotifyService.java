package edu.java.scrapper.services;

import java.time.LocalDateTime;

public interface BotNotifyService {
    void tryNotifyBot(LocalDateTime previousLastUpdatedAt, LocalDateTime lastUpdatedAt, Long linkId, String uri);
}
