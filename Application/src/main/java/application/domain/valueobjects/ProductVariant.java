package application.domain.valueobjects;

import java.util.Objects;

/**
 * ProductVariant Value Object.
 *
 * Represents a specific variation of a product, such as color, size or
 * model. Unlike the business catalogs, ProductVariant does not inherit
 * from DomainCatalog: it is not a domain-controlled value but a free
 * combination defined by the seller when publishing the product.
 *
 * Characteristics:
 * - Immutable.
 * - Equality determined by the combination of variantName and value.
 */
public final class ProductVariant {

    private final String variantName;
    private final String value;

    public ProductVariant(String variantName, String value) {
        if (variantName == null || variantName.isBlank()) {
            throw new IllegalArgumentException("Variant name must not be null or blank");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Variant value must not be null or blank");
        }
        this.variantName = variantName;
        this.value = value;
    }

    /** Name of the variation attribute (e.g. "Color", "Size"). */
    public String getVariantName() {
        return variantName;
    }

    /** Specific value of the variation (e.g. "Red", "M"). */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProductVariant that = (ProductVariant) o;
        return variantName.equals(that.variantName) && value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variantName, value);
    }

    @Override
    public String toString() {
        return "ProductVariant{variantName='" + variantName + "', value='" + value + "'}";
    }
}