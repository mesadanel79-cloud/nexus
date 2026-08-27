package application.adapters.out.persistence.mysql.repositories;

import application.adapters.out.persistence.mysql.entities.ProductEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for product persistence (MySQL).
 */
public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {

    List<ProductEntity> findByStatusCode(String statusCode);
}