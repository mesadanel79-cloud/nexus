package application.domain.services;

import application.domain.exceptions.InvalidRefundException;
import application.domain.exceptions.RefundNotFoundException;
import application.domain.models.Refund;
import application.domain.ports.in.ProcessRefundUseCase;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.RefundRepository;
import application.domain.valueobjects.RefundStatus;

/**
 * Domain Service (Returns and Refunds Management - settlement): processes
 * or rejects pending refunds. Only PENDIENTE refunds transition to
 * PROCESADO | RECHAZADO (Refund lifecycle).
 */
public class RefundSettlementService implements ProcessRefundUseCase {

    private final RefundRepository refundRepository;
    private final NotificationService notificationService;

    public RefundSettlementService(RefundRepository refundRepository,
                                   NotificationService notificationService) {
        if (refundRepository == null || notificationService == null) {
            throw new IllegalArgumentException("Dependencies must not be null");
        }
        this.refundRepository = refundRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Refund processRefund(String refundId) {
        Refund refund = requireRefund(refundId);
        if (!RefundStatus.PENDIENTE.equals(refund.getRefundStatus())) {
            throw new InvalidRefundException(
                    "Only pending refunds can be processed (current: "
                            + refund.getRefundStatus().getCode() + ")");
        }
        refund.markProcessed();
        Refund saved = refundRepository.save(refund);
        notificationService.notifyBuyer(
                saved.getReturnRequest().getOrder().getBuyer().getEmail(),
                "Refund processed",
                "Refund " + refundId + " for amount "
                        + saved.getRefundedAmount() + " was processed.");
        return saved;
    }

    @Override
    public Refund rejectRefund(String refundId) {
        Refund refund = requireRefund(refundId);
        if (!RefundStatus.PENDIENTE.equals(refund.getRefundStatus())) {
            throw new InvalidRefundException(
                    "Only pending refunds can be rejected (current: "
                            + refund.getRefundStatus().getCode() + ")");
        }
        refund.reject();
        return refundRepository.save(refund);
    }

    private Refund requireRefund(String refundId) {
        return refundRepository.findById(refundId)
                .orElseThrow(() -> new RefundNotFoundException(
                        "Refund not found: " + refundId));
    }
}
