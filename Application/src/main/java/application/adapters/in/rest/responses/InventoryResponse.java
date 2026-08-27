package application.adapters.in.rest.responses;

/**
 * Response DTO: standardized API representation of an inventory record.
 */
public class InventoryResponse {

    private String productId;
    private String warehouseId;
    private int availableQuantity;
    private int reservedQuantity;
    private boolean damaged;

    public InventoryResponse() {
    }

    public InventoryResponse(String productId, String warehouseId,
                             int availableQuantity, int reservedQuantity,
                             boolean damaged) {
        this.productId = productId;
        this.warehouseId = warehouseId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
        this.damaged = damaged;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getReservedQuantity() {
        return reservedQuantity;
    }

    public void setReservedQuantity(int reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }

    public boolean isDamaged() {
        return damaged;
    }

    public void setDamaged(boolean damaged) {
        this.damaged = damaged;
    }
}