package application.domain.ports.in;

import java.math.BigDecimal;

import application.domain.models.Product;

/**
 * Input Port (Catalog Management): updates the information maintained for
 * an existing product listing.
 */
public interface UpdateProductUseCase {

    Product updateProduct(String productId, String sellerId, String name,
                          String description, BigDecimal price);
}