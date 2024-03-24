package edu.java.scrapper.repository;

import edu.java.scrapper.model.jpa.JpaChat;
import edu.java.scrapper.model.jpa.JpaLink;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaChatRepository extends JpaRepository<JpaChat, Long> {
    @Query("SELECT c.links FROM chats c WHERE c.id = :chatId")
    List<JpaLink> findAllLinksByChatId(@Param("chatId") Long chatId);

    @Query("SELECT COUNT(c) > 0 FROM chats c JOIN c.links l WHERE l.id = :linkId AND c.id = :chatId")
    Boolean existsByLinkIdAndChatId(@Param("linkId") Long linkId, @Param("chatId") Long chatId);
}
