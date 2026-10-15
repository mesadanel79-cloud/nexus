package application.domain.ports.in;

import application.domain.models.Order;
import application.domain.valueobjects.OrderStatus;

/**
 * Input Port (Order Management): retrieves the current lifecycle status
 * and full details of an order (SDD Consult Order Status / Details).
 */
public interface ConsultOrderStatusUseCase {

    OrderStatus consultOrderStatus(Integer orderId);

    Order consultOrderDetails(Integer orderId);
}
