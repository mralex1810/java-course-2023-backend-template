package edu.java.scrapper.model.jpa;

import edu.java.scrapper.model.Link;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name = "links")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "chats")
public class JpaLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uri;
    private LocalDateTime linkUpdatedAt;
    private LocalDateTime linkCheckedAt;
    private LocalDateTime createdAt;
    private String createdBy;

    @ManyToMany(mappedBy = "links", fetch = FetchType.EAGER)
    private List<JpaChat> chats;

    public void addChat(JpaChat chat) {
        chat.getLinks().add(this);
        chats.add(chat);
    }

    public Link convertToModel() {
        return new Link(id, uri, linkUpdatedAt, linkCheckedAt, createdAt, createdBy);
    }

}
