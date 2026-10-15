package application.domain.services;

import java.math.BigDecimal;

import application.domain.exceptions.InvalidOrderException;
import application.domain.exceptions.OrderNotFoundException;
import application.domain.models.Order;
import application.domain.models.Warehouse;
import application.domain.ports.in.CloseOrderUseCase;
import application.domain.ports.in.ConfirmOrderUseCase;
import application.domain.ports.in.ConsultOrderStatusUseCase;
import application.domain.ports.in.FulfillOrderUseCase;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.OrderRepository;
import application.domain.ports.out.WarehouseRepository;
import application.domain.valueobjects.OrderStatus;

/**
 * Domain Service (Order Management - lifecycle complement to
 * OrderProcessingService): confirms, fulfills and closes orders following
 * the SDD Order subdomain.
 *
 * Business rules enforced:
 * - Confirm: order must be PENDIENTE_DE_PAGO, buyer enabled, non-empty
 *   cart conversion with totalAmount &gt; 0 and
 *   totalAmount == sum(qty * unitPrice).
 * - Fulfill: only PAGADO orders allocate stock; the warehouse must be
 *   operational; reservation never produces negative stock.
 * - Close: only ENTREGADO_FINALIZADO orders are closed; a completed order
 *   is immutable (Order.ensureModifiable).
 */
public class OrderLifecycleService implements ConfirmOrderUseCase,
        FulfillOrderUseCase, CloseOrderUseCase, ConsultOrderStatusUseCase {

    private final OrderRepository orderRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryReservationService inventoryReservationService;
    private final NotificationService notificationService;

    public OrderLifecycleService(OrderRepository orderRepository,
                                 WarehouseRepository warehouseRepository,
                                 InventoryReservationService inventoryReservationService,
                                 NotificationService notificationService) {
        if (orderRepository == null || warehouseRepository == null
                || inventoryReservationService == null
                || notificationService == null) {
            throw new IllegalArgumentException("Dependencies must not be null");
        }
        this.orderRepository = orderRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryReservationService = inventoryReservationService;
        this.notificationService = notificationService;
    }

    @Override
    public Order confirmOrder(Integer orderId) {
        Order order = requireOrder(orderId);
        if (!OrderStatus.PENDIENTE_DE_PAGO.equals(order.getOrderStatus())) {
            throw new InvalidOrderException(
                    "Only orders pending payment can be confirmed (current: "
                            + order.getOrderStatus().getCode() + ")");
        }
        if (!order.getBuyer().canPurchase()) {
            throw new InvalidOrderException(
                    "Buyer " + order.getBuyer().getIdentifier()
                            + " is not enabled to confirm orders");
        }
        if (order.getItems().isEmpty()) {
            throw new InvalidOrderException("Cannot confirm an empty order: " + orderId);
        }
        BigDecimal expected = order.getItems().stream()
                .map(item -> item.getUnitPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (expected.signum() <= 0
                || order.getTotalAmount().compareTo(expected) != 0) {
            throw new InvalidOrderException(
                    "Order total is inconsistent for order: " + orderId);
        }
        notificationService.notifyBuyer(order.getBuyer().getEmail(),
                "Order confirmed",
                "Your order " + orderId + " was confirmed and awaits payment.");
        return orderRepository.save(order);
    }

    @Override
    public Order fulfillOrder(Integer orderId, String warehouseId) {
        Order order = requireOrder(orderId);
        if (!OrderStatus.PAGADO.equals(order.getOrderStatus())) {
            throw new InvalidOrderException(
                    "Only paid orders can be fulfilled (current: "
                            + order.getOrderStatus().getCode() + ")");
        }
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Warehouse not found: " + warehouseId));
        if (!warehouse.isOperational()) {
            throw new InvalidOrderException(
                    "Warehouse not operational: " + warehouseId);
        }
        inventoryReservationService.reserveStockForOrder(order);
        return orderRepository.save(order);
    }

    @Override
    public Order closeOrder(Integer orderId) {
        Order order = requireOrder(orderId);
        if (!OrderStatus.ENTREGADO_FINALIZADO.equals(order.getOrderStatus())) {
            throw new InvalidOrderException(
                    "Only delivered/completed orders can be closed (current: "
                            + order.getOrderStatus().getCode() + ")");
        }
        notificationService.notifyBuyer(order.getBuyer().getEmail(),
                "Order closed",
                "Your order " + orderId + " lifecycle is now closed.");
        return orderRepository.save(order);
    }

    @Override
    public OrderStatus consultOrderStatus(Integer orderId) {
        return requireOrder(orderId).getOrderStatus();
    }

    @Override
    public Order consultOrderDetails(Integer orderId) {
        return requireOrder(orderId);
    }

    private Order requireOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(
                        "Order not found: " + orderId));
    }
}
