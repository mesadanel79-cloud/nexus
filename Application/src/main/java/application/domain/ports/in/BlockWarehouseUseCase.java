package application.domain.ports.in;

import application.domain.models.Warehouse;

/**
 * Input Port (Inventory and Warehouse Management): changes the operational
 * status of a warehouse to blocked and records the corresponding business
 * operation.
 */
public interface BlockWarehouseUseCase {

    Warehouse blockWarehouse(String warehouseId);
}
