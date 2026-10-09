package application.domain.ports.in;

/**
 * Input Port (Inventory and Warehouse Management): retrieves the current
 * available stock of a product within a warehouse.
 */
public interface ConsultStockLevelUseCase {

    int consultStockLevel(String productId, String warehouseId);
}
