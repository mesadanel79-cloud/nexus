package application.domain.exceptions;

/**
 * Domain Exception (Operation and Audit subdomain): raised when an
 * AuditLog record cannot be created because the historical traceability
 * information is not valid.
 */
public class InvalidAuditLogException extends DomainException {

    public InvalidAuditLogException(String message) {
        super(message);
    }
}
