package application.domain.exceptions;

/**
 * Domain Exception (Authorization subdomain): raised when an authenticated
 * system user is not authorized to execute a protected business operation
 * according to its role, status or the rules applicable to the affected
 * marketplace asset.
 *
 * Authorization never executes the business operation; it only determines
 * whether the operation may be initiated. This exception expresses the
 * negative authorization result.
 */
public class UnauthorizedOperationException extends DomainException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
