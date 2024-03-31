package edu.java.scrapper.clients.stackoverflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

public class StackOverflowClientImpl implements StackOverflowClient {
    private final WebClient stackOverflowWebClient;
    private final Retry retry;

    public StackOverflowClientImpl(WebClient stackOverflowWebClient, Retry retry) {
        this.stackOverflowWebClient = stackOverflowWebClient;
        this.retry = retry;
    }

    @Override
    public StackOverflowQuestionResponse getQuestions(Long stackOverflowQuestionId) {
        return stackOverflowWebClient.get()
            .uri("/2.3/questions/" + stackOverflowQuestionId)
            .retrieve()
            .bodyToMono(StackOverflowFullResponse.class)
            .retryWhen(retry)
            .map(StackOverflowFullResponse::questions)
            .map(List::getFirst)
            .block();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private record StackOverflowFullResponse(
        @JsonProperty("items") List<StackOverflowQuestionResponse> questions
    ) {

    }

}
