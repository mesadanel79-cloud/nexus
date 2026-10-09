package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Inventory;
import application.domain.models.PhysicalProduct;
import application.domain.models.Warehouse;
import application.domain.ports.out.InventoryRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * InventoryRepository port. Keyed by product and warehouse, which is the
 * natural identity of a stock record.
 */
@Component
public class InventoryRepositoryAdapter implements InventoryRepository {

    private final Map<String, Inventory> store = new ConcurrentHashMap<>();

    private String key(PhysicalProduct product, Warehouse warehouse) {
        return product.getIdentifier() + ":" + warehouse.getIdentifier();
    }

    @Override
    public Inventory save(Inventory inventory) {
        store.put(key(inventory.getProduct(), inventory.getWarehouse()),
                inventory);
        return inventory;
    }

    @Override
    public Optional<Inventory> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Inventory> findByProductAndWarehouse(
            PhysicalProduct product, Warehouse warehouse) {
        return Optional.ofNullable(store.get(key(product, warehouse)));
    }

    @Override
    public List<Inventory> findByProduct(PhysicalProduct product) {
        return store.entrySet().stream()
                .filter(entry -> entry.getKey()
                        .startsWith(product.getIdentifier() + ":"))
                .map(Map.Entry::getValue)
                .toList();
    }

    @Override
    public List<Inventory> findAll() {
        return List.copyOf(store.values());
    }
}
