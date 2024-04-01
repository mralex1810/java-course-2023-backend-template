package edu.java.scrapper.services;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.exceptions.AlreadyRegisteredChatException;
import edu.java.scrapper.exceptions.ChatNotFoundException;
import edu.java.scrapper.model.jpa.JpaChat;
import edu.java.scrapper.repository.JpaChatRepository;
import edu.java.scrapper.services.impls.jpa.JpaTgChatServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JpaTgChatServiceImplTest extends IntegrationTest {
    @Autowired
    JpaChatRepository jpaChatRepository;
    JpaTgChatServiceImpl jpaTgChatService;

    @BeforeEach
    public void setUp() {
        jpaTgChatService = new JpaTgChatServiceImpl(jpaChatRepository);
    }

    @AfterEach
    public void tearDown() {
        jpaChatRepository.deleteAll();
    }

    @Test
    public void registerChat_NewChat_Successful() throws AlreadyRegisteredChatException {
        // given
        long newChatId = 100;

        // when
        jpaTgChatService.register(newChatId);

        // then
        assertTrue(jpaChatRepository.findById(newChatId).isPresent());
    }

    @Test
    public void registerChat_AlreadyRegistered_ThrowsException() {
        // given
        long existingChatId = 101;
        JpaChat chat = new JpaChat();
        chat.setId(existingChatId);
        jpaChatRepository.save(chat);

        // then
        assertThrows(AlreadyRegisteredChatException.class, () -> jpaTgChatService.register(existingChatId));
    }

    @Test
    public void unregisterChat_ExistingChat_Successful() throws ChatNotFoundException {
        // given
        long existingChatId = 102;
        JpaChat chat = new JpaChat();
        chat.setId(existingChatId);
        jpaChatRepository.save(chat);

        // when
        jpaTgChatService.unregister(existingChatId);

        // then
        assertFalse(jpaChatRepository.findById(existingChatId).isPresent());
    }

    @Test
    public void unregisterChat_NotFound_ThrowsException() {
        // given
        long nonExistingChatId = 103;

        // then
        assertThrows(ChatNotFoundException.class, () -> jpaTgChatService.unregister(nonExistingChatId));
    }

}
