package application.domain.models;

import java.math.BigDecimal;

/**
 * PhysicalProduct - Domain Model.
 *
 * Product that requires physical inventory and a dispatch process.
 *
 * Relationships:
 * - A PhysicalProduct is stored in zero or more Inventory instances.
 * - A PhysicalProduct generates a Shipment when dispatched.
 */
public class PhysicalProduct extends Product {

    public PhysicalProduct(String identifier, String name, String description,
                           BigDecimal price, Seller seller) {
        super(identifier, name, description, price, seller);
    }
}