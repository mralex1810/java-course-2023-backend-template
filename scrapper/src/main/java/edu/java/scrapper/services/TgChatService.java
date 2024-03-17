package edu.java.scrapper.services;

import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;

public interface TgChatService {
    void register(long tgChatId) throws AlreadyRegisteredChatException;

    void unregister(long tgChatId) throws ChatNotFoundException;
}
