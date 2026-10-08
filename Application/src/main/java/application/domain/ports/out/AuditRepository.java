package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;

/**
 * Output Port: append-only persistence contract for the audit history.
 * Implemented by an output adapter (MongoDB according to the architecture)
 * because audit records are immutable historical events that must not be
 * modified once persisted.
 */
public interface AuditRepository {

    AuditLog save(AuditLog auditLog);

    Optional<AuditLog> findById(String auditId);

    List<AuditLog> findAll();

    List<AuditLog> findByAffectedAsset(MarketplaceAsset asset);
}
