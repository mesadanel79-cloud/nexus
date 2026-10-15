package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when an operation requires an
 * ACTIVA account but the account is blocked or closed
 * (bank-services: BankAccountBlockedException).
 */
public class BankAccountBlockedException extends DomainException {

    public BankAccountBlockedException(String message) {
        super(message);
    }
}
