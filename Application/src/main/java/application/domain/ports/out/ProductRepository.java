package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Product;

/**
 * Output Port: persistence contract for catalog products.
 */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String identifier);

    List<Product> findAll();

    /** Products visible in the public catalog (status PUBLICADO). */
    List<Product> findPublished();
}