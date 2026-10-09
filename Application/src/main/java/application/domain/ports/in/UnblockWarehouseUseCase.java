package application.domain.ports.in;

import application.domain.models.Warehouse;

/**
 * Input Port (Inventory and Warehouse Management): restores a blocked
 * warehouse to an operational state and records the corresponding business
 * operation.
 */
public interface UnblockWarehouseUseCase {

    Warehouse unblockWarehouse(String warehouseId);
}
