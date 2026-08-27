package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for adding a product to the shopping cart.
 */
public class AddToCartRequest {

    private String buyerId;
    private String productId;
    private int quantity;

    public AddToCartRequest() {
    }

    public AddToCartRequest(String buyerId, String productId, int quantity) {
        this.buyerId = buyerId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}