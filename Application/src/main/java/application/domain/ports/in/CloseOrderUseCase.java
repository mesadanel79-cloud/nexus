package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port (Order Management): completes the order lifecycle when the
 * closure conditions are satisfied (SDD Close Order). Only delivered
 * orders can be closed; a closed order becomes immutable.
 */
public interface CloseOrderUseCase {

    Order closeOrder(Integer orderId);
}
