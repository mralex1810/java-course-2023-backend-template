package edu.java.scrapper.services;

import edu.java.scrapper.exceptions.UnsupportedLinkException;
import java.net.URI;
import java.time.LocalDateTime;

public interface LinkUpdateCheckerService {
    LocalDateTime lastUpdatedAtForLink(URI uri) throws UnsupportedLinkException;
}
