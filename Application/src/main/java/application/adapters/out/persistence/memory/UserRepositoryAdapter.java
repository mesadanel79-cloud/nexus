package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.SystemUser;
import application.domain.ports.out.UserRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * UserRepository port. Adapters implement ports but never define business
 * rules.
 */
@Component
public class UserRepositoryAdapter implements UserRepository {

    private final Map<String, SystemUser> usersByUsername =
            new ConcurrentHashMap<>();
    private final Map<String, String> usernameByPersonId =
            new ConcurrentHashMap<>();

    @Override
    public SystemUser save(SystemUser user) {
        usersByUsername.put(user.getUsername(), user);
        usernameByPersonId.put(user.getPerson().getIdentifier(),
                user.getUsername());
        return user;
    }

    @Override
    public Optional<SystemUser> findByUsername(String username) {
        return Optional.ofNullable(usersByUsername.get(username));
    }

    @Override
    public Optional<SystemUser> findByPersonId(String personId) {
        return Optional.ofNullable(usernameByPersonId.get(personId))
                .map(usersByUsername::get);
    }

    @Override
    public List<SystemUser> findAll() {
        return List.copyOf(usersByUsername.values());
    }
}