package application.adapters.out.persistence.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * JPA Entity for the shipments table (MySQL).
 */
@Entity
@Table(name = "shipments")
public class ShipmentEntity {

    @Id
    @Column(name = "shipment_id", length = 20, nullable = false, unique = true)
    private String shipmentId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "logistics_operator_identifier", nullable = false)
    private String logisticsOperatorIdentifier;

    @Column(name = "shipment_status_code", nullable = false)
    private String shipmentStatusCode;

    @Column(name = "dispatch_date")
    private LocalDateTime dispatchDate;

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate;

    public ShipmentEntity() {
    }

    public ShipmentEntity(String shipmentId, Integer orderId,
                          String logisticsOperatorIdentifier,
                          String shipmentStatusCode,
                          LocalDateTime dispatchDate,
                          LocalDateTime deliveryDate) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.logisticsOperatorIdentifier = logisticsOperatorIdentifier;
        this.shipmentStatusCode = shipmentStatusCode;
        this.dispatchDate = dispatchDate;
        this.deliveryDate = deliveryDate;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(String shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getLogisticsOperatorIdentifier() {
        return logisticsOperatorIdentifier;
    }

    public void setLogisticsOperatorIdentifier(String logisticsOperatorIdentifier) {
        this.logisticsOperatorIdentifier = logisticsOperatorIdentifier;
    }

    public String getShipmentStatusCode() {
        return shipmentStatusCode;
    }

    public void setShipmentStatusCode(String shipmentStatusCode) {
        this.shipmentStatusCode = shipmentStatusCode;
    }

    public LocalDateTime getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(LocalDateTime dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}