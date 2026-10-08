package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when a withdrawal or transfer
 * would produce a negative balance, which is never allowed.
 */
public class InsufficientBalanceException extends DomainException {

    public InsufficientBalanceException(String message) {
        super(message);
    }
}