package application.adapters.out.persistence.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Refund;
import application.domain.ports.out.RefundRepository;
import org.springframework.stereotype.Component;

/**
 * In-memory adapter for the RefundRepository output port (tests / local
 * execution without infrastructure).
 */
@Component
public class RefundRepositoryAdapter implements RefundRepository {

    private final Map<String, Refund> store = new ConcurrentHashMap<>();

    @Override
    public Refund save(Refund refund) {
        store.put(refund.getRefundId(), refund);
        return refund;
    }

    @Override
    public Optional<Refund> findById(String refundId) {
        return Optional.ofNullable(store.get(refundId));
    }

    @Override
    public List<Refund> findByReturnId(String returnId) {
        return store.values().stream()
                .filter(refund -> refund.getReturnRequest().getReturnId().equals(returnId))
                .toList();
    }

    @Override
    public List<Refund> findAll() {
        return new ArrayList<>(store.values());
    }
}
