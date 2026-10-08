package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.SystemUser;
import application.domain.ports.out.OperationRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * OperationRepository port. Adapters implement ports but never define
 * business rules. Replaceable by a MySQL adapter without touching the
 * domain.
 */
@Component
public class OperationRepositoryAdapter implements OperationRepository {

    private final Map<String, Operation> operations = new ConcurrentHashMap<>();

    @Override
    public Operation save(Operation operation) {
        operations.put(operation.getOperationId(), operation);
        return operation;
    }

    @Override
    public Optional<Operation> findById(String operationId) {
        return Optional.ofNullable(operations.get(operationId));
    }

    @Override
    public List<Operation> findAll() {
        return List.copyOf(operations.values());
    }

    @Override
    public List<Operation> findByAffectedAsset(MarketplaceAsset asset) {
        return operations.values().stream()
                .filter(operation -> sameAsset(operation.getAffectedAsset(),
                        asset))
                .toList();
    }

    @Override
    public List<Operation> findByPerformer(SystemUser user) {
        return operations.values().stream()
                .filter(operation -> operation.getPerformedBy() != null
                        && operation.getPerformedBy().getUsername()
                                .equals(user.getUsername()))
                .toList();
    }

    private boolean sameAsset(MarketplaceAsset first, MarketplaceAsset second) {
        return first != null && second != null
                && first.getAssetType().equals(second.getAssetType())
                && first.getAssetIdentifier()
                        .equals(second.getAssetIdentifier());
    }
}
