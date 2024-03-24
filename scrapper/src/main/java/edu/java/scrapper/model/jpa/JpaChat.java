package edu.java.scrapper.model.jpa;

import edu.java.scrapper.model.Chat;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "chats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JpaChat {
    @Id
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "chats_links_settings",
        joinColumns = @JoinColumn(name = "chat_id"),
        inverseJoinColumns = @JoinColumn(name = "link_id"))
    List<JpaLink> links;

    public void addLink(JpaLink link) {
        link.getChats().add(this);
        links.add(link);
    }

    public Chat convertToModel() {
        return new Chat(id);
    }
}
