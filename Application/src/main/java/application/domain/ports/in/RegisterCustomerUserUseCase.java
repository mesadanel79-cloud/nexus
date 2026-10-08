package application.domain.ports.in;

import application.domain.models.SystemUser;

/**
 * Input Port (User and Authentication): creates a system user associated
 * with an existing Customer (buyer or seller) without requiring the user to
 * be an internal platform employee.
 */
public interface RegisterCustomerUserUseCase {

    SystemUser registerCustomerUser(String personId, String username,
                                    String rawPassword);
}