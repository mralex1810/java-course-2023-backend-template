package edu.java.scrapper.services;

import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.exceptions.LinkNotFoundException;
import edu.java.scrapper.exceptions.UnsupportedLinkException;
import edu.java.scrapper.model.Link;
import java.net.URI;
import java.util.Collection;

public interface LinkService {
    Link add(long tgChatId, URI url) throws UnsupportedLinkException;

    Link remove(long tgChatId, URI url) throws LinkNotFoundException, ChatNotFoundException;

    Collection<Link> listAll(long tgChatId);
}
