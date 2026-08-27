package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for requesting a return of a delivered order.
 */
public class RequestReturnRequest {

    private String buyerId;
    private Integer orderId;
    private String reason;

    public RequestReturnRequest() {
    }

    public RequestReturnRequest(String buyerId, Integer orderId, String reason) {
        this.buyerId = buyerId;
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /** One of: PRODUCTO_DANADO, PRODUCTO_INCORRECTO, CAMBIO_DE_OPINION, OTRO. */
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}