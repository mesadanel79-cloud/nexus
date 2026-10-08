package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for registering a buyer.
 */
public class RegisterBuyerRequest {

    private String identifier;
    private String fullName;
    private String email;
    private String mainAddress;

    public RegisterBuyerRequest() {
    }

    public RegisterBuyerRequest(String identifier, String fullName,
                                String email, String mainAddress) {
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
        this.mainAddress = mainAddress;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
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

    public String getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(String mainAddress) {
        this.mainAddress = mainAddress;
    }
}