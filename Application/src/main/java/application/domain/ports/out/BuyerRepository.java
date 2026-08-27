package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Buyer;

/**
 * Output Port: persistence contract for buyers.
 * The domain owns this interface; adapters implement it.
 */
public interface BuyerRepository {

    Buyer save(Buyer buyer);

    Optional<Buyer> findById(String identifier);

    Optional<Buyer> findByEmail(String email);

    List<Buyer> findAll();

    void deleteById(String identifier);
}