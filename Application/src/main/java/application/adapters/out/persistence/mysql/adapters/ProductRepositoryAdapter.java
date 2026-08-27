package application.adapters.out.persistence.mysql.adapters;

import application.adapters.out.persistence.mysql.entities.ProductEntity;
import application.adapters.out.persistence.mysql.repositories.ProductJpaRepository;
import application.adapters.out.persistence.mysql.repositories.SellerJpaRepository;
import application.domain.models.DigitalProduct;
import application.domain.models.PhysicalProduct;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.ports.out.ProductRepository;
import application.domain.valueobjects.ProductStatus;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (MySQL): implements the ProductRepository output port
 * using JPA.
 */
@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final SellerJpaRepository sellerJpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository,
                                    SellerJpaRepository sellerJpaRepository) {
        this.productJpaRepository = productJpaRepository;
        this.sellerJpaRepository = sellerJpaRepository;
    }

    @Override
    public Product save(Product product) {
        String type = product instanceof DigitalProduct ? "DIGITAL" : "PHYSICAL";
        productJpaRepository.save(new ProductEntity(
                product.getIdentifier(),
                type,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStatus().getCode(),
                product.getSeller().getIdentifier()));
        return product;
    }

    @Override
    public Optional<Product> findById(String identifier) {
        return productJpaRepository.findById(identifier).map(this::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Product> findPublished() {
        return productJpaRepository
                .findByStatusCode(ProductStatus.PUBLICADO.getCode()).stream()
                .map(this::toDomain)
                .toList();
    }

    /** Reconstitutes the domain product with its publishing seller. */
    private Product toDomain(ProductEntity entity) {
        Seller seller = sellerJpaRepository.findById(entity.getSellerIdentifier())
                .map(sellerEntity -> Seller.reconstitute(
                        sellerEntity.getIdentifier(),
                        sellerEntity.getFullName(),
                        sellerEntity.getEmail(),
                        application.domain.valueobjects.UserStatus
                                .fromCode(sellerEntity.getStatusCode()),
                        null))
                .orElse(null);
        BigDecimal price = entity.getPrice();
        if ("DIGITAL".equals(entity.getProductType())) {
            return new DigitalProduct(entity.getIdentifier(), entity.getName(),
                    entity.getDescription(), price, seller);
        }
        return new PhysicalProduct(entity.getIdentifier(), entity.getName(),
                entity.getDescription(), price, seller);
    }
}