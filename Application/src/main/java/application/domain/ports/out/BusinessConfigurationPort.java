package application.domain.ports.out;

import java.math.BigDecimal;

/**
 * Output Port (Shipping Management): business configuration required by
 * shipment services, such as the approval threshold that decides whether
 * a shipment must go through WAITING_FOR_APPROVAL before dispatch.
 * The domain defines what it needs; infrastructure decides how it is stored.
 */
public interface BusinessConfigurationPort {

    /** Amount above which a shipment requires explicit approval. */
    BigDecimal shipmentApprovalThreshold();

    /** True when the order amount requires approval before dispatch. */
    default boolean requiresShipmentApproval(BigDecimal orderTotal) {
        if (orderTotal == null) {
            return false;
        }
        return orderTotal.compareTo(shipmentApprovalThreshold()) > 0;
    }
}
