package edu.java.scrapper.clients.bot;

import edu.java.scrapper.clients.bot.dto.BotApiErrorResponse;
import edu.java.scrapper.clients.bot.dto.BotLinkUpdateRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

public class BotClientImpl implements BotClient {

    private final WebClient webClient;
    private final Retry retry;

    public BotClientImpl(WebClient webClient, Retry retry) {
        this.webClient = webClient;
        this.retry = retry;
    }

    private <T> Mono<T> toResponseBodyMono(ClientResponse clientResponse, Class<T> clazz) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(clazz);
        } else {
            return clientResponse.bodyToMono(BotApiErrorResponse.class)
                .flatMap(apiErrorResponse -> Mono.error(new ResponseStatusException(
                    clientResponse.statusCode(),
                    apiErrorResponse.description()
                )));
        }
    }

    @Override
    public Void postLinkUpdate(BotLinkUpdateRequest linkUpdate) {
        return webClient.post()
            .uri("/updates")
            .bodyValue(linkUpdate)
            .exchangeToMono(clientResponse -> toResponseBodyMono(clientResponse, Void.class))
            .retryWhen(retry)
            .block();
    }
}
