package application.domain.models;

import java.time.LocalDateTime;

import application.domain.valueobjects.UserStatus;

/**
 * SystemUser - Domain Model (User and Authentication subdomain).
 *
 * Represents a system identity with credentials (username + password hash)
 * linked to an existing Person (buyer, seller or internal staff).
 *
 * Business rules:
 * - A user can only start a session when the linked person is ACTIVO.
 * - Only one active session token is kept per user.
 *
 * Relationships:
 * - A SystemUser is associated with one Person.
 */
public class SystemUser {

    private final String username;
    private final String passwordHash;
    private final Person person;
    private String sessionToken;
    private LocalDateTime lastLogin;

    public SystemUser(String username, String passwordHash, Person person) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("SystemUser username must not be null or blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("SystemUser passwordHash must not be null or blank");
        }
        if (person == null) {
            throw new IllegalArgumentException("SystemUser person must not be null");
        }
        this.username = username;
        this.passwordHash = passwordHash;
        this.person = person;
    }

    /** Unique login name of the user. */
    public String getUsername() {
        return username;
    }

    /** Encoded password. Never exposes the raw credential. */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** Person associated with this system identity. */
    public Person getPerson() {
        return person;
    }

    /** Active session token, or null when logged out. */
    public String getSessionToken() {
        return sessionToken;
    }

    /** Date and time of the last successful login. */
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    /**
     * Starts a session. Only users linked to an ACTIVO person can log in.
     */
    public void startSession(String token) {
        if (!UserStatus.ACTIVO.equals(person.getStatus())) {
            throw new IllegalStateException(
                    "User " + username + " is not active: "
                            + person.getStatus().getCode());
        }
        this.sessionToken = token;
        this.lastLogin = LocalDateTime.now();
    }

    /** Terminates the current session. */
    public void endSession() {
        this.sessionToken = null;
    }

    /** True while the user holds an active session. */
    public boolean isLoggedIn() {
        return sessionToken != null;
    }
}