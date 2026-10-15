package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port (Shipping Management): expires a shipment awaiting approval
 * whose approval window has elapsed.
 */
public interface ExpireShipmentUseCase {

    Shipment expireShipment(String shipmentId);
}
