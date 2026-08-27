package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Inventory;
import application.domain.models.PhysicalProduct;
import application.domain.models.Warehouse;

/**
 * Output Port: persistence contract for inventory records.
 */
public interface InventoryRepository {

    Inventory save(Inventory inventory);

    Optional<Inventory> findById(Long id);

    Optional<Inventory> findByProductAndWarehouse(PhysicalProduct product,
                                                  Warehouse warehouse);

    List<Inventory> findByProduct(PhysicalProduct product);

    List<Inventory> findAll();
}