package application.domain.services;

import java.math.BigDecimal;
import java.util.List;

import application.domain.models.DigitalProduct;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.ports.in.ChangeProductStatusUseCase;
import application.domain.ports.in.ConsultCatalogUseCase;
import application.domain.ports.in.PublishProductUseCase;
import application.domain.ports.in.UpdateProductUseCase;
import application.domain.ports.out.ProductRepository;
import application.domain.ports.out.SellerRepository;
import application.domain.valueobjects.ProductStatus;

/**
 * Domain Service (Catalog Management): coordinates the lifecycle of product
 * listings published by sellers.
 *
 * Business rules enforced:
 * - Products are published by the seller who owns them.
 * - Updating or changing the status of a product requires ownership by the
 *   acting seller.
 * - Only published products appear in the public catalog.
 */
public class CatalogManagementService implements PublishProductUseCase,
        UpdateProductUseCase, ChangeProductStatusUseCase,
        ConsultCatalogUseCase {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;

    public CatalogManagementService(ProductRepository productRepository,
                                    SellerRepository sellerRepository) {
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public Product publishPhysicalProduct(String sellerId, String productId,
                                          String name, String description,
                                          BigDecimal price) {
        Seller seller = requireSeller(sellerId);
        Product product = new PhysicalProduct(productId, name, description,
                price, seller);
        return productRepository.save(product);
    }

    @Override
    public Product publishDigitalProduct(String sellerId, String productId,
                                         String name, String description,
                                         BigDecimal price) {
        Seller seller = requireSeller(sellerId);
        Product product = new DigitalProduct(productId, name, description,
                price, seller);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(String productId, String sellerId,
                                 String name, String description,
                                 BigDecimal price) {
        Product product = requireOwnedProduct(productId, sellerId);
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        return productRepository.save(product);
    }

    @Override
    public Product changeProductStatus(String productId, String sellerId,
                                       String statusCode) {
        Product product = requireOwnedProduct(productId, sellerId);
        product.setStatus(ProductStatus.fromCode(statusCode));
        return productRepository.save(product);
    }

    @Override
    public List<Product> consultCatalog() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> consultPublishedProducts() {
        return productRepository.findPublished();
    }

    @Override
    public Product consultProduct(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId));
    }

    @Override
    public List<Product> consultProductsBySeller(String sellerId) {
        requireSeller(sellerId);
        return productRepository.findAll().stream()
                .filter(product -> product.getSeller().getIdentifier()
                        .equals(sellerId))
                .toList();
    }

    private Seller requireSeller(String sellerId) {
        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Seller not found: " + sellerId));
    }

    private Product requireOwnedProduct(String productId, String sellerId) {
        Product product = consultProduct(productId);
        if (!product.getSeller().getIdentifier().equals(sellerId)) {
            throw new application.domain.exceptions.SellerNotAuthorizedException(
                    "Seller " + sellerId + " does not own product " + productId);
        }
        return product;
    }
}