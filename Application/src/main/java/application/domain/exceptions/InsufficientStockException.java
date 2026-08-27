package application.domain.exceptions;

/**
 * Thrown when a stock operation violates the business rules: negative
 * stock is never allowed under any circumstances, and damaged or missing
 * inventory cannot be reserved.
 */
public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String message) {
        super(message);
    }
}