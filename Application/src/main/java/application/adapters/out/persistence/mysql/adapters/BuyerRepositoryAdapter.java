package application.adapters.out.persistence.mysql.adapters;

import application.adapters.out.persistence.mysql.mappers.BuyerPersistenceMapper;
import application.adapters.out.persistence.mysql.repositories.BuyerJpaRepository;
import application.domain.models.Buyer;
import application.domain.ports.out.BuyerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (MySQL): implements the BuyerRepository output port
 * using JPA. Adapters implement ports but never define business rules.
 */
@Component
public class BuyerRepositoryAdapter implements BuyerRepository {

    private final BuyerJpaRepository jpaRepository;

    public BuyerRepositoryAdapter(BuyerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Buyer save(Buyer buyer) {
        jpaRepository.save(BuyerPersistenceMapper.toEntity(buyer));
        return buyer;
    }

    @Override
    public Optional<Buyer> findById(String identifier) {
        return jpaRepository.findById(identifier)
                .map(BuyerPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Buyer> findByEmail(String email) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getEmail().equals(email))
                .findFirst()
                .map(BuyerPersistenceMapper::toDomain);
    }

    @Override
    public List<Buyer> findAll() {
        return jpaRepository.findAll().stream()
                .map(BuyerPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(String identifier) {
        jpaRepository.deleteById(identifier);
    }
}