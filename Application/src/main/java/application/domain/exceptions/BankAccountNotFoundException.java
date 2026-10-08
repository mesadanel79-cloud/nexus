package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when a BankAccount cannot be
 * resolved by its account number.
 */
public class BankAccountNotFoundException extends DomainException {

    public BankAccountNotFoundException(String message) {
        super(message);
    }
}