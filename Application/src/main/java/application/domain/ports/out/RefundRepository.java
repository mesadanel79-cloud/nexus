package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Refund;

/**
 * Output Port: persistence of Refund Domain Models. Request, evaluation
 * and settlement services share this repository instead of keeping
 * private registries inside one service.
 */
public interface RefundRepository {

    Refund save(Refund refund);

    Optional<Refund> findById(String refundId);

    List<Refund> findByReturnId(String returnId);

    List<Refund> findAll();
}
