package application.domain.ports.out;

/**
 * Output Port (User and Authentication subdomain): password hashing and
 * verification. The domain defines what must be done with credentials;
 * infrastructure decides how (e.g. BCrypt).
 */
public interface PasswordHasher {

    /** Encodes a raw password into a secure hash. */
    String encode(String rawPassword);

    /** Verifies a raw password against an encoded hash. */
    boolean matches(String rawPassword, String encodedPassword);
}