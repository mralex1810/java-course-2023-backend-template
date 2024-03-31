package edu.java.scrapper.configuration;

import edu.java.scrapper.clients.bot.BotClient;
import edu.java.scrapper.clients.bot.BotClientImpl;
import edu.java.scrapper.clients.github.GithubClient;
import edu.java.scrapper.clients.github.GithubClientImpl;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClientImpl;
import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.util.retry.Retry;

@Configuration
public class ClientConfiguration {

    @Value("${github.api.baseUrl:https://api.github.com}")
    private String githubBaseUrl;

    @Value("${stackoverflow.api.baseUrl:https://api.stackexchange.com}")
    private String stackoverflowBaseUrl;

    @Value("${bot.api.baseUrl:http://localhost:8081}")
    private String botBaseUrl;

    @Value("${retry.maxAttempts:3}")
    private long retryMaxAttempts;

    @Value("${retry.initialBackoff:1000}")
    private long retryInitialBackoff;

    @Value("${retry.retryableStatusCodes:504,500}")
    private List<Integer> retryableStatusCodes;

    @Value("${retry.strategy:CONSTANT}")
    private RetryStrategy retryStrategy;
    @Bean
    public GithubClient githubWebClient(
        Retry retry
    ) {
        return new GithubClientImpl(
            WebClient.builder()
                .baseUrl(githubBaseUrl)
                .build(),
            retry
        );
    }

    @Bean
    public StackOverflowClient stackoverflowWebClient(
        Retry retry
    ) {
        return new StackOverflowClientImpl(
            WebClient.builder()
                .baseUrl(stackoverflowBaseUrl)
                .build(),
            retry
        );
    }

    @Bean
    public BotClient botClient(
        Retry retry
    ) {
        return new BotClientImpl(
            WebClient.builder()
                .baseUrl(botBaseUrl)
                .build(),
            retry
        );
    }

    @Bean
    public Retry getRetryConfig() {
        var retry = switch (retryStrategy) {
            case CONSTANT -> Retry.fixedDelay(retryMaxAttempts, Duration.ofMillis(retryInitialBackoff));
            case EXPONENTIAL -> Retry.backoff(retryMaxAttempts, Duration.ofMillis(retryInitialBackoff))
                .jitter(1.0);
            case LINEAR -> Retry.backoff(retryMaxAttempts, Duration.ofMillis(retryInitialBackoff));
        };

        retry = retry.filter((resEx) -> {
                if (resEx instanceof ResponseStatusException) {
                    return retryableStatusCodes.contains(((ResponseStatusException) resEx).getStatusCode().value());
                }
                return false;
            })
            .onRetryExhaustedThrow((spec, signal) -> signal.failure());

        return retry;
    }

    public enum RetryStrategy {
        CONSTANT,
        LINEAR,
        EXPONENTIAL
    }
}
