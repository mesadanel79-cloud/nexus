package application.domain.ports.in;

import application.domain.models.SystemUser;

/**
 * Input Port (User and Authentication): changes the status of a system
 * user's access to the application (activate, deactivate, block).
 */
public interface ChangeUserStatusUseCase {

    SystemUser changeUserStatus(String username, String statusCode);
}