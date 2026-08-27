package application.domain.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Order;
import application.domain.models.Refund;
import application.domain.models.Return;
import application.domain.ports.in.ApproveReturnUseCase;
import application.domain.ports.in.RequestReturnUseCase;
import application.domain.enums.ReturnReason;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.OrderRepository;

/**
 * Domain Service: coordinates the return and refund cycle - request by
 * the buyer, evaluation by internal staff and refund processing.
 *
 * Business rules enforced:
 * - Returns can only be requested for delivered/completed orders.
 * - Only approved returns generate a Refund.
 */
public class RefundProcessingService implements RequestReturnUseCase,
        ApproveReturnUseCase {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final Map<String, Return> returnRegistry = new ConcurrentHashMap<>();

    public RefundProcessingService(OrderRepository orderRepository,
                                   NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Return requestReturn(String buyerId, Integer orderId,
                                ReturnReason reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
        if (!order.getBuyer().getIdentifier().equals(buyerId)) {
            throw new application.domain.exceptions.InvalidReturnRequestException(
                    "The order does not belong to the requesting buyer");
        }
        Return returnRequest = order.requestReturn(null, new Order.ReturnReasonHolder(reason));
        returnRegistry.put(returnRequest.getReturnId(), returnRequest);
        notificationService.notifyBuyer(order.getBuyer().getEmail(),
                "Return registered",
                "Your return request " + returnRequest.getReturnId()
                        + " for order " + orderId + " was registered.");
        return returnRequest;
    }

    @Override
    public Return markUnderReview(String returnId) {
        Return returnRequest = requireReturn(returnId);
        returnRequest.markUnderReview();
        return returnRequest;
    }

    @Override
    public Refund approveReturn(String returnId) {
        Return returnRequest = requireReturn(returnId);
        Refund refund = returnRequest.approve();
        notificationService.notifyBuyer(
                returnRequest.getOrder().getBuyer().getEmail(),
                "Return approved",
                "Your return " + returnId + " was approved. Refund "
                        + refund.getRefundId() + " is pending processing.");
        return refund;
    }

    @Override
    public Return rejectReturn(String returnId) {
        Return returnRequest = requireReturn(returnId);
        returnRequest.reject();
        notificationService.notifyBuyer(
                returnRequest.getOrder().getBuyer().getEmail(),
                "Return rejected",
                "Your return " + returnId + " was rejected.");
        return returnRequest;
    }

    /** Marks a pending refund as processed (money issued to the buyer). */
    public Refund processRefund(String refundId) {
        Refund refund = returnRegistry.values().stream()
                .map(Return::getRefund)
                .filter(r -> r != null && r.getRefundId().equals(refundId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Refund not found: " + refundId));
        refund.markProcessed();
        return refund;
    }

    private Return requireReturn(String returnId) {
        Return returnRequest = returnRegistry.get(returnId);
        if (returnRequest == null) {
            throw new IllegalArgumentException("Return not found: " + returnId);
        }
        return returnRequest;
    }
}