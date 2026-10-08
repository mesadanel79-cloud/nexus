package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when a Transfer cannot be
 * executed because a domain validation failed. The whole operation is
 * rejected and no partial balance mutation persists.
 */
public class InvalidTransferException extends DomainException {

    public InvalidTransferException(String message) {
        super(message);
    }
}