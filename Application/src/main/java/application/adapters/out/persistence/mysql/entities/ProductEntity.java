package application.adapters.out.persistence.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.math.BigDecimal;

/**
 * JPA Entity for the products table (MySQL). Distinguishes physical and
 * digital products through the product_type discriminator.
 */
@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class ProductEntity {

    @Id
    @Column(name = "identifier", length = 40, nullable = false, unique = true)
    private String identifier;

    @Column(name = "product_type", nullable = false)
    private String productType;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "status_code", nullable = false)
    private String statusCode;

    @Column(name = "seller_identifier", nullable = false)
    private String sellerIdentifier;

    public ProductEntity() {
    }

    public ProductEntity(String identifier, String productType, String name,
                         String description, BigDecimal price,
                         String statusCode, String sellerIdentifier) {
        this.identifier = identifier;
        this.productType = productType;
        this.name = name;
        this.description = description;
        this.price = price;
        this.statusCode = statusCode;
        this.sellerIdentifier = sellerIdentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSellerIdentifier() {
        return sellerIdentifier;
    }

    public void setSellerIdentifier(String sellerIdentifier) {
        this.sellerIdentifier = sellerIdentifier;
    }
}