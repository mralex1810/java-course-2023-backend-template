package edu.java.scrapper.dao;

import edu.java.scrapper.model.Chat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatDAO {

    private final JdbcTemplate jdbcTemplate;

    private static final String SQL_ADD_CHAT = "INSERT INTO chats (id) VALUES (?)";
    private static final String SQL_FIND_ALL_CHATS = "SELECT * FROM chats";
    private static final String SQL_REMOVE_CHAT = "DELETE FROM chats WHERE id = ?";
    private static final String SQL_FIND_BY_ID = "SELECT id FROM chats WHERE id = ?";

    public void add(Chat chat) {
        jdbcTemplate.update(SQL_ADD_CHAT, chat.id());
    }

    public List<Chat> findAll() {
        return jdbcTemplate.queryForList(SQL_FIND_ALL_CHATS, Long.class)
            .stream()
            .map(Chat::new)
            .collect(Collectors.toList());
    }

    public void remove(Long id) {
        jdbcTemplate.update(SQL_REMOVE_CHAT, id);
    }

    public Optional<Chat> findById(Long id) {
        try {
            Long chatId = jdbcTemplate.queryForObject(SQL_FIND_BY_ID, Long.class, id);
            return Optional.ofNullable(chatId).map(Chat::new);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
