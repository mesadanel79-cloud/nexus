package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Order;

/**
 * Output Port: persistence contract for orders.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(Integer orderId);

    List<Order> findByBuyerId(String buyerId);

    List<Order> findAll();
}