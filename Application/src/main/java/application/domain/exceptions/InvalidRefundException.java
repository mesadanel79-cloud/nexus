package application.domain.exceptions;

/**
 * Domain Exception (Returns and Refunds Management): raised when a refund
 * settlement transition violates the lifecycle PENDIENTE -&gt; PROCESADO |
 * RECHAZADO.
 */
public class InvalidRefundException extends DomainException {

    public InvalidRefundException(String message) {
        super(message);
    }
}
