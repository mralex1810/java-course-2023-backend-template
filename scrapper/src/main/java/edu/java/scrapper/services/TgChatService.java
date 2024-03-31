package edu.java.scrapper.services;

import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.model.Chat;
import java.util.List;

public interface TgChatService {
    void register(long tgChatId) throws AlreadyRegisteredChatException;

    void unregister(long tgChatId) throws ChatNotFoundException;

    List<Chat> findAllRegisteredLink(long tgChatId);
}
