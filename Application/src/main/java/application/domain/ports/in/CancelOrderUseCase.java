package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port (Order Management): cancels an order and records the
 * corresponding decision and operation. Cancellation is only permitted
 * before the order is dispatched.
 */
public interface CancelOrderUseCase {

    Order cancelOrder(Integer orderId);
}
