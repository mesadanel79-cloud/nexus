package application.domain.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Buyer - Domain Model.
 *
 * Represents a buyer who purchases products published in the catalog.
 * A buyer never manages information belonging to other buyers or to
 * inventory.
 *
 * Relationships:
 * - A Buyer owns one ShoppingCart.
 * - A Buyer places zero or more Order instances.
 */
public class Buyer extends Person {

    private String mainAddress;
    private final List<String> additionalAddresses;
    private CommercialStatus commercialStatus;
    private ShoppingCart cart;
    private final List<Order> orders;

    public Buyer(String identifier, String fullName, String email,
                 UserStatus status, String mainAddress) {
        super(identifier, fullName, email, SystemRole.COMPRADOR, status);
        this.mainAddress = mainAddress;
        this.additionalAddresses = new ArrayList<>();
        this.commercialStatus = CommercialStatus.HABILITADO;
        this.orders = new ArrayList<>();
    }

    /** Usual location for deliveries. */
    public String getMainAddress() {
        return mainAddress;
    }

    public void setMainAddress(String mainAddress) {
        if (mainAddress == null || mainAddress.isBlank()) {
            throw new IllegalArgumentException("Buyer mainAddress must not be null or blank");
        }
        this.mainAddress = mainAddress;
    }

    /** Secondary delivery locations. Empty by default. Read-only view. */
    public List<String> getAdditionalAddresses() {
        return Collections.unmodifiableList(additionalAddresses);
    }

    public void addAdditionalAddress(String address) {
        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Additional address must not be null or blank");
        }
        additionalAddresses.add(address);
    }

    /** Condition of the buyer for making purchases. */
    public CommercialStatus getCommercialStatus() {
        return commercialStatus;
    }

    public void setCommercialStatus(CommercialStatus commercialStatus) {
        this.commercialStatus = commercialStatus;
    }

    /** Buyer's active shopping cart (lazily created on first access). */
    public ShoppingCart getCart() {
        if (cart == null) {
            cart = new ShoppingCart(this);
        }
        return cart;
    }

    void attachCart(ShoppingCart cart) {
        this.cart = cart;
    }

    /** Orders placed by the buyer. Empty by default. Read-only view. */
    public List<Order> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    void attachOrder(Order order) {
        orders.add(order);
    }

    /**
     * Business rule: a buyer can only purchase when commercially enabled.
     */
    public boolean canPurchase() {
        return CommercialStatus.HABILITADO.equals(commercialStatus)
                && UserStatus.ACTIVO.equals(getStatus());
    }
}