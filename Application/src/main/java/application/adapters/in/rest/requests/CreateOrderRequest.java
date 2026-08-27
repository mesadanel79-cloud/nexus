package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for placing an order from the shopping cart.
 */
public class CreateOrderRequest {

    private String buyerId;
    private String deliveryAddress;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String buyerId, String deliveryAddress) {
        this.buyerId = buyerId;
        this.deliveryAddress = deliveryAddress;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}