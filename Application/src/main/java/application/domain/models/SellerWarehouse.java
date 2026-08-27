package application.domain.models;

/**
 * SellerWarehouse - Domain Model.
 *
 * Warehouse owned by a seller, registered either at onboarding time or
 * afterward.
 *
 * Relationships:
 * - A SellerWarehouse belongs to one Seller.
 */
public class SellerWarehouse extends Warehouse {

    private final Seller owner;

    /**
     * Created by the owning Seller (or on its behalf); the relationship is
     * registered bidirectionally.
     */
    public SellerWarehouse(String identifier, String name, String location,
                           Seller owner) {
        super(identifier, name, location);
        if (owner == null) {
            throw new IllegalArgumentException("SellerWarehouse owner must not be null");
        }
        this.owner = owner;
        owner.attachWarehouse(this);
    }

    /** Seller who owns the warehouse. */
    public Seller getOwner() {
        return owner;
    }
}