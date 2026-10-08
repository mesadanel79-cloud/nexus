package application.domain.ports.in;

import java.util.List;

import application.domain.models.Order;
import application.domain.models.Person;

/**
 * Input Port (Customer Management): retrieves customer information and the
 * marketplace activity associated with a customer.
 */
public interface ConsultCustomerUseCase {

    Person consultCustomer(String personId);

    /** Products/catalog activity: orders placed by a buyer. */
    List<Order> consultCustomerOrders(String buyerId);
}