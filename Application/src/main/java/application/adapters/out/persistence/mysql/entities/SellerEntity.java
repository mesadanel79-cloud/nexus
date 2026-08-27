package application.adapters.out.persistence.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

/**
 * JPA Entity for the sellers table (MySQL).
 */
@Entity
@Table(name = "sellers")
public class SellerEntity {

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

    @Column(name = "seller_status_code", nullable = false)
    private String sellerStatusCode;

    @Column(name = "onboarding_date", nullable = false)
    private LocalDate onboardingDate;

    @Column(name = "registered_by", nullable = false)
    private String registeredBy;

    public SellerEntity() {
    }

    public SellerEntity(String identifier, String fullName, String email,
                        String roleCode, String statusCode,
                        String sellerStatusCode, LocalDate onboardingDate,
                        String registeredBy) {
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
        this.roleCode = roleCode;
        this.statusCode = statusCode;
        this.sellerStatusCode = sellerStatusCode;
        this.onboardingDate = onboardingDate;
        this.registeredBy = registeredBy;
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

    public String getSellerStatusCode() {
        return sellerStatusCode;
    }

    public void setSellerStatusCode(String sellerStatusCode) {
        this.sellerStatusCode = sellerStatusCode;
    }

    public LocalDate getOnboardingDate() {
        return onboardingDate;
    }

    public void setOnboardingDate(LocalDate onboardingDate) {
        this.onboardingDate = onboardingDate;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }
}