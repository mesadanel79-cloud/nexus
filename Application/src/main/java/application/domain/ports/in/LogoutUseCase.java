package application.domain.ports.in;

/**
 * Input Port (User and Authentication): terminates the authenticated user's
 * current session.
 */
public interface LogoutUseCase {

    void logout(String username);
}