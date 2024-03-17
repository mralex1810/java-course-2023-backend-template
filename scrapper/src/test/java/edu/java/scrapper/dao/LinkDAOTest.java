package edu.java.scrapper.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import edu.java.scrapper.IntegrationTest;
import edu.java.scrapper.model.Link;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
public class LinkDAOTest extends IntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private LinkDAO linkRepository;

    private Link testLink;
    private LocalDateTime time;

    @BeforeEach
    void setUp() {
        time = LocalDateTime.now();
        testLink = new Link(
                null,
                "https://test.com",
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                "created_by"
        );
        linkRepository = new LinkDAO(jdbcTemplate);
        jdbcTemplate.update(
            "INSERT INTO links (uri, link_updated_at, link_checked_at, created_at, created_by) VALUES (?,?,?,?,?)",
            testLink.uri(),
            testLink.linkUpdatedAt(),
            testLink.linkCheckedAt(),
            testLink.createdAt(),
            testLink.createdBy()
        );
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM links");
    }

    @Test
    @Transactional
    @Rollback
    void addTest() {
        time = LocalDateTime.now();
        Link newLink = new Link(
            null,
            "https://newTest.com",
                time,
                time,
                time,
            "created_by"
        );
        linkRepository.add(newLink);

        List<Link> links = jdbcTemplate.query("SELECT * FROM links", linkRepository.LINK_ROW_MAPPER);
        Assertions.assertTrue(links.stream().anyMatch(link -> link.uri().equals(newLink.uri())));
    }

    @Test
    @Transactional
    @Rollback
    void removeTest() {
        List<Link> links = jdbcTemplate.query("SELECT * FROM links", linkRepository.LINK_ROW_MAPPER);
        Long idToRemove = links.getFirst().id();

        linkRepository.remove(idToRemove);

        links = jdbcTemplate.query("SELECT * FROM links", linkRepository.LINK_ROW_MAPPER);
        Assertions.assertTrue(links.stream().noneMatch(link -> link.id().equals(idToRemove)));
    }

    @Test
    @Transactional
    @Rollback
    void findAllTest() {
        List<Link> actual = linkRepository.findAll();
        List<Link> expected = jdbcTemplate.query("SELECT * FROM links", linkRepository.LINK_ROW_MAPPER);
        Assertions.assertEquals(expected.size(), actual.size());
    }

    @Test
    @Transactional
    @Rollback
    void findByUrlTest() {
        Optional<Link> actual = linkRepository.findByUrl("https://test.com");
        Link expected = jdbcTemplate.queryForObject(
                "SELECT * FROM links WHERE uri = ?", linkRepository.LINK_ROW_MAPPER, "https://test.com");
        Assertions.assertEquals(expected, actual.orElse(null));
    }

    @Test
    @Transactional
    @Rollback
    void updateLinkMetaTest() {
        LocalDateTime newLinkUpdatedAt = time.minusDays(1);
        LocalDateTime newLinkCheckedAt = time.minusDays(1);

        Link linkToUpdate = jdbcTemplate.queryForObject(
                "SELECT * FROM links WHERE uri = ?", linkRepository.LINK_ROW_MAPPER, "https://test.com");
        if (linkToUpdate != null) {
            linkRepository.updateLinkMeta(linkToUpdate.id(), newLinkUpdatedAt, newLinkCheckedAt);
            Link updatedLink = jdbcTemplate.queryForObject(
                    "SELECT * FROM links WHERE id = ?", linkRepository.LINK_ROW_MAPPER, linkToUpdate.id());
            Assertions.assertNotNull(updatedLink);
            Assertions.assertEquals(newLinkUpdatedAt, updatedLink.linkUpdatedAt());
            Assertions.assertEquals(newLinkCheckedAt, updatedLink.linkCheckedAt());
        }
    }

    @Test
    @Transactional
    @Rollback
    void findAllCheckedBeforeTest() {
        LocalDateTime checkedBefore = LocalDateTime.now();
        List<Link> actual = linkRepository.findAllCheckedBefore(checkedBefore);
        List<Link> expected = jdbcTemplate.query(
                "SELECT * FROM links WHERE link_checked_at < ?", linkRepository.LINK_ROW_MAPPER, checkedBefore);
        Assertions.assertEquals(expected.size(), actual.size());
    }
}
