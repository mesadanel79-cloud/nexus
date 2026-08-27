package application.adapters.in.rest.mappers;

import application.adapters.in.rest.responses.InventoryResponse;
import application.adapters.in.rest.responses.OrderResponse;
import application.adapters.in.rest.responses.ShipmentResponse;
import application.domain.models.Inventory;
import application.domain.models.Order;
import application.domain.models.Shipment;

/**
 * Mapper: converts between Domain Models and Request/Response DTOs so the
 * domain never depends on transport objects.
 */
public final class RestMapper {

    private RestMapper() {
    }

    public static OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getBuyer().getIdentifier(),
                order.getOrderStatus().getCode(),
                order.getCreationDate(),
                order.getPaymentConfirmationDate(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getInvoice() != null
                        ? order.getInvoice().getInvoiceId()
                        : null);
    }

    public static InventoryResponse toInventoryResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProduct().getIdentifier(),
                inventory.getWarehouse().getIdentifier(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.isDamaged());
    }

    public static ShipmentResponse toShipmentResponse(Shipment shipment) {
        return new ShipmentResponse(
                shipment.getShipmentId(),
                shipment.getOrder().getOrderId(),
                shipment.getLogisticsOperator().getIdentifier(),
                shipment.getShipmentStatus().getCode(),
                shipment.getDispatchDate(),
                shipment.getDeliveryDate());
    }
}