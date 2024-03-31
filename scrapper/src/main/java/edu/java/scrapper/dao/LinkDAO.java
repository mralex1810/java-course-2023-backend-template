package edu.java.scrapper.dao;

import edu.java.scrapper.model.Link;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LinkDAO {

    private final JdbcTemplate jdbcTemplate;

    @VisibleForTesting
    static final RowMapper<Link> LINK_ROW_MAPPER = (rs, rowNum) -> new Link(
        rs.getLong("id"),
        rs.getString("uri"),
        rs.getTimestamp("link_updated_at").toLocalDateTime(),
        rs.getTimestamp("link_checked_at").toLocalDateTime(),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getString("created_by")
    );

    private static final String SQL_ADD_LINK = """
        INSERT INTO links (uri, link_updated_at, link_checked_at, created_at, created_by)
        VALUES (?, ?, ?, ?, ?)
        """;
    private static final String SQL_FIND_ALL_LINKS = "SELECT * FROM links";
    private static final String SQL_REMOVE_LINK = "DELETE FROM links WHERE id = ?";
    private static final String SQL_FIND_BY_URL = """
        SELECT id, uri, link_updated_at, link_checked_at, created_at, created_by
        FROM links
        WHERE uri = ?
        """;
    private static final String SQL_UPDATE_LINK_META = """
        UPDATE links
        SET link_updated_at = ?, link_checked_at = ?
        WHERE id = ?
        """;
    private static final String SQL_FIND_ALL_CHECKED_BEFORE = """
        SELECT id, uri, link_updated_at, link_checked_at, created_at, created_by
        FROM links
        WHERE link_checked_at < ?
        """;

    public void add(Link link) {
        jdbcTemplate.update(
            SQL_ADD_LINK,
            link.uri(),
            link.linkUpdatedAt(),
            link.linkCheckedAt(),
            link.createdAt(),
            link.createdBy()
        );
    }

    public List<Link> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL_LINKS, LINK_ROW_MAPPER);
    }

    public void remove(Long id) {
        jdbcTemplate.update(SQL_REMOVE_LINK, id);
    }

    public Optional<Link> findByUrl(String url) {
        return jdbcTemplate.query(SQL_FIND_BY_URL, LINK_ROW_MAPPER, url).stream().findFirst();
    }

    public void updateLinkMeta(Long id, LocalDateTime linkUpdatedAt, LocalDateTime linkCheckedAt) {
        jdbcTemplate.update(SQL_UPDATE_LINK_META, linkUpdatedAt, linkCheckedAt, id);
    }

    public List<Link> findAllCheckedBefore(LocalDateTime dateTime) {
        return jdbcTemplate.query(SQL_FIND_ALL_CHECKED_BEFORE, LINK_ROW_MAPPER, dateTime);
    }
}
