package application.domain.models;

import java.math.BigDecimal;

/**
 * DigitalProduct - Domain Model.
 *
 * Product delivered immediately upon payment confirmation; it does not
 * require inventory or physical dispatch.
 */
public class DigitalProduct extends Product {

    public DigitalProduct(String identifier, String name, String description,
                          BigDecimal price, Seller seller) {
        super(identifier, name, description, price, seller);
    }
}