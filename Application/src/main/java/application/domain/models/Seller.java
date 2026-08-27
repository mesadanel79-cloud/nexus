package application.domain.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Seller - Domain Model.
 *
 * Represents a seller responsible for marketing products within the
 * platform.
 *
 * Business rule:
 * - Sellers cannot self-register; they are onboarded by an Administrator.
 *
 * Relationships:
 * - A Seller owns zero or more SellerWarehouse instances.
 * - A Seller publishes zero or more Product instances.
 * - A Seller is onboarded by an Administrator.
 */
public class Seller extends Person {

    private final List<SellerWarehouse> warehouses;
    private final List<Product> products;
    private SellerStatus sellerStatus;
    private LocalDate onboardingDate;
    private Administrator registeredBy;

    /**
     * Package-private constructor: Sellers are created through
     * {@link Administrator#onboardSeller}, never self-registered.
     */
    Seller(String identifier, String fullName, String email,
           UserStatus status, Administrator registeredBy) {
        super(identifier, fullName, email, SystemRole.VENDEDOR, status);
        this.warehouses = new ArrayList<>();
        this.products = new ArrayList<>();
        this.sellerStatus = SellerStatus.ACTIVO;
        this.onboardingDate = LocalDate.now();
        this.registeredBy = registeredBy;
    }

    /**
     * Reconstitution constructor for persistence adapters only. Business
     * creation must always go through {@link Administrator#onboardSeller}.
     */
    public static Seller reconstitute(String identifier, String fullName,
                                      String email, UserStatus status,
                                      Administrator registeredBy) {
        return new Seller(identifier, fullName, email, status, registeredBy);
    }

    /** Warehouses owned by the seller. Empty by default. Read-only view. */
    public List<SellerWarehouse> getWarehouses() {
        return Collections.unmodifiableList(warehouses);
    }

    void attachWarehouse(SellerWarehouse warehouse) {
        warehouses.add(warehouse);
    }

    /** Products published by the seller. Empty by default. Read-only view. */
    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    void attachProduct(Product product) {
        products.add(product);
    }

    /** Operational condition of the seller within the marketplace. */
    public SellerStatus getSellerStatus() {
        return sellerStatus;
    }

    public void setSellerStatus(SellerStatus sellerStatus) {
        this.sellerStatus = sellerStatus;
    }

    /** Date on which the seller was registered. */
    public LocalDate getOnboardingDate() {
        return onboardingDate;
    }

    /** Administrator who onboarded the seller. */
    public Administrator getRegisteredBy() {
        return registeredBy;
    }
}