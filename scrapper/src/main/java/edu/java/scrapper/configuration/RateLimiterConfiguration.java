package edu.java.scrapper.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import java.time.Duration;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimiterConfiguration {

    private final long capacity = 100;
    private final long refillTokens = 10;
    private final Duration refillDuration = Duration.ofMinutes(1);

    @Bean
    public Cache<String, byte[]> bucketCache() {
        return Caching.getCachingProvider().getCacheManager().createCache(
            "bucketCache",
            new MutableConfiguration<>()
        );
    }

    @Bean
    public BucketConfiguration bucketConfiguration() {
        Refill refill = Refill.greedy(refillTokens, refillDuration);
        Bandwidth limit = Bandwidth.classic(capacity, refill);
        return BucketConfiguration.builder()
            .addLimit(limit)
            .build();
    }
}
