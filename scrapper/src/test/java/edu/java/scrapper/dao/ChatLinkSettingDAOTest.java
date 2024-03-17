package edu.java.scrapper.dao;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.ChatLinkSetting;
import edu.java.scrapper.model.Link;
import java.time.LocalDateTime;
import java.util.List;
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
public class ChatLinkSettingDAOTest extends IntegrationTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ChatLinkSettingDAO chatLinkSettingDAO;
    private ChatDAO chatDAO;
    private LinkDAO linkDAO;

    private ChatLinkSetting chatLinkSetting;
    private Chat chat;
    private Link link;

    @BeforeEach
    void setUp() {
        chatLinkSettingDAO = new ChatLinkSettingDAO(jdbcTemplate);
        chatDAO = new ChatDAO(jdbcTemplate);
        linkDAO = new LinkDAO(jdbcTemplate);

        chat = new Chat(1L);
        link = new Link(
            2L,
            "http://example.com",
            LocalDateTime.now(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            "test"
        );

        chatDAO.add(chat);
        linkDAO.add(link);
        link = linkDAO.findAll().getFirst();

        chatLinkSetting = new ChatLinkSetting(link.id(), chat.id());
    }

    @Test
    @Transactional
    @Rollback
    public void addAndFindTest() {
        chatLinkSettingDAO.add(chatLinkSetting);

        assertTrue(chatLinkSettingDAO.existsByLinkIdAndChatId(chatLinkSetting.linkId(), chatLinkSetting.chatId()));
    }

    @Test
    @Transactional
    @Rollback
    public void findAllTest() {
        chatLinkSettingDAO.add(chatLinkSetting);
        List<ChatLinkSetting> settingsList = chatLinkSettingDAO.findAll();

        assertFalse(settingsList.isEmpty());
        assertTrue(settingsList.contains(chatLinkSetting));
    }

    @Test
    @Transactional
    @Rollback
    public void removeTest() {
        chatLinkSettingDAO.add(chatLinkSetting);
        chatLinkSettingDAO.remove(chatLinkSetting.linkId(), chatLinkSetting.chatId());

        assertFalse(chatLinkSettingDAO.existsByLinkIdAndChatId(chatLinkSetting.linkId(), chatLinkSetting.chatId()));
    }

    @Test
    @Transactional
    @Rollback
    public void findAllByChatIdTest() {
        chatLinkSettingDAO.add(chatLinkSetting);
        List<Link> links = chatLinkSettingDAO.findAllByChatId(chatLinkSetting.chatId());

        assertEquals(links.size(), 1);
        assertEquals(link, links.getFirst());
    }

    @Test
    @Transactional
    @Rollback
    public void existsByLinkIdAndChatIdTest() {
        chatLinkSettingDAO.add(chatLinkSetting);
        boolean exists = chatLinkSettingDAO.existsByLinkIdAndChatId(chatLinkSetting.linkId(), chatLinkSetting.chatId());

        assertTrue(exists);
    }
}
