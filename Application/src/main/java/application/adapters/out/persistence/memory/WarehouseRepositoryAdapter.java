package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Warehouse;
import application.domain.ports.out.WarehouseRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * WarehouseRepository port. Replaceable by a MySQL/MongoDB adapter without
 * touching the domain.
 */
@Component
public class WarehouseRepositoryAdapter implements WarehouseRepository {

    private final Map<String, Warehouse> warehousesById =
            new ConcurrentHashMap<>();

    @Override
    public Warehouse save(Warehouse warehouse) {
        warehousesById.put(warehouse.getIdentifier(), warehouse);
        return warehouse;
    }

    @Override
    public Optional<Warehouse> findById(String warehouseId) {
        return Optional.ofNullable(warehousesById.get(warehouseId));
    }

    @Override
    public List<Warehouse> findAll() {
        return List.copyOf(warehousesById.values());
    }
}
