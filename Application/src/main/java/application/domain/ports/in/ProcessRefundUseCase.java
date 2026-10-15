package application.domain.ports.in;

import application.domain.models.Refund;

/**
 * Input Port (Returns and Refunds Management): processes a pending refund
 * (money issued to the buyer). Only pending refunds can be processed.
 */
public interface ProcessRefundUseCase {

    Refund processRefund(String refundId);

    Refund rejectRefund(String refundId);
}
