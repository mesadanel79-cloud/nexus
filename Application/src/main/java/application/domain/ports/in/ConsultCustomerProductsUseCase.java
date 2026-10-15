package application.domain.ports.in;

import java.util.List;

import application.domain.models.Product;
import application.domain.models.Shipment;
import application.domain.models.Order;

/**
 * Input Port (Customer Management - SDD Consult Customer Products):
 * retrieves the marketplace products and activity associated with a
 * customer, such as listed catalog items, orders and shipments.
 */
public interface ConsultCustomerProductsUseCase {

    /** Catalog items published by a seller. */
    List<Product> consultCustomerProducts(String sellerId);

    /** Shipments associated with the orders of a buyer. */
    List<Shipment> consultCustomerShipments(String buyerId);

    /** Full activity snapshot: orders placed by a buyer. */
    List<Order> consultCustomerActivity(String buyerId);
}
