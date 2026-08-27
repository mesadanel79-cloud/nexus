package application.domain.ports.in;

import application.domain.enums.ReturnReason;
import application.domain.models.Return;

/**
 * Input Port: registers a return request made by the buyer for a
 * delivered/completed order.
 */
public interface RequestReturnUseCase {

    Return requestReturn(String buyerId, Integer orderId, ReturnReason reason);
}