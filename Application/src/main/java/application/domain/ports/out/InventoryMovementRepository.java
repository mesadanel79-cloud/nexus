package application.domain.ports.out;

import java.util.List;

import application.domain.models.InventoryMovement;

/**
 * Output Port: append-only persistence contract for the inventory
 * movement history. Implemented by the MongoDB adapter because movements
 * are historical events that must not be modified once persisted.
 */
public interface InventoryMovementRepository {

    InventoryMovement save(InventoryMovement movement);

    List<InventoryMovement> findByProductId(String productId);

    List<InventoryMovement> findAll();
}