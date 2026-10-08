package application.domain.ports.in;

import application.domain.models.SystemUser;

/**
 * Input Port (User and Authentication): creates a system user representing
 * an internal platform employee. Restricted to the PLATFORM_ADMIN role.
 */
public interface RegisterEmployeeUserUseCase {

    SystemUser registerEmployeeUser(String administratorId, String roleCode,
                                    String identifier, String fullName,
                                    String email, String username,
                                    String rawPassword);
}