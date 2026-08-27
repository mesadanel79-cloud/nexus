package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Supervisor - Domain Model.
 *
 * Read-only, operational monitoring profile, without direct administration
 * permissions.
 *
 * Relationships:
 * - A Supervisor consults Order, Return and Refund for monitoring purposes.
 */
public class Supervisor extends InternalStaff {

    public Supervisor(String identifier, String fullName, String email,
                      UserStatus status) {
        super(identifier, fullName, email, SystemRole.SUPERVISOR, status);
    }
}