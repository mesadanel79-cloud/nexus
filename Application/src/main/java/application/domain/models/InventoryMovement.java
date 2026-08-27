package application.domain.models;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import application.domain.valueobjects.InventoryMovementType;

/**
 * InventoryMovement - Domain Model.
 *
 * Represents a significant change in the stock of an Inventory record.
 * Provides traceability between products, warehouses and users.
 *
 * A movement represents an event that occurred; it is distinct from the
 * current status of the inventory.
 *
 * Relationships:
 * - An Inventory may generate zero or more InventoryMovement instances.
 * - Each InventoryMovement affects one Inventory.
 */
public class InventoryMovement {

    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    private final Integer movementId;
    private final InventoryMovementType movementType;
    private final int quantity;
    private final LocalDateTime executionDate;
    private final InternalStaff performedBy;
    private final Inventory affectedInventory;

    /**
     * Creates an immutable movement event. The execution date is the
     * creation instant; the performing user is attached afterwards when
     * available (e.g. by the application service).
     */
    InventoryMovement(InventoryMovementType movementType, int quantity,
                      Inventory affectedInventory) {
        if (movementType == null) {
            throw new IllegalArgumentException("Movement type must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Movement quantity must be positive");
        }
        if (affectedInventory == null) {
            throw new IllegalArgumentException("Affected inventory must not be null");
        }
        this.movementId = SEQUENCE.incrementAndGet();
        this.movementType = movementType;
        this.quantity = quantity;
        this.executionDate = LocalDateTime.now();
        this.performedBy = null;
        this.affectedInventory = affectedInventory;
        affectedInventory.attachMovement(this);
    }

    /** Full constructor including the responsible user. */
    public InventoryMovement(Integer movementId, InventoryMovementType movementType,
                             int quantity, LocalDateTime executionDate,
                             InternalStaff performedBy, Inventory affectedInventory) {
        this.movementId = movementId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.executionDate = executionDate;
        this.performedBy = performedBy;
        this.affectedInventory = affectedInventory;
    }

    /** Unique movement identifier. */
    public Integer getMovementId() {
        return movementId;
    }

    /** Category of the inventory movement. */
    public InventoryMovementType getMovementType() {
        return movementType;
    }

    /** Units affected by the movement. */
    public int getQuantity() {
        return quantity;
    }

    /** Date and time when the movement occurred. */
    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    /** User responsible for the movement (may be null for system events). */
    public InternalStaff getPerformedBy() {
        return performedBy;
    }

    /** Inventory record affected by the movement. */
    public Inventory getAffectedInventory() {
        return affectedInventory;
    }
}