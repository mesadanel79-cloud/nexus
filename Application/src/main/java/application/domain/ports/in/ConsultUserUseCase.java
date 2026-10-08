package application.domain.ports.in;

import application.domain.models.SystemUser;

/**
 * Input Port (User and Authentication): retrieves information about a system
 * user according to the permissions of the requesting user.
 */
public interface ConsultUserUseCase {

    SystemUser consultUser(String username);
}