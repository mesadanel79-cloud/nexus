package application.domain.ports.in;

import application.domain.models.Inventory;

/**
 * Input Port (Inventory and Warehouse Management): removes units of a
 * product from a warehouse's inventory after validating the applicable
 * stock and transaction conditions. Negative stock is never allowed.
 */
public interface DecreaseStockUseCase {

    Inventory decreaseStock(String productId, String warehouseId, int quantity);
}
