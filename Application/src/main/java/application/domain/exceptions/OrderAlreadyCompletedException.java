package application.domain.exceptions;

/**
 * Thrown when an operation attempts to modify a completed order.
 *
 * Business rule: a completed order shall not be modified under any
 * circumstances.
 */
public class OrderAlreadyCompletedException extends DomainException {

    public OrderAlreadyCompletedException(String message) {
        super(message);
    }
}