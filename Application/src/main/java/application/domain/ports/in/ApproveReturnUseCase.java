package application.domain.ports.in;

import application.domain.models.Refund;
import application.domain.models.Return;

/**
 * Input Port: evaluates a return request (approve/reject) performed by
 * internal staff. Approval generates a Refund.
 */
public interface ApproveReturnUseCase {

    Return markUnderReview(String returnId);

    Refund approveReturn(String returnId);

    Return rejectReturn(String returnId);
}