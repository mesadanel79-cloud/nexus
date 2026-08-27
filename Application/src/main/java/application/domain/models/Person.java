package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Person (Abstract) - Domain Model.
 *
 * Represents any identifiable participant within NexusMarket. Centralizes
 * the identity and contact information shared by buyers, sellers and
 * internal staff.
 *
 * The role assigned to a person represents what that person means within
 * the system and determines their responsibilities and permissions.
 *
 * Business rules:
 * - Each user has a single role within the system (RG-02).
 * - The identification document and email address must be unique across
 *   the platform.
 *
 * This class cannot be instantiated directly.
 */
public abstract class Person {

    private final String identifier;
    private String fullName;
    private String email;
    private SystemRole role;
    private UserStatus status;

    protected Person(String identifier, String fullName, String email,
                     SystemRole role, UserStatus status) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Person identifier must not be null or blank");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Person fullName must not be null or blank");
        }
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            throw new IllegalArgumentException("Person email must be a valid address");
        }
        this.identifier = identifier;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    /** Unique identifier of the person. */
    public String getIdentifier() {
        return identifier;
    }

    /** Official name of the person. */
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Person fullName must not be null or blank");
        }
        this.fullName = fullName;
    }

    /** Primary access and communication channel. Unique across the platform. */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]+$")) {
            throw new IllegalArgumentException("Person email must be a valid address");
        }
        this.email = email;
    }

    /** Defines the participant's responsibilities and permissions. Unique per person. */
    public SystemRole getRole() {
        return role;
    }

    protected void setRole(SystemRole role) {
        this.role = role;
    }

    /** Operational condition (Activo, Bloqueado, Inactivo). */
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}