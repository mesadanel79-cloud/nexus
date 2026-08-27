package application.adapters.out.persistence.mongodb.documents;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB Document for the inventory_movements collection.
 *
 * Movements are historical, append-only events: once persisted they are
 * never modified. MongoDB's flexible schema suits this event history.
 */
@Document(collection = "inventory_movements")
public class InventoryMovementDocument {

    @Id
    private Integer movementId;

    private String movementTypeCode;
    private Integer quantity;
    private LocalDateTime executionDate;
    private String performedByIdentifier;
    private String productId;
    private String warehouseId;

    public InventoryMovementDocument() {
    }

    public InventoryMovementDocument(Integer movementId, String movementTypeCode,
                                     Integer quantity,
                                     LocalDateTime executionDate,
                                     String performedByIdentifier,
                                     String productId, String warehouseId) {
        this.movementId = movementId;
        this.movementTypeCode = movementTypeCode;
        this.quantity = quantity;
        this.executionDate = executionDate;
        this.performedByIdentifier = performedByIdentifier;
        this.productId = productId;
        this.warehouseId = warehouseId;
    }

    public Integer getMovementId() {
        return movementId;
    }

    public void setMovementId(Integer movementId) {
        this.movementId = movementId;
    }

    public String getMovementTypeCode() {
        return movementTypeCode;
    }

    public void setMovementTypeCode(String movementTypeCode) {
        this.movementTypeCode = movementTypeCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(LocalDateTime executionDate) {
        this.executionDate = executionDate;
    }

    public String getPerformedByIdentifier() {
        return performedByIdentifier;
    }

    public void setPerformedByIdentifier(String performedByIdentifier) {
        this.performedByIdentifier = performedByIdentifier;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }
}