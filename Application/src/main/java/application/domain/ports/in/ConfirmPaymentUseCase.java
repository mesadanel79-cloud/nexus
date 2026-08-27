package application.domain.ports.in;

import application.domain.models.Order;

/**
 * Input Port: confirms the payment of an order (PENDING_PAYMENT -> PAID).
 * The invoice is issued and preparation processes begin.
 */
public interface ConfirmPaymentUseCase {

    Order confirmPayment(Integer orderId);
}