package application.domain.ports.in;

import application.domain.models.Shipment;

/**
 * Input Port (Shipping Management): submits a shipment in preparation
 * into an approval state when business rules require authorization
 * before dispatch.
 */
public interface SubmitShipmentForApprovalUseCase {

    Shipment submitForApproval(String shipmentId, String operatorId);
}
