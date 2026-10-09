package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port (Shipping Management): retrieves the current status and
 * movement history of a shipment.
 */
public interface TrackShipmentUseCase {

    Shipment trackShipment(String shipmentId);
}
