package application.domain.services;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.ports.in.AddToCartUseCase;
import application.domain.ports.in.ConfirmPaymentUseCase;
import application.domain.ports.in.PlaceOrderUseCase;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.OrderRepository;
import application.domain.ports.out.ProductRepository;

/**
 * Domain Service: coordinates the central commercial cycle of the
 * marketplace - cart management, order placement and payment confirmation -
 * preserving the integrity of the domain.
 *
 * Implements the input ports AddToCartUseCase, PlaceOrderUseCase and
 * ConfirmPaymentUseCase.
 */
public class OrderProcessingService implements AddToCartUseCase, PlaceOrderUseCase,
        ConfirmPaymentUseCase {

    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public OrderProcessingService(BuyerRepository buyerRepository,
                                  ProductRepository productRepository,
                                  OrderRepository orderRepository,
                                  NotificationService notificationService) {
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    @Override
    public ShoppingCart addToCart(String buyerId, String productId, int quantity) {
        Buyer buyer = requireBuyer(buyerId);
        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer " + buyerId + " is not enabled to make purchases");
        }
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Product not found: " + productId));
        buyer.getCart().addProduct(product, quantity);
        buyerRepository.save(buyer);
        return buyer.getCart();
    }

    @Override
    public ShoppingCart removeFromCart(String buyerId, String productId) {
        Buyer buyer = requireBuyer(buyerId);
        buyer.getCart().removeProduct(productId);
        buyerRepository.save(buyer);
        return buyer.getCart();
    }

    @Override
    public Order placeOrder(String buyerId, String deliveryAddress) {
        Buyer buyer = requireBuyer(buyerId);
        if (!buyer.canPurchase()) {
            throw new IllegalStateException(
                    "Buyer " + buyerId + " is not enabled to make purchases");
        }
        ShoppingCart cart = buyer.getCart();
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot place an order with an empty cart");
        }
        Order order = new Order(buyer, cart, deliveryAddress);
        Order saved = orderRepository.save(order);
        cart.clear();
        buyerRepository.save(buyer);
        return saved;
    }

    @Override
    public Order confirmPayment(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
        order.confirmPayment();
        Order saved = orderRepository.save(order);
        notificationService.notifyBuyer(
                saved.getBuyer().getEmail(),
                "Payment confirmed",
                "Your payment for order " + saved.getOrderId()
                        + " was confirmed. Invoice " + saved.getInvoice().getInvoiceId()
                        + " has been issued.");
        return saved;
    }

    private Buyer requireBuyer(String buyerId) {
        return buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found: " + buyerId));
    }
}