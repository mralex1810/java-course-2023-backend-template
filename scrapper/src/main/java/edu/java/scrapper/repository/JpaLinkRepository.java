package edu.java.scrapper.repository;

import edu.java.scrapper.model.jpa.JpaLink;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<JpaLink, Long> {
    Optional<JpaLink> findByUri(String uri);

    List<JpaLink> findAllByLinkCheckedAtBefore(LocalDateTime dateTime);
}
