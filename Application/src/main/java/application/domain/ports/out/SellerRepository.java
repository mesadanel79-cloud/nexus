package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Seller;

/**
 * Output Port: persistence contract for sellers.
 */
public interface SellerRepository {

    Seller save(Seller seller);

    Optional<Seller> findById(String identifier);

    Optional<Seller> findByEmail(String email);

    List<Seller> findAll();
}