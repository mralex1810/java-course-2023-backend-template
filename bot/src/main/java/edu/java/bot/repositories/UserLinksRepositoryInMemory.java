package edu.java.bot.repositories;

import edu.java.bot.model.Link;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserLinksRepositoryInMemory implements UserLinksRepository {

    private final Map<Long, List<Link>> data = new HashMap<>();

    @Override
    public List<Link> getLinksByUser(Long user) {
        return data.getOrDefault(user, List.of());
    }

    @Override
    public boolean addUserLinks(Long user, Link link) {
        var linksList = data.computeIfAbsent(user, (k) -> new ArrayList<>());
        return linksList.add(link);
    }

    @Override
    public boolean removeUserLinks(Long user, Link link) {
        var linksList = data.computeIfAbsent(user, (k) -> new ArrayList<>());
        return linksList.remove(link);
    }
}
