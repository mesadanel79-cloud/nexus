package application.domain.services;

import java.util.List;
import application.domain.models.*;
import application.domain.ports.in.*;
import application.domain.ports.out.*;

/** Domain Service: Inventory and Warehouse Management subdomain. */
public class WarehouseInventoryService implements RegisterWarehouseUseCase,
        ConsultWarehouseUseCase, ConsultStockLevelUseCase,
        IncreaseStockUseCase, DecreaseStockUseCase,
        BlockWarehouseUseCase, UnblockWarehouseUseCase, CloseWarehouseUseCase {
    private final WarehouseRepository warehouses;
    private final InventoryRepository inventories;
    private final InventoryMovementRepository movements;
    private final ProductRepository products;
    private final SellerRepository sellers;
    public WarehouseInventoryService(WarehouseRepository w, InventoryRepository i,
            InventoryMovementRepository m, ProductRepository p, SellerRepository s) {
        if (w == null || i == null || m == null || p == null || s == null)
            throw new IllegalArgumentException("Repositories must not be null");
        this.warehouses = w; this.inventories = i; this.movements = m;
        this.products = p; this.sellers = s;
    }
    @Override
    public Warehouse registerMarketplaceWarehouse(String id, String name, String loc) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Warehouse id required");
        if (warehouses.findById(id).isPresent()) throw new IllegalStateException("Warehouse exists: " + id);
        return warehouses.save(new MarketplaceWarehouse(id, name, loc, (Administrator) null));
    }
    @Override
    public Warehouse registerSellerWarehouse(String sellerId, String id, String name, String loc) {
        Seller seller = sellers.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found: " + sellerId));
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Warehouse id required");
        if (warehouses.findById(id).isPresent()) throw new IllegalStateException("Warehouse exists: " + id);
        return warehouses.save(new SellerWarehouse(id, name, loc, seller));
    }
    @Override
    public Warehouse consultWarehouse(String id) {
        return warehouses.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + id));
    }
    @Override
    public List<Warehouse> consultWarehouses() { return warehouses.findAll(); }
    @Override
    public int consultStockLevel(String productId, String warehouseId) {
        return requireInventory(productId, warehouseId).getAvailableQuantity();
    }
    @Override
    public Inventory increaseStock(String productId, String warehouseId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Inventory inv = requireInventory(productId, warehouseId);
        if (!inv.getWarehouse().isOperational())
            throw new IllegalStateException("Warehouse not operational: " + warehouseId);
        inv.stockIn(qty);
        Inventory saved = inventories.save(inv);
        saveLastMovement(saved);
        return saved;
    }
    @Override
    public Inventory decreaseStock(String productId, String warehouseId, int qty) {
        if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive");
        Inventory inv = requireInventory(productId, warehouseId);
        if (inv.getAvailableQuantity() < qty)
            throw new IllegalStateException("Insufficient stock: requested=" + qty
                    + ", available=" + inv.getAvailableQuantity());
        inv.adjust(-qty);
        Inventory saved = inventories.save(inv);
        saveLastMovement(saved);
        return saved;
    }
    @Override
    public Warehouse blockWarehouse(String id) {
        Warehouse w = consultWarehouse(id); w.block(); return warehouses.save(w);
    }
    @Override
    public Warehouse unblockWarehouse(String id) {
        Warehouse w = consultWarehouse(id); w.unblock(); return warehouses.save(w);
    }
    @Override
    public Warehouse closeWarehouse(String id) {
        Warehouse w = consultWarehouse(id); w.close(); return warehouses.save(w);
    }
    private Inventory requireInventory(String productId, String warehouseId) {
        Product p = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        if (!(p instanceof PhysicalProduct pp))
            throw new IllegalStateException("Only physical products have inventory: " + productId);
        Warehouse w = consultWarehouse(warehouseId);
        return inventories.findByProductAndWarehouse(pp, w)
                .orElseGet(() -> inventories.save(new Inventory(pp, w, 0)));
    }
    private void saveLastMovement(Inventory inv) {
        if (inv.getMovements().isEmpty()) return;
        movements.save(inv.getMovements().get(inv.getMovements().size() - 1));
    }
}
