package application.domain.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.domain.valueobjects.WarehouseStatus;

/**
 * Warehouse (Abstract) - Domain Model.
 *
 * Represents a physical storage space where inventory is managed.
 * Marketplace warehouses and Seller warehouses are distinguished.
 *
 * Relationships:
 * - A Warehouse stores zero or more Inventory instances.
 *
 * This class cannot be instantiated directly.
 */
public abstract class Warehouse implements MarketplaceAsset {

    private final String identifier;
    private String name;
    private String location;
    private WarehouseStatus status;
    private final List<Inventory> inventories;

    protected Warehouse(String identifier, String name, String location) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Warehouse identifier must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name must not be null or blank");
        }
        this.identifier = identifier;
        this.name = name;
        this.location = location;
        this.status = WarehouseStatus.ACTIVA;
        this.inventories = new ArrayList<>();
    }

    /** Unique identifier of the warehouse. */
    public String getIdentifier() {
        return identifier;
    }

    /** Descriptive name of the warehouse. */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Warehouse name must not be null or blank");
        }
        this.name = name;
    }

    /** Physical address of the warehouse. */
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /** Operational condition of the warehouse. */
    public WarehouseStatus getStatus() {
        return status;
    }

    public void setStatus(WarehouseStatus status) {
        this.status = status;
    }

    /** Inventory records stored in this warehouse. Read-only view. */
    public List<Inventory> getInventories() {
        return Collections.unmodifiableList(inventories);
    }

    void attachInventory(Inventory inventory) {
        inventories.add(inventory);
    }

    /**
     * Business rule: inventory can only be received by an operational
     * warehouse.
     */
    public boolean isOperational() {
        return WarehouseStatus.ACTIVA.equals(status);
    }

    /**
     * ACTIVA -> BLOQUEADA. A blocked warehouse stops receiving inventory
     * movements until it is unblocked.
     */
    public void block() {
        if (WarehouseStatus.CERRADA.equals(status)) {
            throw new IllegalStateException(
                    "Closed warehouse " + identifier + " cannot be blocked");
        }
        if (!WarehouseStatus.ACTIVA.equals(status)) {
            throw new IllegalStateException(
                    "Only active warehouses can be blocked (current status: "
                            + status.getCode() + ")");
        }
        this.status = WarehouseStatus.BLOQUEADA;
    }

    /** BLOQUEADA -> ACTIVA. */
    public void unblock() {
        if (!WarehouseStatus.BLOQUEADA.equals(status)) {
            throw new IllegalStateException(
                    "Only blocked warehouses can be unblocked (current status: "
                            + status.getCode() + ")");
        }
        this.status = WarehouseStatus.ACTIVA;
    }

    /**
     * Any state -> CERRADA. Terminal state: a closed warehouse is never
     * reopened.
     */
    public void close() {
        if (WarehouseStatus.CERRADA.equals(status)) {
            throw new IllegalStateException(
                    "Warehouse " + identifier + " is already closed");
        }
        this.status = WarehouseStatus.CERRADA;
    }

    /** Marketplace asset identity of the warehouse (Operation/Audit tracing). */
    @Override
    public String getAssetIdentifier() {
        return identifier;
    }

    /** Marketplace asset type of the warehouse. */
    @Override
    public String getAssetType() {
        return "WAREHOUSE";
    }
}