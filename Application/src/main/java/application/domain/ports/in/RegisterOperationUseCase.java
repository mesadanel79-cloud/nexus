package application.domain.ports.in;

import application.domain.models.Operation;

/**
 * Input Port (Operation and Audit subdomain): registers a business
 * operation performed over a marketplace asset.
 *
 * The service receives the Operation Domain Model, which already contains
 * the performing user and the affected asset; it never receives individual
 * values or primitive identifiers.
 */
public interface RegisterOperationUseCase {

    /** Persists the operation and returns the registered Domain Model. */
    Operation registerOperation(Operation operation);

    /**
     * Persists the operation and generates the corresponding AuditLog in the
     * same application flow, returning the registered operation.
     */
    Operation registerOperationWithAudit(Operation operation);
}
