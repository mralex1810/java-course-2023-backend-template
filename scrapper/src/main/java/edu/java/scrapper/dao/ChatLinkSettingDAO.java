package edu.java.scrapper.dao;

import edu.java.scrapper.model.Chat;
import edu.java.scrapper.model.ChatLinkSetting;
import edu.java.scrapper.model.Link;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatLinkSettingDAO {

    private final JdbcTemplate jdbcTemplate;
    static final RowMapper<ChatLinkSetting> CHAT_LINK_SETTING_ROW_MAPPER = (rs, rowNum) -> new ChatLinkSetting(
        rs.getLong("link_id"),
        rs.getLong("chat_id")
    );

    private static final String SQL_ADD_CHAT_LINK_SETTING =
        "INSERT INTO chats_links_settings (link_id, chat_id) VALUES (?, ?)";
    private static final String SQL_FIND_ALL_CHAT_LINK_SETTINGS = "SELECT link_id, chat_id FROM chats_links_settings";
    private static final String SQL_REMOVE_CHAT_LINK_SETTING =
        "DELETE FROM chats_links_settings WHERE link_id = ? AND chat_id = ?";
    private static final String SQL_FIND_ALL_BY_CHAT_ID = """
        SELECT l.id, l.uri, l.link_updated_at, l.link_checked_at, l.created_at, l.created_by
        FROM links l
        INNER JOIN chats_links_settings cls ON l.id = cls.link_id
        WHERE cls.chat_id = ?
        """;

    private static final String SQL_FIND_ALL_BY_LINK_ID = """
        SELECT c.id
        FROM chats c
        INNER JOIN chats_links_settings cls ON c.id = cls.chat_id
        WHERE cls.link_id = ?
        """;
    private static final String SQL_EXISTS_BY_LINK_ID_AND_CHAT_ID =
        "SELECT COUNT(*) FROM chats_links_settings WHERE link_id = ? AND chat_id = ?";

    private static final String SQL_REMOVE_CHAT_LINK_SETTING_BY_TG_CHAT =
        "DELETE FROM chats_links_settings WHERE chat_id = ?";

    public void add(ChatLinkSetting chatLinkSetting) {
        jdbcTemplate.update(SQL_ADD_CHAT_LINK_SETTING, chatLinkSetting.linkId(), chatLinkSetting.chatId());
    }

    public List<ChatLinkSetting> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL_CHAT_LINK_SETTINGS, CHAT_LINK_SETTING_ROW_MAPPER);
    }

    public void remove(Long linkId, Long chatId) {
        jdbcTemplate.update(SQL_REMOVE_CHAT_LINK_SETTING, linkId, chatId);
    }

    public List<Link> findAllByChatId(Long chatId) {
        return jdbcTemplate.query(SQL_FIND_ALL_BY_CHAT_ID, LinkDAO.LINK_ROW_MAPPER, chatId);
    }

    public List<Chat> findAllByLinkId(Long linkId) {
        return jdbcTemplate.queryForList(SQL_FIND_ALL_BY_LINK_ID, Long.class, linkId).stream().map(Chat::new).toList();
    }

    public boolean existsByLinkIdAndChatId(Long linkId, Long chatId) {
        Integer count = jdbcTemplate.queryForObject(SQL_EXISTS_BY_LINK_ID_AND_CHAT_ID, Integer.class, linkId, chatId);
        return count != null && count > 0;
    }

    public void deleteByChatId(long tgChatId) {
        jdbcTemplate.update(SQL_REMOVE_CHAT_LINK_SETTING_BY_TG_CHAT, tgChatId);
    }
}
