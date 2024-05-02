package edu.java.scrapper.clients.github;

import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

public class GithubClientImpl implements GithubClient {
    private final WebClient githubWebClient;
    private final Retry retry;

    public GithubClientImpl(WebClient githubWebClient, Retry retry) {
        this.githubWebClient = githubWebClient;
        this.retry = retry;
    }

    @Override
    public GithubRepositoryResponse getRepository(GithubRepository githubRepository) {
        return githubWebClient.get()
            .uri("/repos/" + githubRepository.owner() + "/" + githubRepository.repo())
            .retrieve()
            .bodyToMono(GithubRepositoryResponse.class)
            .retryWhen(retry)
            .block();
    }
}
