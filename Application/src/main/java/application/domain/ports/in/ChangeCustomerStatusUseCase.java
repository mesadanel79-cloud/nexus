package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Seller;

/**
 * Input Port (Customer Management): changes the operational status of a
 * customer, such as activating, suspending, or blocking the customer's
 * marketplace relationship.
 */
public interface ChangeCustomerStatusUseCase {

    /** Suspends/enables a buyer's purchasing capability (CommercialStatus). */
    Buyer changeBuyerCommercialStatus(String buyerId, String statusCode);

    /** Changes a seller's marketplace status (SellerStatus). */
    Seller changeSellerStatus(String sellerId, String statusCode);
}