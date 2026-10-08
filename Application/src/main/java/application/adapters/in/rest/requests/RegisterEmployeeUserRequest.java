package application.adapters.in.rest.requests;

/**
 * Request DTO: payload for registering an internal employee system user.
 */
public class RegisterEmployeeUserRequest {

    private String administratorId;
    private String roleCode;
    private String identifier;
    private String fullName;
    private String email;
    private String username;
    private String password;

    public RegisterEmployeeUserRequest() {
    }

    public RegisterEmployeeUserRequest(String administratorId,
                                       String roleCode, String identifier,
                                       String fullName, String email,
                                       String username, String password) {
        this.administratorId = administratorId;
        this.roleCode = roleCode;
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getAdministratorId() {
        return administratorId;
    }

    public void setAdministratorId(String administratorId) {
        this.administratorId = administratorId;
    }

    /** ADMINISTRADOR, OPERADOR_LOGISTICO or SUPERVISOR. */
    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
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