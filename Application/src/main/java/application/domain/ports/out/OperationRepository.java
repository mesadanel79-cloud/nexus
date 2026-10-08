package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.SystemUser;

/**
 * Output Port: persistence contract for the operation history of the
 * marketplace (Operation and Audit subdomain). Implemented by an output
 * adapter; the domain never accesses the database directly.
 */
public interface OperationRepository {

    Operation save(Operation operation);

    Optional<Operation> findById(String operationId);

    List<Operation> findAll();

    List<Operation> findByAffectedAsset(MarketplaceAsset asset);

    List<Operation> findByPerformer(SystemUser user);
}
