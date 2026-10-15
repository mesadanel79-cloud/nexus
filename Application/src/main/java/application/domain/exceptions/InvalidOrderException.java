package application.domain.exceptions;

/**
 * Domain Exception (Order Management): raised when an order lifecycle
 * transition violates the SDD rules (confirm / fulfill / close).
 */
public class InvalidOrderException extends DomainException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
