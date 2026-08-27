package application.adapters.in.rest.requests;

import java.math.BigDecimal;

/**
 * Request DTO: payload for publishing a product in the catalog.
 */
public class PublishProductRequest {

    private String sellerId;
    private String productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String productType;

    public PublishProductRequest() {
    }

    public PublishProductRequest(String sellerId, String productId, String name,
                                 String description, BigDecimal price,
                                 String productType) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.productType = productType;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    /** PHYSICAL or DIGITAL. */
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}