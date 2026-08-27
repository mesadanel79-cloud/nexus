package application.domain.models;

import java.util.Collections;
import java.util.List;

/**
 * MarketplaceWarehouse - Domain Model.
 *
 * Warehouse directly administered by the marketplace organization.
 *
 * Relationships:
 * - A MarketplaceWarehouse is administered by one or more Administrator
 *   instances.
 */
public class MarketplaceWarehouse extends Warehouse {

    private final List<Administrator> administrators;

    public MarketplaceWarehouse(String identifier, String name, String location,
                                Administrator administrator) {
        super(identifier, name, location);
        this.administrators = new java.util.ArrayList<>();
        if (administrator != null) {
            administrators.add(administrator);
        }
    }

    /** Administrators responsible for this warehouse. Read-only view. */
    public List<Administrator> getAdministrators() {
        return Collections.unmodifiableList(administrators);
    }

    /** Assigns an additional administrator to this warehouse. */
    public void addAdministrator(Administrator administrator) {
        if (administrator == null) {
            throw new IllegalArgumentException("Administrator must not be null");
        }
        administrators.add(administrator);
    }
}