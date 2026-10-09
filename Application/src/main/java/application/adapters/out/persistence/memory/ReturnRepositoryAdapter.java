package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Return;
import application.domain.ports.out.ReturnRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * ReturnRepository port shared by the return request, evaluation, refund and
 * closure services.
 */
@Component
public class ReturnRepositoryAdapter implements ReturnRepository {

    private final Map<String, Return> returnsById = new ConcurrentHashMap<>();

    @Override
    public Return save(Return returnRequest) {
        returnsById.put(returnRequest.getReturnId(), returnRequest);
        return returnRequest;
    }

    @Override
    public Optional<Return> findById(String returnId) {
        return Optional.ofNullable(returnsById.get(returnId));
    }

    @Override
    public List<Return> findAll() {
        return List.copyOf(returnsById.values());
    }
}
