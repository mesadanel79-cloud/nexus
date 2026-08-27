package application.adapters.out.persistence.mysql.repositories;

import application.adapters.out.persistence.mysql.entities.OrderEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for order persistence (MySQL).
 */
public interface OrderJpaRepository extends JpaRepository<OrderEntity, Integer> {

    List<OrderEntity> findByBuyerIdentifier(String buyerIdentifier);
}