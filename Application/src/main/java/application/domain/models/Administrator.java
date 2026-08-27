package application.domain.models;

import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Administrator - Domain Model.
 *
 * Responsible for registering sellers and administering marketplace
 * warehouses.
 *
 * Relationships:
 * - An Administrator registers zero or more Seller instances.
 * - An Administrator administers MarketplaceWarehouse instances.
 */
public class Administrator extends InternalStaff {

    public Administrator(String identifier, String fullName, String email,
                         UserStatus status) {
        super(identifier, fullName, email, SystemRole.ADMINISTRADOR, status);
    }

    /**
     * Business rule: sellers cannot self-register; they are onboarded by
     * an Administrator. This factory method is the only way to create a
     * Seller within the domain.
     */
    public Seller onboardSeller(String identifier, String fullName,
                                String email) {
        return new Seller(identifier, fullName, email, UserStatus.ACTIVO, this);
    }

    /**
     * Registers a marketplace warehouse administered by this administrator.
     */
    public MarketplaceWarehouse registerMarketplaceWarehouse(String identifier,
                                                             String name,
                                                             String location) {
        MarketplaceWarehouse warehouse =
                new MarketplaceWarehouse(identifier, name, location, this);
        return warehouse;
    }
}