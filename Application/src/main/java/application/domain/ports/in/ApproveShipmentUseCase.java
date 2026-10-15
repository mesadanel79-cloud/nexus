package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port (Shipping Management): approves a shipment that requires
 * authorization and allows it to proceed to dispatch.
 */
public interface ApproveShipmentUseCase {

    Shipment approveShipment(String shipmentId, String approverId);
}
