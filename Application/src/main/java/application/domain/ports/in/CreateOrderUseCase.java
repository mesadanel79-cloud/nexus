package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port (Order Management - services.md Create Order):
 * creates a new order for a buyer (cart conversion step).
 */
public interface CreateOrderUseCase {

    Order createOrder(String buyerId, String deliveryAddress);
}
