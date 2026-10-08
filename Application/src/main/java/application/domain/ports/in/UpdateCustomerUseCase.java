package application.domain.ports.in;

import application.domain.models.Buyer;
import application.domain.models.Person;

/**
 * Input Port (Customer Management): updates the information maintained for
 * an existing marketplace customer.
 */
public interface UpdateCustomerUseCase {

    Person updateCustomer(String personId, String fullName, String email);

    Buyer updateBuyerAddress(String buyerId, String mainAddress);
}