package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for generating an invoice for an order.
 */
public class GenerateInvoiceRequest {

    private Integer orderId;

    public GenerateInvoiceRequest() {
    }

    public GenerateInvoiceRequest(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}