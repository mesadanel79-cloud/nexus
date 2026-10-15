package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when the account currency
 * does not match the transfer currency (bank-services:
 * BankAccountCurrencyMismatchException).
 */
public class BankAccountCurrencyMismatchException extends DomainException {

    public BankAccountCurrencyMismatchException(String message) {
        super(message);
    }
}
