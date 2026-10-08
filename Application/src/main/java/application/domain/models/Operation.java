package application.domain.models;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import application.domain.valueobjects.OperationType;

/**
 * Operation - Domain Model (Operation and Audit subdomain).
 *
 * Represents a significant business action executed by a system user over a
 * marketplace asset. Operations provide traceability between the performing
 * user and the affected asset.
 *
 * Relationships:
 * - An Operation is performed by one SystemUser.
 * - An Operation affects one MarketplaceAsset.
 * - An Operation is classified by one OperationType.
 *
 * The model represents the business event independently from its
 * persistence mechanism: it never stores raw user or asset identifiers.
 */
public class Operation {

    private static final AtomicLong SEQUENCE = new AtomicLong(400000);

    private final String operationId;
    private final OperationType operationType;
    private final LocalDateTime executionDate;
    private final SystemUser performedBy;
    private final MarketplaceAsset affectedAsset;

    /**
     * Creates a business operation executed at the current instant.
     */
    public Operation(OperationType operationType, SystemUser performedBy,
                     MarketplaceAsset affectedAsset) {
        this("OP-" + SEQUENCE.incrementAndGet(), operationType,
                LocalDateTime.now(), performedBy, affectedAsset);
    }

    /** Reconstitution constructor (persistence). */
    public Operation(String operationId, OperationType operationType,
                     LocalDateTime executionDate, SystemUser performedBy,
                     MarketplaceAsset affectedAsset) {
        if (operationId == null || operationId.isBlank()) {
            throw new IllegalArgumentException(
                    "Operation operationId must not be null or blank");
        }
        if (operationType == null) {
            throw new IllegalArgumentException(
                    "Operation operationType must not be null");
        }
        if (executionDate == null) {
            throw new IllegalArgumentException(
                    "Operation executionDate must not be null");
        }
        if (performedBy == null) {
            throw new IllegalArgumentException(
                    "Operation performedBy must not be null");
        }
        if (affectedAsset == null) {
            throw new IllegalArgumentException(
                    "Operation affectedAsset must not be null");
        }
        this.operationId = operationId;
        this.operationType = operationType;
        this.executionDate = executionDate;
        this.performedBy = performedBy;
        this.affectedAsset = affectedAsset;
    }

    /** Unique operation identifier. */
    public String getOperationId() {
        return operationId;
    }

    /** Business type of the executed operation. */
    public OperationType getOperationType() {
        return operationType;
    }

    /** Date and time on which the operation was executed. */
    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    /** System user who performed the operation. */
    public SystemUser getPerformedBy() {
        return performedBy;
    }

    /** Marketplace asset affected by the operation. */
    public MarketplaceAsset getAffectedAsset() {
        return affectedAsset;
    }
}
