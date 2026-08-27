package application.adapters.out.persistence.mongodb.adapters;

import application.adapters.out.persistence.mongodb.mappers.InventoryMovementMongoMapper;
import application.adapters.out.persistence.mongodb.repositories.InventoryMovementMongoRepository;
import application.domain.models.InventoryMovement;
import application.domain.ports.out.InventoryMovementRepository;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (MongoDB): implements the InventoryMovementRepository
 * output port. Movements are append-only: save is the only write
 * operation exposed.
 */
@Component
public class InventoryMovementRepositoryAdapter implements InventoryMovementRepository {

    private final InventoryMovementMongoRepository mongoRepository;

    public InventoryMovementRepositoryAdapter(
            InventoryMovementMongoRepository mongoRepository) {
        this.mongoRepository = mongoRepository;
    }

    @Override
    public InventoryMovement save(InventoryMovement movement) {
        String productId = movement.getAffectedInventory() != null
                && movement.getAffectedInventory().getProduct() != null
                ? movement.getAffectedInventory().getProduct().getIdentifier()
                : null;
        String warehouseId = movement.getAffectedInventory() != null
                && movement.getAffectedInventory().getWarehouse() != null
                ? movement.getAffectedInventory().getWarehouse().getIdentifier()
                : null;
        mongoRepository.save(
                InventoryMovementMongoMapper.toDocument(movement, productId, warehouseId));
        return movement;
    }

    @Override
    public List<InventoryMovement> findByProductId(String productId) {
        return mongoRepository.findByProductId(productId).stream()
                .map(InventoryMovementMongoMapper::toDomain)
                .toList();
    }

    @Override
    public List<InventoryMovement> findAll() {
        return mongoRepository.findAll().stream()
                .map(InventoryMovementMongoMapper::toDomain)
                .toList();
    }
}