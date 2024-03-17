package edu.java.scrapper.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ChatDAOTest extends IntegrationTest {

    @Autowired
    public JdbcTemplate jdbcTemplate;
    private ChatDAO chatDAO;
    private Chat chat;

    @BeforeEach
    void setUp() throws SQLException {
        chat = new Chat(1L);
        chatDAO = new ChatDAO(jdbcTemplate);
    }

    @Test
    @Transactional
    @Rollback
    public void addTest() {
        chatDAO.add(chat);

        Optional<Chat> foundChat = chatDAO.findById(chat.id());
        assertTrue(foundChat.isPresent());
        assertEquals(chat.id(), foundChat.get().id());
    }

    @Test
    @Transactional
    @Rollback
    public void findAllTest() {
        chatDAO.add(chat);

        List<Chat> chatList = chatDAO.findAll();
        assertFalse(chatList.isEmpty());
        assertTrue(chatList.contains(chat));
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        chatDAO.add(chat);
        chatDAO.remove(chat.id());

        Optional<Chat> foundChat = chatDAO.findById(chat.id());
        assertFalse(foundChat.isPresent());
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdTest() {
        chatDAO.add(chat);

        Optional<Chat> foundChat = chatDAO.findById(chat.id());
        assertTrue(foundChat.isPresent());
        assertEquals(chat.id(), foundChat.get().id());
    }
}
