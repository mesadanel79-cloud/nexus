package application.domain.ports.in;

import application.domain.models.Product;

/**
 * Input Port (Catalog Management): changes the availability status of a
 * product listing (publish, unpublish, discontinue).
 */
public interface ChangeProductStatusUseCase {

    Product changeProductStatus(String productId, String sellerId,
                                String statusCode);
}