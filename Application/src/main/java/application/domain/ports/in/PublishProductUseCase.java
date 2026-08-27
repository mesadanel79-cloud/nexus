package application.domain.ports.in;

import java.math.BigDecimal;

import application.domain.models.Product;

/**
 * Input Port: publishes a product in the catalog on behalf of a seller.
 */
public interface PublishProductUseCase {

    Product publishPhysicalProduct(String sellerId, String productId,
                                   String name, String description,
                                   BigDecimal price);

    Product publishDigitalProduct(String sellerId, String productId,
                                  String name, String description,
                                  BigDecimal price);
}