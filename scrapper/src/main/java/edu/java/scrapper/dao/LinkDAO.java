package edu.java.scrapper.dao;

import edu.java.scrapper.model.Link;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class LinkDAO {

    private final JdbcTemplate jdbcTemplate;

    @VisibleForTesting
    static final RowMapper<Link> linkRowMapper = (rs, rowNum) -> new Link(
        rs.getLong("id"),
        rs.getString("uri"),
        rs.getTimestamp("link_updated_at").toLocalDateTime(),
        rs.getTimestamp("link_checked_at").toLocalDateTime(),
        rs.getTimestamp("created_at").toLocalDateTime(),
        rs.getString("created_by")
    );

    public LinkDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(Link link) {
        String sql = """
            INSERT INTO links (uri, link_updated_at, link_checked_at, created_at, created_by)
            VALUES (?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(
            sql,
            link.uri(),
            link.linkUpdatedAt(),
            link.linkCheckedAt(),
            link.createdAt(),
            link.createdBy()
        );
    }

    public List<Link> findAll() {
        String sql = "SELECT * FROM links";
        return jdbcTemplate.query(sql, linkRowMapper);
    }

    public void remove(Long id) {
        String sql = "DELETE FROM links WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public Optional<Link> findByUrl(String url) {
        String sql = """
            SELECT id, uri, link_updated_at, link_checked_at, created_at, created_by
            FROM links
            WHERE uri = ?
            """;
        return jdbcTemplate.query(sql, linkRowMapper, url).stream().findFirst();
    }

    public void updateLinkMeta(Long id, LocalDateTime linkUpdatedAt, LocalDateTime linkCheckedAt) {
        String sql = """
            UPDATE links
            SET link_updated_at = ?, link_checked_at = ?
            WHERE id = ?
            """;
        jdbcTemplate.update(sql, linkUpdatedAt, linkCheckedAt, id);
    }

    public List<Link> findAllCheckedBefore(LocalDateTime dateTime) {
        String sql = """
            SELECT id, uri, link_updated_at, link_checked_at, created_at, created_by
            FROM links
            WHERE link_checked_at < ?
            """;
        return jdbcTemplate.query(sql, linkRowMapper, dateTime);
    }
}
