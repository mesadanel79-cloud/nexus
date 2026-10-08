package application.domain.ports.in;

import java.util.List;

import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.SystemUser;

/**
 * Input Port (Operation and Audit subdomain): retrieves operations recorded
 * by the system for traceability purposes.
 *
 * Queries are expressed with Domain Models (the affected asset or the
 * performing user); persistence representations are never exposed.
 */
public interface ConsultOperationUseCase {

    /** Returns the operation identified by its operation identifier. */
    Operation consultOperation(String operationId);

    /** Returns every registered operation. */
    List<Operation> consultOperations();

    /** Returns the operations performed over the given marketplace asset. */
    List<Operation> consultOperationsByAsset(MarketplaceAsset asset);

    /** Returns the operations performed by the given system user. */
    List<Operation> consultOperationsByPerformer(SystemUser user);
}
