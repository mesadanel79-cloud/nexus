package application.domain.exceptions;

/**
 * Thrown when a return request does not satisfy the business rules: the
 * order must be delivered/completed and must belong to the requesting
 * buyer.
 */
public class InvalidReturnRequestException extends DomainException {

    public InvalidReturnRequestException(String message) {
        super(message);
    }
}