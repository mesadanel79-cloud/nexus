package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.LogisticsOperator;

/**
 * Output Port: directory of logistics operators authorized to handle
 * shipments. Operators are resolved by the shipping services through this
 * port.
 */
public interface LogisticsOperatorRepository {

    LogisticsOperator save(LogisticsOperator operator);

    Optional<LogisticsOperator> findById(String operatorId);

    List<LogisticsOperator> findAll();
}
