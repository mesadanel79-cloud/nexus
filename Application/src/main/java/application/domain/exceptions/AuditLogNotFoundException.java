package application.domain.exceptions;

/**
 * Domain Exception (Operation and Audit subdomain): raised when an
 * AuditLog record cannot be resolved by its audit identifier.
 */
public class AuditLogNotFoundException extends DomainException {

    public AuditLogNotFoundException(String message) {
        super(message);
    }
}
