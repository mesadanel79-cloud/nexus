package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for updating a customer's basic information.
 */
public class UpdateCustomerRequest {

    private String fullName;
    private String email;

    public UpdateCustomerRequest() {
    }

    public UpdateCustomerRequest(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}