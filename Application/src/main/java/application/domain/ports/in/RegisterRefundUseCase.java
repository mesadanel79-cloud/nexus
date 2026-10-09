package application.domain.ports.in;

import application.domain.models.Refund;

/**
 * Input Port (Returns and Refunds Management): registers the refund
 * associated with an approved return and records the corresponding business
 * operation and audit event.
 */
public interface RegisterRefundUseCase {

    Refund registerRefund(String returnId);
}
