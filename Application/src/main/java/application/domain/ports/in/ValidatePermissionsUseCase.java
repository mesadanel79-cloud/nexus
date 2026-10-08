package application.domain.ports.in;

import application.domain.models.SystemUser;
import application.domain.valueobjects.OperationType;

/**
 * Input Port (Authorization subdomain): determines whether an
 * authenticated system user has permission to perform a specific business
 * operation according to the user's role and status.
 *
 * The authorization context is expressed with Domain Models: the user is
 * always received as a SystemUser, never as a primitive identifier or as a
 * raw role string.
 */
public interface ValidatePermissionsUseCase {

    /** True when the user may execute the requested operation type. */
    boolean validatePermissions(SystemUser user, OperationType operationType);

    /**
     * Validates the permission and fails with
     * UnauthorizedOperationException when the user is not authorized.
     */
    void authorize(SystemUser user, OperationType operationType);
}
