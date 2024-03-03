package edu.java.bot.scrapper;

import edu.java.bot.scrapper.dto.ScrapperAddLinkRequest;
import edu.java.bot.scrapper.dto.ScrapperApiErrorResponse;
import edu.java.bot.scrapper.dto.ScrapperLinkResponse;
import edu.java.bot.scrapper.dto.ScrapperListLinksResponse;
import edu.java.bot.scrapper.dto.ScrapperRemoveLinkRequest;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

public class ScrapperClientImpl implements ScrapperClient {

    private final WebClient webClient;

    public ScrapperClientImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    private <T> Mono<T> toResponseBodyMono(ClientResponse clientResponse, Class<T> clazz) {
        if (clientResponse.statusCode().is2xxSuccessful()) {
            return clientResponse.bodyToMono(clazz);
        } else {
            return clientResponse.bodyToMono(ScrapperApiErrorResponse.class)
                .flatMap(apiErrorResponse -> Mono.error(new ResponseStatusException(
                    clientResponse.statusCode(),
                    apiErrorResponse.description()
                )));
        }
    }

    @Override
    public Void postChat(long id) {
        return webClient.post()
            .uri("/tg-chat/{id}", id)
            .exchangeToMono(clientResponse -> toResponseBodyMono(clientResponse, Void.class))
            .block();
    }

    @Override
    public Void deleteChat(long id) {
        return webClient.delete()
            .uri("/tg-chat/{id}", id)
            .exchangeToMono(clientResponse -> toResponseBodyMono(clientResponse, Void.class))
            .block();
    }

    @Override
    public ScrapperLinkResponse postLink(long chatId, ScrapperAddLinkRequest addLinkRequest) {
        return webClient.post()
            .uri("/links")
            .header("Tg-Chat-Id", Long.toString(chatId))
            .bodyValue(addLinkRequest)
            .exchangeToMono(clientResponse -> toResponseBodyMono(clientResponse, ScrapperLinkResponse.class))
            .block();
    }

    @Override
    public ScrapperLinkResponse deleteLink(long chatId, ScrapperRemoveLinkRequest removeLinkRequest) {
        return webClient.method(HttpMethod.DELETE)
            .uri("/links")
            .header("Tg-Chat-Id", Long.toString(chatId))
            .bodyValue(removeLinkRequest)
            .exchangeToMono(clientResponse -> toResponseBodyMono(clientResponse, ScrapperLinkResponse.class))
            .block();
    }

    @Override
    public ScrapperListLinksResponse getAllLinks(long chatId) {
        return webClient.get()
            .uri("/links")
            .header("Tg-Chat-Id", Long.toString(chatId))
            .exchangeToMono(clientResponse -> toResponseBodyMono(clientResponse, ScrapperListLinksResponse.class))
            .block();
    }
}
