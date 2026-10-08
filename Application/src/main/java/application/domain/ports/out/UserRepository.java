package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.SystemUser;

/**
 * Output Port (User and Authentication subdomain): persistence contract for
 * system users.
 */
public interface UserRepository {

    SystemUser save(SystemUser user);

    Optional<SystemUser> findByUsername(String username);

    Optional<SystemUser> findByPersonId(String personId);

    List<SystemUser> findAll();
}