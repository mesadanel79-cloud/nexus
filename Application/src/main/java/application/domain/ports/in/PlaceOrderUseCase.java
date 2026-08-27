package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port: converts the buyer's shopping cart into a formal order
 * (purchase confirmation step of the lifecycle).
 */
public interface PlaceOrderUseCase {

    Order placeOrder(String buyerId, String deliveryAddress);
}