package application.domain.exceptions;

/**
 * Domain Exception (Returns and Refunds Management): raised when a Refund
 * cannot be resolved by its refund identifier.
 */
public class RefundNotFoundException extends DomainException {

    public RefundNotFoundException(String message) {
        super(message);
    }
}
