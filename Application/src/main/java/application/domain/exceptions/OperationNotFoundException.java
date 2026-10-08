package application.domain.exceptions;

/**
 * Domain Exception (Operation and Audit subdomain): raised when an
 * Operation cannot be resolved by its operation identifier.
 */
public class OperationNotFoundException extends DomainException {

    public OperationNotFoundException(String message) {
        super(message);
    }
}
