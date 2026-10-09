package application.domain.ports.in;

import application.domain.models.Inventory;

/**
 * Input Port (Inventory and Warehouse Management): adds units of a product
 * to a warehouse's inventory and generates the corresponding business
 * operation and audit record.
 */
public interface IncreaseStockUseCase {

    Inventory increaseStock(String productId, String warehouseId, int quantity);
}
