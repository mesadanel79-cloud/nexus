package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port (Shipping Management): rejects a shipment awaiting approval
 * and records the corresponding business operation.
 */
public interface RejectShipmentUseCase {

    Shipment rejectShipment(String shipmentId, String approverId, String reason);
}
