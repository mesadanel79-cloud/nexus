package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port (Order Management): confirms an order after business
 * validations (SDD Confirm Order). Confirmation is distinct from payment:
 * it validates eligibility and moves the order toward fulfillment.
 */
public interface ConfirmOrderUseCase {

    Order confirmOrder(Integer orderId);
}
