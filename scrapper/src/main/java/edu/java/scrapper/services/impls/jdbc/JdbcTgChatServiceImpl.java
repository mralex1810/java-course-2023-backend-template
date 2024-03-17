package edu.java.scrapper.services.impls.jdbc;

import edu.java.scrapper.dao.ChatDAO;
import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.services.TgChatService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class JdbcTgChatServiceImpl implements TgChatService {

    private final ChatDAO chatDAO;

    @Override
    @Transactional
    public void register(long tgChatId) throws AlreadyRegisteredChatException {
        if (chatDAO.findById(tgChatId).isEmpty()) {
            chatDAO.add(new Chat(tgChatId));
        } else {
            throw new AlreadyRegisteredChatException("Chat ID" + tgChatId + " already exists");
        }
    }

    @Override
    @Transactional
    public void unregister(long tgChatId) throws ChatNotFoundException {
        if (chatDAO.findById(tgChatId).isPresent()) {
            chatDAO.remove(tgChatId);
        } else {
            throw new ChatNotFoundException("Chat ID" + tgChatId + " not found");
        }
    }
}
