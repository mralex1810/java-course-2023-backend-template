package edu.java.scrapper.dao;

import edu.java.scrapper.model.ChatLinkSetting;
import edu.java.scrapper.model.Link;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class ChatLinkSettingDAO {

    private final JdbcTemplate jdbcTemplate;
    static final RowMapper<ChatLinkSetting> CHAT_LINK_SETTING_ROW_MAPPER = (rs, rowNum) -> new ChatLinkSetting(
        rs.getLong("link_id"),
        rs.getLong("chat_id")
    );

    public ChatLinkSettingDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(ChatLinkSetting chatLinkSetting) {
        String sql = "INSERT INTO chats_links_settings (link_id, chat_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, chatLinkSetting.linkId(), chatLinkSetting.chatId());
    }

    public List<ChatLinkSetting> findAll() {
        String sql = "SELECT link_id, chat_id FROM chats_links_settings";
        return jdbcTemplate.query(sql, CHAT_LINK_SETTING_ROW_MAPPER);
    }

    public void remove(Long linkId, Long chatId) {
        String sql = "DELETE FROM chats_links_settings WHERE link_id = ? AND chat_id = ?";
        jdbcTemplate.update(sql, linkId, chatId);
    }

    public List<Link> findAllByChatId(Long chatId) {
        String sql = """
            SELECT l.id, l.uri, l.link_updated_at, l.link_checked_at, l.created_at, l.created_by
            FROM links l
            INNER JOIN chats_links_settings cls ON l.id = cls.link_id
            WHERE cls.chat_id = ?
            """;
        return jdbcTemplate.query(sql, LinkDAO.LINK_ROW_MAPPER, chatId);
    }

    public boolean existsByLinkIdAndChatId(Long linkId, Long chatId) {
        String sql = "SELECT COUNT(*) FROM chats_links_settings WHERE link_id = ? AND chat_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, linkId, chatId);
        return count != null && count > 0;
    }
}
