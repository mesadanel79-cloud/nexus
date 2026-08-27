package application.adapters.in.rest.responses;

import java.time.LocalDateTime;

/**
 * Response DTO: standardized API representation of a shipment.
 */
public class ShipmentResponse {

    private String shipmentId;
    private Integer orderId;
    private String logisticsOperatorId;
    private String shipmentStatusCode;
    private LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;

    public ShipmentResponse() {
    }

    public ShipmentResponse(String shipmentId, Integer orderId,
                            String logisticsOperatorId,
                            String shipmentStatusCode,
                            LocalDateTime dispatchDate,
                            LocalDateTime deliveryDate) {
        this.shipmentId = shipmentId;
        this.orderId = orderId;
        this.logisticsOperatorId = logisticsOperatorId;
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

    public String getLogisticsOperatorId() {
        return logisticsOperatorId;
    }

    public void setLogisticsOperatorId(String logisticsOperatorId) {
        this.logisticsOperatorId = logisticsOperatorId;
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