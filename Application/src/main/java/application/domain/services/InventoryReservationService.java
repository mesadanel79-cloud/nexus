package application.domain.services;

import java.util.List;

import application.domain.exceptions.InsufficientStockException;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.PhysicalProduct;
import application.domain.ports.out.InventoryRepository;
import application.domain.valueobjects.InventoryMovementType;

/**
 * Domain Service: coordinates stock reservation for orders in progress.
 *
 * Business rules enforced:
 * - Negative stock is never allowed under any circumstances.
 * - Damaged or missing inventory cannot be reserved.
 * - Every significant stock change is recorded as an InventoryMovement
 *   (append-only history persisted through the movement repository).
 */
public class InventoryReservationService {

    private final InventoryRepository inventoryRepository;
    private final MovementRecorder movementRecorder;

    /** Functional hook to persist movements as they occur. */
    public interface MovementRecorder {
        void record(InventoryMovementType type, int quantity, String productId,
                    String warehouseId);
    }

    public InventoryReservationService(InventoryRepository inventoryRepository,
                                       MovementRecorder movementRecorder) {
        this.inventoryRepository = inventoryRepository;
        this.movementRecorder = movementRecorder;
    }

    /**
     * Reserves stock for every physical item of the order (RESERVA).
     * Throws InsufficientStockException when the business rules are
     * violated.
     */
    public void reserveStockForOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() instanceof PhysicalProduct physicalProduct) {
                List<Inventory> inventories =
                        inventoryRepository.findByProduct(physicalProduct);
                reserveAcrossWarehouses(physicalProduct, item.getQuantity(), inventories);
            }
        }
    }

    private void reserveAcrossWarehouses(PhysicalProduct product, int quantity,
                                         List<Inventory> inventories) {
        int remaining = quantity;
        for (Inventory inventory : inventories) {
            if (remaining <= 0) {
                break;
            }
            int take = Math.min(inventory.getAvailableQuantity(), remaining);
            if (take > 0) {
                try {
                    inventory.reserve(take);
                    inventoryRepository.save(inventory);
                    movementRecorder.record(InventoryMovementType.RESERVA, take,
                            product.getIdentifier(),
                            inventory.getWarehouse().getIdentifier());
                    remaining -= take;
                } catch (InsufficientStockException ignored) {
                    // Try next warehouse; negative stock never allowed.
                }
            }
        }
        if (remaining > 0) {
            throw new InsufficientStockException(
                    "Insufficient stock across all warehouses for product "
                            + product.getIdentifier() + ": missing=" + remaining);
        }
    }

    /**
     * Records sale outflow of reserved stock upon dispatch
     * (SALIDA_POR_VENTA).
     */
    public void registerSaleOutflow(Order order) {
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() instanceof PhysicalProduct physicalProduct) {
                List<Inventory> inventories =
                        inventoryRepository.findByProduct(physicalProduct);
                for (Inventory inventory : inventories) {
                    int reserved = inventory.getReservedQuantity();
                    if (reserved > 0) {
                        inventory.releaseSaleOutflow(reserved);
                        inventoryRepository.save(inventory);
                        movementRecorder.record(InventoryMovementType.SALIDA_POR_VENTA,
                                reserved, physicalProduct.getIdentifier(),
                                inventory.getWarehouse().getIdentifier());
                    }
                }
            }
        }
    }
}