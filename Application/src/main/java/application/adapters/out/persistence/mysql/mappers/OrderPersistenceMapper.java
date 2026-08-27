package application.adapters.out.persistence.mysql.mappers;

import application.adapters.out.persistence.mysql.entities.OrderEntity;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;

/**
 * Mapper: converts between the Order domain model and the OrderEntity
 * persistence entity.
 */
public final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {
    }

    public static OrderEntity toEntity(Order order) {
        return new OrderEntity(
                order.getOrderId(),
                order.getBuyer().getIdentifier(),
                order.getOrderStatus().getCode(),
                order.getCreationDate(),
                order.getPaymentConfirmationDate(),
                order.getDeliveryAddress(),
                order.getTotalAmount());
    }

    /**
     * Reconstitutes the domain order. The buyer reference must be supplied
     * by the caller (adapter) since it lives in another aggregate.
     */
    public static Order toDomain(OrderEntity entity, Buyer buyer) {
        return new Order(
                entity.getOrderId(),
                buyer,
                entity.getDeliveryAddress());
    }
}