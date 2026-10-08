package application.domain.models;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.SystemRole;

/**
 * AuditLog - Domain Model (Operation and Audit subdomain).
 *
 * Represents the immutable historical record of a significant business
 * event. Audit records preserve the historical context of the operation:
 * what happened, when, who performed it, with which role and which
 * marketplace asset was affected.
 *
 * Once created, an AuditLog cannot be modified: its state is fully defined
 * by its constructor. Persistence is delegated to the AuditRepository
 * output port (implemented with MongoDB according to the architecture).
 *
 * Relationships:
 * - An AuditLog is performed by one SystemUser.
 * - An AuditLog affects one MarketplaceAsset.
 * - An AuditLog is classified by one OperationType.
 */
public class AuditLog {

    private static final AtomicLong SEQUENCE = new AtomicLong(500000);

    private final String auditId;
    private final OperationType operationType;
    private final LocalDateTime operationDate;
    private final SystemUser performedBy;
    private final SystemRole userRole;
    private final MarketplaceAsset affectedAsset;
    private final Map<String, Object> details;

    /**
     * Creates an audit record for the current instant, deriving the user
     * role from the performing user's person.
     */
    public AuditLog(OperationType operationType, SystemUser performedBy,
                    MarketplaceAsset affectedAsset,
                    Map<String, Object> details) {
        this("AUD-" + SEQUENCE.incrementAndGet(), operationType,
                LocalDateTime.now(), performedBy,
                performedBy != null && performedBy.getPerson() != null
                        ? performedBy.getPerson().getRole()
                        : null,
                affectedAsset, details);
    }

    /** Reconstitution constructor (persistence). */
    public AuditLog(String auditId, OperationType operationType,
                    LocalDateTime operationDate, SystemUser performedBy,
                    SystemRole userRole, MarketplaceAsset affectedAsset,
                    Map<String, Object> details) {
        if (auditId == null || auditId.isBlank()) {
            throw new IllegalArgumentException(
                    "AuditLog auditId must not be null or blank");
        }
        if (operationType == null) {
            throw new IllegalArgumentException(
                    "AuditLog operationType must not be null");
        }
        if (operationDate == null) {
            throw new IllegalArgumentException(
                    "AuditLog operationDate must not be null");
        }
        if (performedBy == null) {
            throw new IllegalArgumentException(
                    "AuditLog performedBy must not be null");
        }
        if (affectedAsset == null) {
            throw new IllegalArgumentException(
                    "AuditLog affectedAsset must not be null");
        }
        this.auditId = auditId;
        this.operationType = operationType;
        this.operationDate = operationDate;
        this.performedBy = performedBy;
        this.userRole = userRole;
        this.affectedAsset = affectedAsset;
        this.details = details == null
                ? Map.of()
                : Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    /**
     * Builds the audit record derived from an already executed operation.
     * The AuditLog preserves the information contained in the Operation.
     */
    public static AuditLog deriveFrom(Operation operation,
                                      Map<String, Object> details) {
        return new AuditLog(operation.getOperationType(),
                operation.getPerformedBy(), operation.getAffectedAsset(),
                details);
    }

    /** Unique audit record identifier. */
    public String getAuditId() {
        return auditId;
    }

    /** Business type of the audited operation. */
    public OperationType getOperationType() {
        return operationType;
    }

    /** Date and time on which the audited operation occurred. */
    public LocalDateTime getOperationDate() {
        return operationDate;
    }

    /** System user who performed the audited operation. */
    public SystemUser getPerformedBy() {
        return performedBy;
    }

    /** Role held by the performing user at operation time. */
    public SystemRole getUserRole() {
        return userRole;
    }

    /** Marketplace asset affected by the audited operation. */
    public MarketplaceAsset getAffectedAsset() {
        return affectedAsset;
    }

    /** Immutable operation-specific information. */
    public Map<String, Object> getDetails() {
        return details;
    }
}
