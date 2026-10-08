package application.domain.exceptions;

/**
 * Domain Exception (Operation and Audit subdomain): raised when an
 * Operation cannot be registered or consulted because its business
 * information is not valid (missing type, performing user or affected
 * asset).
 */
public class InvalidOperationException extends DomainException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
