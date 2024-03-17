package edu.java.scrapper.dao;

import edu.java.scrapper.model.Chat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ChatDAO {

    private final JdbcTemplate jdbcTemplate;

    public ChatDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(Chat chat) {
        String sql = "INSERT INTO chats (id) VALUES (?)";
        jdbcTemplate.update(sql, chat.id());
    }

    public List<Chat> findAll() {
        String sql = "SELECT * FROM chats";
        return jdbcTemplate.queryForList(sql, Long.class)
            .stream()
            .map(Chat::new)
            .collect(Collectors.toList());
    }

    public void remove(Long id) {
        String sql = "DELETE FROM chats WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Chat> findById(Long id) {
        String sql = "SELECT id FROM chats WHERE id = ?";
        try {
            Long chatId = jdbcTemplate.queryForObject(sql, Long.class, id);
            return Optional.ofNullable(chatId).map(Chat::new);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
