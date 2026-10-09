package application.domain.ports.in;

import java.util.List;

import application.domain.models.Warehouse;

/**
 * Input Port (Inventory and Warehouse Management): retrieves the information
 * of a warehouse according to the permissions of the requesting user.
 */
public interface ConsultWarehouseUseCase {

    Warehouse consultWarehouse(String warehouseId);

    List<Warehouse> consultWarehouses();
}
