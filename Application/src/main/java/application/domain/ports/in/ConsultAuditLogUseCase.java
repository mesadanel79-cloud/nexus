package application.domain.ports.in;

import java.util.List;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;

/**
 * Input Port (Operation and Audit subdomain): retrieves the historical
 * audit records of significant business operations.
 */
public interface ConsultAuditLogUseCase {

    /** Returns the audit record identified by its audit identifier. */
    AuditLog consultAuditLog(String auditId);

    /** Returns every registered audit record. */
    List<AuditLog> consultAuditLogs();

    /** Returns the audit records associated with the given asset. */
    List<AuditLog> consultAuditLogsByAsset(MarketplaceAsset asset);
}
