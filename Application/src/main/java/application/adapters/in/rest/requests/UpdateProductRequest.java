package application.adapters.in.rest.requests;

import java.math.BigDecimal;

/**
 * Request DTO: payload for updating an existing product listing.
 */
public class UpdateProductRequest {

    private String sellerId;
    private String name;
    private String description;
    private BigDecimal price;

    public UpdateProductRequest() {
    }

    public UpdateProductRequest(String sellerId, String name,
                                String description, BigDecimal price) {
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
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
}