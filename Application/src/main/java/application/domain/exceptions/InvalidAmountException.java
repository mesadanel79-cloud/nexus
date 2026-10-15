package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when a monetary amount is
 * null, zero or negative (bank-services: initial amount &gt;= 0,
 * deposit/withdraw/transfer amount &gt; 0).
 */
public class InvalidAmountException extends DomainException {

    public InvalidAmountException(String message) {
        super(message);
    }
}
