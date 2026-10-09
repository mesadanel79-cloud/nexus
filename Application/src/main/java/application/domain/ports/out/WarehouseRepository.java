package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Warehouse;

/**
 * Output Port: persistence of Warehouse Domain Models. The domain never
 * accesses the storage technology directly.
 */
public interface WarehouseRepository {

    Warehouse save(Warehouse warehouse);

    Optional<Warehouse> findById(String warehouseId);

    List<Warehouse> findAll();
}
