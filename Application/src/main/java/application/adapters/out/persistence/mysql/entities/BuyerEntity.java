package application.adapters.out.persistence.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA Entity for the buyers table (MySQL).
 * Persistence entities are never exposed through the API.
 */
@Entity
@Table(name = "buyers")
public class BuyerEntity {

    @Id
    @Column(name = "identifier", length = 30, nullable = false, unique = true)
    private String identifier;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "role_code", nullable = false)
    private String roleCode;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "commercial_status_code", nullable = false)
    private String commercialStatusCode;

    @Column(name = "main_address", nullable = false)
    private String mainAddress;

    public BuyerEntity() {
    }

    public BuyerEntity(String identifier, String fullName, String email,
                       String roleCode, String statusCode,
                       String commercialStatusCode, String mainAddress) {
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
        this.roleCode = roleCode;
        this.statusCode = statusCode;
        this.commercialStatusCode = commercialStatusCode;
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

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCommercialStatusCode() {
        return commercialStatusCode;
    }

    public void setCommercialStatusCode(String commercialStatusCode) {
        this.commercialStatusCode = commercialStatusCode;
    }

    public String getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(String mainAddress) {
        this.mainAddress = mainAddress;
    }
}