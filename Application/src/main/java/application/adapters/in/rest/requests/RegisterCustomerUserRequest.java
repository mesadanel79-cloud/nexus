package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for registering a customer system user.
 */
public class RegisterCustomerUserRequest {

    private String personId;
    private String username;
    private String password;

    public RegisterCustomerUserRequest() {
    }

    public RegisterCustomerUserRequest(String personId, String username,
                                       String password) {
        this.personId = personId;
        this.username = username;
        this.password = password;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}