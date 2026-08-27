package application.domain.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * CartItem - Domain Model.
 *
 * Represents a product line within a ShoppingCart: the selected product
 * and its quantity.
 */
public class CartItem {

    private final Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("CartItem product must not be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("CartItem quantity must be positive");
        }
        this.product = product;
        this.quantity = quantity;
    }

    /** Selected product. */
    public Product getProduct() {
        return product;
    }

    /** Selected quantity. */
    public int getQuantity() {
        return quantity;
    }

    void increaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }

    /** Line total = unit price * quantity. */
    public BigDecimal getLineTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CartItem cartItem = (CartItem) o;
        return product.getIdentifier().equals(cartItem.product.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getIdentifier());
    }
}