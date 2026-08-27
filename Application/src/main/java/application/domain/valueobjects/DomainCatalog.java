package application.domain.valueobjects;

import java.util.Objects;

/**
 * Abstract base class for every business catalog Value Object in NexusMarket.
 *
 * Represents a generic business catalog used throughout the domain.
 * Provides a consistent structure for controlled business values that require
 * a code, a human-readable name and a business description.
 *
 * Characteristics:
 * - Immutable.
 * - Equality determined by value (type + code), not object identity.
 * - Catalog values are controlled by the domain and must never be replaced
 *   by arbitrary strings in the application.
 *
 * This class cannot be instantiated directly.
 */
public abstract class DomainCatalog {

    private final String code;
    private final String name;
    private final String description;

    protected DomainCatalog(String code, String name, String description) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Catalog code must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Catalog name must not be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Catalog description must not be null or blank");
        }
        this.code = code;
        this.name = name;
        this.description = description;
    }

    /** Unique business identifier of the catalog value. */
    public String getCode() {
        return code;
    }

    /** Human-readable name displayed within the application. */
    public String getName() {
        return name;
    }

    /** Business definition of the catalog value. */
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainCatalog that = (DomainCatalog) o;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), code);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{code='" + code + "', name='" + name + "'}";
    }
}