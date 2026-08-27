package application.adapters.out.persistence.mongodb.mappers;

import application.adapters.out.persistence.mongodb.documents.InventoryMovementDocument;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.valueobjects.InventoryMovementType;

/**
 * Mapper: converts between the InventoryMovement domain model and the
 * InventoryMovementDocument of MongoDB.
 */
public final class InventoryMovementMongoMapper {

    private InventoryMovementMongoMapper() {
    }

    public static InventoryMovementDocument toDocument(InventoryMovement movement,
                                                       String productId,
                                                       String warehouseId) {
        return new InventoryMovementDocument(
                movement.getMovementId(),
                movement.getMovementType().getCode(),
                movement.getQuantity(),
                movement.getExecutionDate(),
                movement.getPerformedBy() != null
                        ? movement.getPerformedBy().getIdentifier()
                        : null,
                productId,
                warehouseId);
    }

    /**
     * Reconstitutes a lightweight domain movement for read purposes. The
     * inventory reference is not rehydrated because the document stores
     * product/warehouse identifiers instead.
     */
    public static InventoryMovement toDomain(InventoryMovementDocument document) {
        return new InventoryMovement(
                document.getMovementId(),
                InventoryMovementType.fromCode(document.getMovementTypeCode()),
                document.getQuantity(),
                document.getExecutionDate(),
                null,
                null);
    }
}