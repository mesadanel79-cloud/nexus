package application.domain.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.ports.in.DispatchShipmentUseCase;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.OrderRepository;

/**
 * Domain Service: coordinates the logistics lifecycle of shipments
 * (dispatch, transport, delivery) handled by logistics operators.
 *
 * Business rules enforced:
 * - Shipments are generated only for orders containing physical products.
 * - Only paid orders can enter dispatch.
 * - Delivery completion transitions the order to DELIVERED_COMPLETED, after
 *   which it becomes immutable.
 */
public class ShipmentDispatchService implements DispatchShipmentUseCase {

    private final OrderRepository orderRepository;
    private final NotificationService notificationService;
    private final InventoryReservationService inventoryReservationService;
    private final Map<String, LogisticsOperator> operatorDirectory = new ConcurrentHashMap<>();
    private final Map<String, Shipment> shipmentRegistry = new ConcurrentHashMap<>();

    public ShipmentDispatchService(OrderRepository orderRepository,
                                   NotificationService notificationService,
                                   InventoryReservationService inventoryReservationService) {
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
        this.inventoryReservationService = inventoryReservationService;
    }

    /** Registers a logistics operator in the in-memory directory. */
    public void registerOperator(LogisticsOperator operator) {
        operatorDirectory.put(operator.getIdentifier(), operator);
    }

    @Override
    public Shipment createShipment(Integer orderId, String operatorId) {
        Order order = requireOrder(orderId);
        if (!order.containsPhysicalProducts()) {
            throw new IllegalStateException(
                    "Shipments are generated only for orders containing physical products");
        }
        LogisticsOperator operator = requireOperator(operatorId);
        inventoryReservationService.registerSaleOutflow(order);
        Shipment shipment = new Shipment(order, operator);
        shipmentRegistry.put(shipment.getShipmentId(), shipment);
        return shipment;
    }

    @Override
    public Shipment dispatchShipment(String shipmentId, String operatorId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(operatorId);
        shipment.dispatch();
        notificationService.notifyBuyer(shipment.getOrder().getBuyer().getEmail(),
                "Order dispatched",
                "Your order " + shipment.getOrder().getOrderId()
                        + " has left the warehouse.");
        return shipment;
    }

    @Override
    public Shipment markShipmentInTransit(String shipmentId, String operatorId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(operatorId);
        shipment.markInTransit();
        return shipment;
    }

    @Override
    public Shipment markShipmentDelivered(String shipmentId, String operatorId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(operatorId);
        shipment.markDelivered();
        orderRepository.save(shipment.getOrder());
        notificationService.notifyBuyer(shipment.getOrder().getBuyer().getEmail(),
                "Order delivered",
                "Your order " + shipment.getOrder().getOrderId()
                        + " was delivered successfully.");
        return shipment;
    }

    private Order requireOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    private LogisticsOperator requireOperator(String operatorId) {
        LogisticsOperator operator = operatorDirectory.get(operatorId);
        if (operator == null) {
            throw new IllegalArgumentException("Logistics operator not registered: " + operatorId);
        }
        return operator;
    }

    private Shipment requireShipment(String shipmentId) {
        Shipment shipment = shipmentRegistry.get(shipmentId);
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found: " + shipmentId);
        }
        return shipment;
    }
}