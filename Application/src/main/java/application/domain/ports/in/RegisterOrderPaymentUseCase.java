package application.domain.ports.in;

import java.math.BigDecimal;

import application.domain.models.Order;

/**
 * Input Port (Order Management - services.md Register Order Payment):
 * registers the payment of an existing order. Amount is validated against
 * the authoritative Order total (paidAmount == totalAmount unless partial
 * payments are explicitly allowed).
 */
public interface RegisterOrderPaymentUseCase {

    Order registerOrderPayment(Integer orderId, BigDecimal paidAmount);
}
