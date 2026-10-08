package application.domain.ports.in;

import java.util.List;

import application.domain.models.Product;

/**
 * Input Port (Catalog Management): retrieves product listings and the
 * public catalog according to the applicable search and access rules.
 */
public interface ConsultCatalogUseCase {

    List<Product> consultCatalog();

    List<Product> consultPublishedProducts();

    Product consultProduct(String productId);

    List<Product> consultProductsBySeller(String sellerId);
}