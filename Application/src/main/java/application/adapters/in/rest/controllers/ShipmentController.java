package application.adapters.in.rest.controllers;

import application.adapters.in.rest.mappers.RestMapper;
import application.adapters.in.rest.responses.ShipmentResponse;
import application.domain.models.Shipment;
import application.domain.ports.in.DispatchShipmentUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes shipment logistics endpoints handled by
 * logistics operators.
 */
@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final DispatchShipmentUseCase dispatchShipmentUseCase;

    public ShipmentController(DispatchShipmentUseCase dispatchShipmentUseCase) {
        this.dispatchShipmentUseCase = dispatchShipmentUseCase;
    }

    @PostMapping
    public ResponseEntity<ShipmentResponse> createShipment(
            @RequestParam Integer orderId,
            @RequestParam String operatorId) {
        Shipment shipment = dispatchShipmentUseCase.createShipment(orderId, operatorId);
        return ResponseEntity.ok(RestMapper.toShipmentResponse(shipment));
    }

    @PostMapping("/{shipmentId}/dispatch")
    public ResponseEntity<ShipmentResponse> dispatch(
            @PathVariable String shipmentId,
            @RequestParam String operatorId) {
        Shipment shipment = dispatchShipmentUseCase.dispatchShipment(shipmentId, operatorId);
        return ResponseEntity.ok(RestMapper.toShipmentResponse(shipment));
    }

    @PostMapping("/{shipmentId}/transit")
    public ResponseEntity<ShipmentResponse> markInTransit(
            @PathVariable String shipmentId,
            @RequestParam String operatorId) {
        Shipment shipment = dispatchShipmentUseCase.markShipmentInTransit(shipmentId, operatorId);
        return ResponseEntity.ok(RestMapper.toShipmentResponse(shipment));
    }

    @PostMapping("/{shipmentId}/delivery")
    public ResponseEntity<ShipmentResponse> markDelivered(
            @PathVariable String shipmentId,
            @RequestParam String operatorId) {
        Shipment shipment = dispatchShipmentUseCase.markShipmentDelivered(shipmentId, operatorId);
        return ResponseEntity.ok(RestMapper.toShipmentResponse(shipment));
    }
}