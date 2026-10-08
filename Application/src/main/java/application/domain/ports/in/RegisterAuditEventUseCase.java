package application.domain.ports.in;

import application.domain.models.AuditLog;

/**
 * Input Port (Operation and Audit subdomain): creates the immutable audit
 * record of a significant business operation.
 *
 * The service receives the AuditLog Domain Model, which preserves the
 * historical context of the operation (type, date, performing user, role
 * and affected asset).
 */
public interface RegisterAuditEventUseCase {

    /** Persists the audit record and returns the registered Domain Model. */
    AuditLog registerAuditEvent(AuditLog auditLog);
}
