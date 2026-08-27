package application.adapters.out.persistence.mysql.adapters;

import application.adapters.out.persistence.mysql.mappers.BuyerPersistenceMapper;
import application.adapters.out.persistence.mysql.mappers.OrderPersistenceMapper;
import application.adapters.out.persistence.mysql.repositories.BuyerJpaRepository;
import application.adapters.out.persistence.mysql.repositories.OrderJpaRepository;
import application.domain.models.Order;
import application.domain.ports.out.OrderRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (MySQL): implements the OrderRepository output port
 * using JPA.
 */
@Component
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final BuyerJpaRepository buyerJpaRepository;

    public OrderRepositoryAdapter(OrderJpaRepository orderJpaRepository,
                                  BuyerJpaRepository buyerJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
        this.buyerJpaRepository = buyerJpaRepository;
    }

    @Override
    public Order save(Order order) {
        orderJpaRepository.save(OrderPersistenceMapper.toEntity(order));
        return order;
    }

    @Override
    public Optional<Order> findById(Integer orderId) {
        return orderJpaRepository.findById(orderId).map(entity ->
                OrderPersistenceMapper.toDomain(entity,
                        buyerJpaRepository.findById(entity.getBuyerIdentifier())
                                .map(BuyerPersistenceMapper::toDomain)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Buyer not found for order: " + orderId))));
    }

    @Override
    public List<Order> findByBuyerId(String buyerId) {
        return orderJpaRepository.findByBuyerIdentifier(buyerId).stream()
                .map(entity -> OrderPersistenceMapper.toDomain(entity,
                        buyerJpaRepository.findById(entity.getBuyerIdentifier())
                                .map(BuyerPersistenceMapper::toDomain)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Buyer not found for order of buyer " + buyerId))))
                .toList();
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream()
                .map(entity -> OrderPersistenceMapper.toDomain(entity,
                        buyerJpaRepository.findById(entity.getBuyerIdentifier())
                                .map(BuyerPersistenceMapper::toDomain)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Buyer not found for order"))))
                .toList();
    }
}