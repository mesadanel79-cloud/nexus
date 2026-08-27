package application.domain.models;

import java.math.BigDecimal;

/**
 * OrderItem - Domain Model.
 *
 * Represents a confirmed product line within an Order.
 */
public class OrderItem {

    private final Product product;
    private final int quantity;
    private final BigDecimal unitPrice;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("OrderItem product must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("OrderItem quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
    }

    /** Confirmed product. */
    public Product getProduct() {
        return product;
    }

    /** Confirmed quantity. */
    public int getQuantity() {
        return quantity;
    }

    /** Unit price captured at confirmation time (immutable snapshot). */
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    /** Line total = unit price * quantity. */
    public BigDecimal getLineTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}