package application.adapters.out.persistence.mongodb.repositories;

import application.adapters.out.persistence.mongodb.documents.InventoryMovementDocument;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Mongo repository providing persistence for inventory movement documents.
 */
public interface InventoryMovementMongoRepository
        extends MongoRepository<InventoryMovementDocument, Integer> {

    List<InventoryMovementDocument> findByProductId(String productId);
}