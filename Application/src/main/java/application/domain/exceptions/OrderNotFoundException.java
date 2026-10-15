package application.domain.exceptions;

/**
 * Domain Exception (Order Management): raised when an Order cannot be
 * resolved by its order identifier.
 */
public class OrderNotFoundException extends DomainException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
