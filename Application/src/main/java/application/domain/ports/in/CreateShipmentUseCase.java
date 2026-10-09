package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port (Shipping Management): creates a shipment request for an order
 * and establishes its initial state.
 */
public interface CreateShipmentUseCase {

    Shipment createShipment(Integer orderId, String operatorId);
}
