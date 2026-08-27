package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * InternalStaff (Abstract) - Domain Model.
 *
 * Represents NexusMarket's internal staff responsible for administering,
 * operating and supervising the platform.
 *
 * Specialized into Administrator, LogisticsOperator and Supervisor.
 *
 * This class cannot be instantiated directly.
 */
public abstract class InternalStaff extends Person {

    protected InternalStaff(String identifier, String fullName, String email,
                            SystemRole role, UserStatus status) {
        super(identifier, fullName, email, role, status);
    }
}