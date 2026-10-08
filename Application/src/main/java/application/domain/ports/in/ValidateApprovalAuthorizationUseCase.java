package application.domain.ports.in;

import application.domain.models.Return;
import application.domain.models.Shipment;
import application.domain.models.SystemUser;

/**
 * Input Port (Authorization subdomain): determines whether a system user
 * has the required authority to approve a business operation that requires
 * authorization, such as a return or a shipment.
 *
 * Approval authorization is separate from the approval operation itself:
 * the business service keeps the responsibility of executing the approval
 * once authorization has been granted.
 */
public interface ValidateApprovalAuthorizationUseCase {

    /**
     * True when the user may approve the return: active user,
     * administrator authority and a return under review.
     */
    boolean canApproveReturn(SystemUser user, Return returnRequest);

    /**
     * True when the user may approve the shipment: active user, approval
     * authority and a shipment waiting for approval.
     */
    boolean canApproveShipment(SystemUser user, Shipment shipment);

    /**
     * Validates return approval authorization and fails with
     * UnauthorizedOperationException when it is not allowed.
     */
    void ensureCanApproveReturn(SystemUser user, Return returnRequest);

    /**
     * Validates shipment approval authorization and fails with
     * UnauthorizedOperationException when it is not allowed.
     */
    void ensureCanApproveShipment(SystemUser user, Shipment shipment);
}
