package edu.java.scrapper.services.impls;

import edu.java.scrapper.clients.ClientEntityResponse;
import edu.java.scrapper.clients.github.GithubClient;
import edu.java.scrapper.clients.stackoverflow.StackOverflowClient;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.services.LinkUpdateCheckerService;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class LinkUpdateCheckerServiceImpl implements LinkUpdateCheckerService {

    private final GithubClient githubClient;
    private final StackOverflowClient stackOverflowClient;

    @Override
    public LocalDateTime lastUpdatedAtForLink(URI uri) throws UnsupportedLinkException {
        ClientEntityResponse entityResponse = switch (uri.getHost()) {
            case "github.com" -> githubClient.getRepository(getGithubRepository(uri));
            case "stackoverflow.com" -> stackOverflowClient.getQuestions(getStackOverflowQuestionId(uri));
            default -> throw new UnsupportedLinkException("Unknown host");
        };
        return entityResponse.getUpdatedAt().toLocalDateTime();
    }

    private Long getStackOverflowQuestionId(URI uri) throws UnsupportedLinkException {
        Path path = Path.of(uri.getPath());
        if (path.getNameCount() < 2 || !path.getName(0).toString().equals("questions")) {
            throw new UnsupportedLinkException("Require stackoverflow.com/questions/{questionId}/...");
        }
        return Long.parseLong(path.getName(1).toString());
    }

    private GithubClient.GithubRepository getGithubRepository(URI uri) throws UnsupportedLinkException {
        Path path = Path.of(uri.getPath());
        if (path.getNameCount() != 2) {
            throw new UnsupportedLinkException("Require github.com/{owner}/{repo}");
        }
        return new GithubClient.GithubRepository(path.getName(0).toString(), path.getName(1).toString());
    }
}
