package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Return;

/**
 * Output Port: persistence of Return Domain Models. Request, evaluation,
 * refund and closure services share this repository instead of keeping a
 * private registry inside one service.
 */
public interface ReturnRepository {

    Return save(Return returnRequest);

    Optional<Return> findById(String returnId);

    List<Return> findAll();
}
