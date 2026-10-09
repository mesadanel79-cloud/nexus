package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port (Order Management): retrieves information about an order
 * according to the requesting user's permissions.
 */
public interface ConsultOrderUseCase {

    Order consultOrder(Integer orderId);
}
