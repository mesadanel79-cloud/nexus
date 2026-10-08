package application.domain.ports.in;

import application.domain.models.Person;
import application.domain.models.SystemUser;

/**
 * Input Port (Authorization subdomain): determines whether a system user is
 * authorized to access information belonging to a specific marketplace
 * customer (buyer or seller).
 *
 * The customer is always received as a Domain Model (Person), and the
 * relationship between the user and the customer is evaluated through the
 * User domain model rather than through raw identifiers.
 */
public interface ValidateCustomerAccessUseCase {

    /** True when the user may consult the information of the customer. */
    boolean canConsultCustomer(SystemUser user, Person customer);

    /**
     * Validates customer access and fails with
     * UnauthorizedOperationException when the user is not allowed to
     * consult the customer.
     */
    void ensureCanConsultCustomer(SystemUser user, Person customer);
}
