package application.domain.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.LogisticsOperator;
import application.domain.models.Order;
import application.domain.models.Shipment;
import application.domain.ports.in.ApproveShipmentUseCase;
import application.domain.ports.in.CreateShipmentUseCase;
import application.domain.ports.in.DispatchShipmentUseCase;
import application.domain.ports.in.ExpireShipmentUseCase;
import application.domain.ports.in.RejectShipmentUseCase;
import application.domain.ports.in.SubmitShipmentForApprovalUseCase;
import application.domain.ports.in.TrackShipmentUseCase;
import application.domain.ports.out.LogisticsOperatorRepository;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.OrderRepository;
import application.domain.ports.out.ShipmentRepository;

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
public class ShipmentDispatchService implements CreateShipmentUseCase,
        DispatchShipmentUseCase, SubmitShipmentForApprovalUseCase,
        ApproveShipmentUseCase, RejectShipmentUseCase, ExpireShipmentUseCase,
        TrackShipmentUseCase {

    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;
    private final LogisticsOperatorRepository operatorRepository;
    private final NotificationService notificationService;
    private final InventoryReservationService inventoryReservationService;
    private final Map<String, LogisticsOperator> operatorDirectory = new ConcurrentHashMap<>();
    private final Map<String, Shipment> shipmentRegistry = new ConcurrentHashMap<>();

    public ShipmentDispatchService(OrderRepository orderRepository,
                                   NotificationService notificationService,
                                   InventoryReservationService inventoryReservationService) {
        this(orderRepository, null, null, notificationService, inventoryReservationService);
    }

    public ShipmentDispatchService(OrderRepository orderRepository,
                                   ShipmentRepository shipmentRepository,
                                   LogisticsOperatorRepository operatorRepository,
                                   NotificationService notificationService,
                                   InventoryReservationService inventoryReservationService) {
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
        this.operatorRepository = operatorRepository;
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
        storeShipment(shipment);
        return shipment;
    }

    @Override
    public Shipment trackShipment(String shipmentId) {
        return requireShipment(shipmentId);
    }

    @Override
    public Shipment submitForApproval(String shipmentId, String operatorId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(operatorId);
        shipment.submitForApproval();
        return storeShipment(shipment);
    }

    @Override
    public Shipment approveShipment(String shipmentId, String approverId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(approverId);
        shipment.approve();
        return storeShipment(shipment);
    }

    @Override
    public Shipment rejectShipment(String shipmentId, String approverId, String reason) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(approverId);
        shipment.reject();
        return storeShipment(shipment);
    }

    @Override
    public Shipment expireShipment(String shipmentId) {
        Shipment shipment = requireShipment(shipmentId);
        shipment.expire();
        return storeShipment(shipment);
    }

    @Override
    public Shipment dispatchShipment(String shipmentId, String operatorId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(operatorId);
        shipment.dispatch();
        // dispatch() also transitions the order to DESPACHADO: persist the
        // order so the repository never keeps a stale lifecycle status.
        orderRepository.save(shipment.getOrder());
        storeShipment(shipment);
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
        return storeShipment(shipment);
    }

    @Override
    public Shipment markShipmentDelivered(String shipmentId, String operatorId) {
        Shipment shipment = requireShipment(shipmentId);
        requireOperator(operatorId);
        shipment.markDelivered();
        orderRepository.save(shipment.getOrder());
        storeShipment(shipment);
        notificationService.notifyBuyer(shipment.getOrder().getBuyer().getEmail(),
                "Order delivered",
                "Your order " + shipment.getOrder().getOrderId()
                        + " was delivered successfully.");
        return shipment;
    }

    private Shipment storeShipment(Shipment shipment) {
        shipmentRegistry.put(shipment.getShipmentId(), shipment);
        if (shipmentRepository != null) {
            return shipmentRepository.save(shipment);
        }
        return shipment;
    }

    private Order requireOrder(Integer orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    private LogisticsOperator requireOperator(String operatorId) {
        LogisticsOperator operator = operatorDirectory.get(operatorId);
        if (operator == null && operatorRepository != null) {
            operator = operatorRepository.findById(operatorId).orElse(null);
        }
        if (operator == null) {
            throw new IllegalArgumentException("Logistics operator not registered: " + operatorId);
        }
        return operator;
    }

    private Shipment requireShipment(String shipmentId) {
        Shipment shipment = shipmentRegistry.get(shipmentId);
        if (shipment == null && shipmentRepository != null) {
            shipment = shipmentRepository.findById(shipmentId).orElse(null);
            if (shipment != null) {
                shipmentRegistry.put(shipmentId, shipment);
            }
        }
        if (shipment == null) {
            throw new IllegalArgumentException("Shipment not found: " + shipmentId);
        }
        return shipment;
    }
}