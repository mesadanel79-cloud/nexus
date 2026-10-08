package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;
import application.domain.ports.out.AuditRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * AuditRepository port. Audit records are append-only: save is the only
 * write operation exposed, which preserves their immutability.
 *
 * The in-memory implementation is replaceable by a MongoDB adapter without
 * touching the domain, since the domain only depends on the output port.
 */
@Component
public class AuditRepositoryAdapter implements AuditRepository {

    private final Map<String, AuditLog> auditLogs = new ConcurrentHashMap<>();

    @Override
    public AuditLog save(AuditLog auditLog) {
        auditLogs.put(auditLog.getAuditId(), auditLog);
        return auditLog;
    }

    @Override
    public Optional<AuditLog> findById(String auditId) {
        return Optional.ofNullable(auditLogs.get(auditId));
    }

    @Override
    public List<AuditLog> findAll() {
        return List.copyOf(auditLogs.values());
    }

    @Override
    public List<AuditLog> findByAffectedAsset(MarketplaceAsset asset) {
        return auditLogs.values().stream()
                .filter(auditLog -> sameAsset(auditLog.getAffectedAsset(),
                        asset))
                .toList();
    }

    private boolean sameAsset(MarketplaceAsset first, MarketplaceAsset second) {
        return first != null && second != null
                && first.getAssetType().equals(second.getAssetType())
                && first.getAssetIdentifier()
                        .equals(second.getAssetIdentifier());
    }
}
