package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.LogisticsOperator;
import application.domain.ports.out.LogisticsOperatorRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * LogisticsOperatorRepository port (operator directory used by the shipping
 * services).
 */
@Component
public class LogisticsOperatorRepositoryAdapter
        implements LogisticsOperatorRepository {

    private final Map<String, LogisticsOperator> operatorsById =
            new ConcurrentHashMap<>();

    @Override
    public LogisticsOperator save(LogisticsOperator operator) {
        operatorsById.put(operator.getIdentifier(), operator);
        return operator;
    }

    @Override
    public Optional<LogisticsOperator> findById(String operatorId) {
        return Optional.ofNullable(operatorsById.get(operatorId));
    }

    @Override
    public List<LogisticsOperator> findAll() {
        return List.copyOf(operatorsById.values());
    }
}
