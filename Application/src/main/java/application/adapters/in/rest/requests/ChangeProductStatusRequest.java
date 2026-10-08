package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for changing a product's availability status.
 */
public class ChangeProductStatusRequest {

    private String sellerId;
    private String statusCode;

    public ChangeProductStatusRequest() {
    }

    public ChangeProductStatusRequest(String sellerId, String statusCode) {
        this.sellerId = sellerId;
        this.statusCode = statusCode;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}