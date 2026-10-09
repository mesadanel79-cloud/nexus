package application.domain.ports.in;

import application.domain.models.Warehouse;

/**
 * Input Port (Inventory and Warehouse Management): permanently closes a
 * warehouse according to the applicable business rules and records the
 * corresponding operation.
 */
public interface CloseWarehouseUseCase {

    Warehouse closeWarehouse(String warehouseId);
}
