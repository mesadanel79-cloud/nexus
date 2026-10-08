package application.domain.ports.in;

import application.domain.models.SystemUser;

/**
 * Input Port (User and Authentication): authenticates a system user with
 * their registered credentials and establishes an authenticated session.
 */
public interface LoginUseCase {

    SystemUser login(String username, String rawPassword);
}