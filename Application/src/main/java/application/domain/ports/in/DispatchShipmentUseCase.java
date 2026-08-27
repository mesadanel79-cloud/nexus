package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port: manages the logistics lifecycle of a shipment handled by a
 * logistics operator (dispatch, transit, delivery).
 */
public interface DispatchShipmentUseCase {

    Shipment createShipment(Integer orderId, String operatorId);

    Shipment dispatchShipment(String shipmentId, String operatorId);

    Shipment markShipmentInTransit(String shipmentId, String operatorId);

    Shipment markShipmentDelivered(String shipmentId, String operatorId);
}