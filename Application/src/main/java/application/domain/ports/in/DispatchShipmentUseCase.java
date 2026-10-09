package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port: manages the logistics lifecycle of a shipment handled by a
 * logistics operator (dispatch, transit, delivery).
 *
 * Shipment creation lives in its own port (CreateShipmentUseCase) so every
 * domain service keeps a single responsibility.
 */
public interface DispatchShipmentUseCase {

    Shipment dispatchShipment(String shipmentId, String operatorId);

    Shipment markShipmentInTransit(String shipmentId, String operatorId);

    Shipment markShipmentDelivered(String shipmentId, String operatorId);
}