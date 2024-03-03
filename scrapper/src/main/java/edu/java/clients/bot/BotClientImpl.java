package edu.java.clients.bot;

import edu.java.clients.bot.dto.BotApiErrorResponse;
import edu.java.clients.bot.dto.BotLinkUpdateRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class BotClientImpl implements BotClient {

    private final WebClient webClient;

    public BotClientImpl(WebClient webClient) {
        this.webClient = webClient;
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
            .block();
    }
}
