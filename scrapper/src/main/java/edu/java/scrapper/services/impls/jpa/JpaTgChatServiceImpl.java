package edu.java.scrapper.services.impls.jpa;

import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.model.jpa.JpaChat;
import edu.java.scrapper.repository.JpaChatRepository;
import edu.java.scrapper.services.TgChatService;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class JpaTgChatServiceImpl implements TgChatService {

    private final JpaChatRepository jpaChatRepository;

    @Transactional
    public void register(long tgChatId) throws AlreadyRegisteredChatException {
        if (jpaChatRepository.findById(tgChatId).isEmpty()) {
            JpaChat chat = new JpaChat();
            chat.setId(tgChatId);
            jpaChatRepository.save(chat);
        } else {
            throw new AlreadyRegisteredChatException(String.format("Chat ID %d already exists", tgChatId));
        }
    }

    @Transactional
    public void unregister(long tgChatId) throws ChatNotFoundException {
        if (jpaChatRepository.findById(tgChatId).isPresent()) {
            jpaChatRepository.deleteById(tgChatId);
        } else {
            throw new ChatNotFoundException(String.format("Chat ID %d not found", tgChatId));
        }
    }
}
