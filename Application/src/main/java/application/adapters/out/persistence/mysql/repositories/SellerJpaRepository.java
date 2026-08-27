package application.adapters.out.persistence.mysql.repositories;

import application.adapters.out.persistence.mysql.entities.SellerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for seller persistence (MySQL).
 */
public interface SellerJpaRepository extends JpaRepository<SellerEntity, String> {
}