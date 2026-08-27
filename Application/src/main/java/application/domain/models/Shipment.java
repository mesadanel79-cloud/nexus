package application.domain.models;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import application.domain.valueobjects.ShipmentStatus;

/**
 * Shipment - Domain Model.
 *
 * Represents the logistics process of dispatch, transport and delivery
 * for an order containing physical products.
 *
 * Lifecycle: EN_PREPARACION -> DESPACHADO -> EN_TRANSITO -> ENTREGADO.
 *
 * Relationships:
 * - A Shipment is generated from one Order.
 * - A Shipment is managed by one LogisticsOperator.
 */
public class Shipment {

    private static final AtomicLong SEQUENCE = new AtomicLong(90000);

    private final String shipmentId;
    private final Order order;
    private final LogisticsOperator logisticsOperator;
    private ShipmentStatus shipmentStatus;
    private LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;

    /** Generated for orders that include physical products. */
    public Shipment(Order order, LogisticsOperator logisticsOperator) {
        if (order == null) {
            throw new IllegalArgumentException("Shipment order must not be null");
        }
        if (logisticsOperator == null) {
            throw new IllegalArgumentException("Shipment logistics operator must not be null");
        }
        this.shipmentId = "SHP-" + SEQUENCE.incrementAndGet();
        this.order = order;
        this.logisticsOperator = logisticsOperator;
        this.shipmentStatus = ShipmentStatus.EN_PREPARACION;
        order.attachShipment(this);
    }

    /** Reconstitution constructor (persistence). */
    public Shipment(String shipmentId, Order order,
                    LogisticsOperator logisticsOperator,
                    ShipmentStatus shipmentStatus, LocalDateTime dispatchDate,
                    LocalDateTime deliveryDate) {
        this.shipmentId = shipmentId;
        this.order = order;
        this.logisticsOperator = logisticsOperator;
        this.shipmentStatus = shipmentStatus;
        this.dispatchDate = dispatchDate;
        this.deliveryDate = deliveryDate;
        order.attachShipment(this);
    }

    /** Unique shipment identifier. */
    public String getShipmentId() {
        return shipmentId;
    }

    /** Order associated with the shipment. */
    public Order getOrder() {
        return order;
    }

    /** Operator responsible for the dispatch. */
    public LogisticsOperator getLogisticsOperator() {
        return logisticsOperator;
    }

    /** Current status of the shipment. */
    public ShipmentStatus getShipmentStatus() {
        return shipmentStatus;
    }

    /** Date and time of physical departure from the warehouse. */
    public LocalDateTime getDispatchDate() {
        return dispatchDate;
    }

    /** Date and time of confirmed delivery. */
    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * Physical departure from the warehouse: EN_PREPARACION -> DESPACHADO.
     * The associated order transitions to DISPATCHED as well.
     */
    public void dispatch() {
        ensureStatus(ShipmentStatus.EN_PREPARACION, "Only shipments in preparation can be dispatched");
        this.shipmentStatus = ShipmentStatus.DESPACHADO;
        this.dispatchDate = LocalDateTime.now();
        order.markDispatched();
    }

    /** DESPACHADO -> EN_TRANSITO. */
    public void markInTransit() {
        ensureStatus(ShipmentStatus.DESPACHADO, "Only dispatched shipments can be set in transit");
        this.shipmentStatus = ShipmentStatus.EN_TRANSITO;
    }

    /**
     * EN_TRANSITO -> ENTREGADO. The associated order transitions to
     * DELIVERED_COMPLETED.
     */
    public void markDelivered() {
        ensureStatus(ShipmentStatus.EN_TRANSITO, "Only in-transit shipments can be delivered");
        this.shipmentStatus = ShipmentStatus.ENTREGADO;
        this.deliveryDate = LocalDateTime.now();
        order.markDelivered();
    }

    private void ensureStatus(ShipmentStatus expected, String message) {
        if (!expected.equals(shipmentStatus)) {
            throw new IllegalStateException(message + " (current status: "
                    + shipmentStatus.getCode() + ")");
        }
    }
}