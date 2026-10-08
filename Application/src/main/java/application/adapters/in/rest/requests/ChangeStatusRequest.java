package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for a status change (customer, product or user).
 */
public class ChangeStatusRequest {

    private String statusCode;

    public ChangeStatusRequest() {
    }

    public ChangeStatusRequest(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}