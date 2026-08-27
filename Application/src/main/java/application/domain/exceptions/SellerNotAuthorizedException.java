package application.domain.exceptions;

/**
 * Thrown when a seller attempts to operate on resources that do not belong
 * to it, or when a participant attempts to manage information outside its
 * own functions (RG-03).
 */
public class SellerNotAuthorizedException extends DomainException {

    public SellerNotAuthorizedException(String message) {
        super(message);
    }
}