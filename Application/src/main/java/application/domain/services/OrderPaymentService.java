package application.domain.services;

import java.math.BigDecimal;

import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.OrderNotFoundException;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.ports.in.CreateOrderUseCase;
import application.domain.ports.in.RegisterOrderPaymentUseCase;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.OrderRepository;
import application.domain.valueobjects.OrderStatus;

/**
 * Domain Service (Order Management - services.md Create Order / Register
 * Order Payment): complements OrderProcessingService (cart/confirm-payment
 * cycle) with creation and payment registration.
 *
 * Business rules enforced:
 * - Create: buyer must be enabled (canPurchase), cart non-empty.
 * - Payment: PENDIENTE_DE_PAGO -&gt; PAGADO; paidAmount == totalAmount
 *   (compareTo, never equals); invoice issued by the model.
 */
public class OrderPaymentService implements CreateOrderUseCase,
        RegisterOrderPaymentUseCase {

    private final BuyerRepository buyerRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public OrderPaymentService(BuyerRepository buyerRepository,
                               OrderRepository orderRepository,
                               NotificationService notificationService) {
        if (buyerRepository == null || orderRepository == null
                || notificationService == null) {
            throw new IllegalArgumentException("Dependencies must not be null");
        }
        this.buyerRepository = buyerRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Order createOrder(String buyerId, String deliveryAddress) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Buyer not found: " + buyerId));
        if (!buyer.canPurchase()) {
            throw new InvalidOrderException(
                    "Buyer " + buyerId + " is not enabled to create orders");
        }
        ShoppingCart cart = buyer.getCart();
        if (cart.getItems().isEmpty()) {
            throw new InvalidOrderException(
                    "Cannot create an order with an empty cart");
        }
        Order order = new Order(buyer, cart, deliveryAddress);
        Order saved = orderRepository.save(order);
        cart.clear();
        buyerRepository.save(buyer);
        return saved;
    }

    @Override
    public Order registerOrderPayment(Integer orderId, BigDecimal paidAmount) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found: " + orderId));
        if (!OrderStatus.PENDIENTE_DE_PAGO.equals(order.getOrderStatus())) {
            throw new InvalidOrderException(
                    "Only orders pending payment can register payment (current: "
                            + order.getOrderStatus().getCode() + ")");
        }
        if (paidAmount == null || paidAmount.compareTo(BigDecimal.ZERO) <= 0
                || paidAmount.compareTo(order.getTotalAmount()) != 0) {
            throw new InvalidOrderException(
                    "Paid amount must equal the order total: expected="
                            + order.getTotalAmount() + ", received=" + paidAmount);
        }
        order.confirmPayment();
        Order saved = orderRepository.save(order);
        notificationService.notifyBuyer(saved.getBuyer().getEmail(),
                "Payment registered",
                "Payment of " + paidAmount + " for order " + orderId
                        + " was registered. Invoice "
                        + saved.getInvoice().getInvoiceId() + " issued.");
        return saved;
    }
}
