package application.domain.ports.in;

import application.domain.models.Warehouse;

/**
 * Input Port (Inventory and Warehouse Management): registers a new warehouse
 * of the marketplace or of a seller and establishes its initial
 * configuration and status.
 */
public interface RegisterWarehouseUseCase {

    /** Registers a warehouse administered directly by the marketplace. */
    Warehouse registerMarketplaceWarehouse(String warehouseId, String name,
                                           String location);

    /** Registers a warehouse owned by an existing seller. */
    Warehouse registerSellerWarehouse(String sellerId, String warehouseId,
                                      String name, String location);
}
