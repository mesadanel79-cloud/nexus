package application.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * ShoppingCart - Domain Model.
 *
 * Represents the provisional selection of products by a buyer, prior to
 * order confirmation.
 *
 * Relationships:
 * - A ShoppingCart belongs to one Buyer.
 * - A ShoppingCart contains zero or more CartItem instances.
 * - A ShoppingCart may be converted into an Order upon purchase
 *   confirmation.
 */
public class ShoppingCart {

    private final Buyer buyer;
    private final List<CartItem> items;
    private LocalDateTime lastUpdated;

    /** Created by and bound to its owning buyer. */
    public ShoppingCart(Buyer buyer) {
        if (buyer == null) {
            throw new IllegalArgumentException("Cart buyer must not be null");
        }
        this.buyer = buyer;
        this.items = new ArrayList<>();
        this.lastUpdated = LocalDateTime.now();
        buyer.attachCart(this);
    }

    /** Buyer who owns the cart. */
    public Buyer getBuyer() {
        return buyer;
    }

    /** Selected products and quantities. Read-only view. */
    public List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /** Last modification date of the cart. */
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Adds a product to the cart. If the product already exists, the
     * quantity is increased. Only purchasable (published/active seller)
     * products are accepted.
     */
    public void addProduct(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (!product.isPurchasable()) {
            throw new IllegalArgumentException(
                    "Product " + product.getIdentifier() + " is not available for purchase");
        }
        Optional<CartItem> existing = items.stream()
                .filter(item -> item.getProduct().getIdentifier()
                        .equals(product.getIdentifier()))
                .findFirst();
        if (existing.isPresent()) {
            existing.get().increaseQuantity(quantity);
        } else {
            items.add(new CartItem(product, quantity));
        }
        touch();
    }

    /** Removes a product line from the cart. */
    public void removeProduct(String productId) {
        items.removeIf(item -> item.getProduct().getIdentifier().equals(productId));
        touch();
    }

    /** Empties the cart. */
    public void clear() {
        items.clear();
        touch();
    }

    /** Total value of the provisional selection. */
    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void touch() {
        this.lastUpdated = LocalDateTime.now();
    }
}