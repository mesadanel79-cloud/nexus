package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for seller registration.
 * DTOs never enter the domain layer and contain no business logic.
 */
public class RegisterSellerRequest {

    private String administratorId;
    private String identifier;
    private String fullName;
    private String email;

    public RegisterSellerRequest() {
    }

    public RegisterSellerRequest(String administratorId, String identifier,
                                 String fullName, String email) {
        this.administratorId = administratorId;
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
    }

    public String getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(String administratorId) {
        this.administratorId = administratorId;
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
}