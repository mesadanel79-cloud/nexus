package application.domain.exceptions;

/**
 * Base exception for every business exception of the NexusMarket domain.
 * Business exceptions belong exclusively to the domain layer.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }
}