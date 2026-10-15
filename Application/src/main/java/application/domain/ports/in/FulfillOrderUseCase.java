package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port (Order Management): allocates the ordered items from the
 * designated warehouse (SDD Fulfill Order) and records the business
 * operation and audit event.
 */
public interface FulfillOrderUseCase {

    Order fulfillOrder(Integer orderId, String warehouseId);
}
