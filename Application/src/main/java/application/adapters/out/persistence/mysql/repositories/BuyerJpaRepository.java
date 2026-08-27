package application.adapters.out.persistence.mysql.repositories;

import application.adapters.out.persistence.mysql.entities.BuyerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for buyer persistence (MySQL).
 */
public interface BuyerJpaRepository extends JpaRepository<BuyerEntity, String> {
}