package application.domain.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductVariant;

/**
 * Product (Abstract) - Domain Model.
 *
 * Represents a good offered in the NexusMarket catalog. The catalog
 * distinguishes between physical products, which require inventory and
 * dispatch, and digital products, which are delivered immediately upon
 * payment.
 *
 * Relationships:
 * - A Product is published by one Seller.
 * - A Product may have zero or more ProductVariant instances.
 *
 * This class cannot be instantiated directly.
 */
public abstract class Product {

    private final String identifier;
    private String name;
    private String description;
    private BigDecimal price;
    private final List<ProductVariant> variants;
    private ProductStatus status;
    private final Seller seller;

    protected Product(String identifier, String name, String description,
                      BigDecimal price, Seller seller) {
        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException("Product identifier must not be null or blank");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product price must not be null or negative");
        }
        if (seller == null) {
            throw new IllegalArgumentException("Product seller must not be null");
        }
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.price = price;
        this.variants = new ArrayList<>();
        this.status = ProductStatus.PUBLICADO;
        this.seller = seller;
        seller.attachProduct(this);
    }

    /** Unique identifier of the product. */
    public String getIdentifier() {
        return identifier;
    }

    /** Commercial name of the product. */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be null or blank");
        }
        this.name = name;
    }

    /** Functional description of the product. */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /** Unit sale price. */
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product price must not be null or negative");
        }
        this.price = price;
    }

    /** Variations in color, size, model, etc. Empty by default. Read-only view. */
    public List<ProductVariant> getVariants() {
        return Collections.unmodifiableList(variants);
    }

    public void addVariant(ProductVariant variant) {
        if (variant == null) {
            throw new IllegalArgumentException("Variant must not be null");
        }
        variants.add(variant);
    }

    /** Published, Suspended or Discontinued. */
    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    /** Seller who publishes the product. */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Business rule: only published products are visible in the public
     * catalog and therefore purchasable.
     */
    public boolean isPurchasable() {
        return ProductStatus.PUBLICADO.equals(status)
                && application.domain.valueobjects.SellerStatus.ACTIVO
                        .equals(seller.getSellerStatus());
    }
}