package application.adapters.out.persistence.mysql.adapters;

import application.adapters.out.persistence.mysql.entities.SellerEntity;
import application.adapters.out.persistence.mysql.repositories.SellerJpaRepository;
import application.domain.models.Administrator;
import application.domain.models.Seller;
import application.domain.ports.out.SellerRepository;
import application.domain.valueobjects.UserStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (MySQL): implements the SellerRepository output port
 * using JPA.
 */
@Component
public class SellerRepositoryAdapter implements SellerRepository {

    private final SellerJpaRepository jpaRepository;

    public SellerRepositoryAdapter(SellerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Seller save(Seller seller) {
        SellerEntity entity = new SellerEntity(
                seller.getIdentifier(),
                seller.getFullName(),
                seller.getEmail(),
                seller.getRole().getCode(),
                seller.getStatus().getCode(),
                seller.getSellerStatus().getCode(),
                seller.getOnboardingDate(),
                seller.getRegisteredBy() != null
                        ? seller.getRegisteredBy().getIdentifier()
                        : null);
        jpaRepository.save(entity);
        return seller;
    }

    @Override
    public Optional<Seller> findById(String identifier) {
        return jpaRepository.findById(identifier).map(this::toDomain);
    }

    @Override
    public Optional<Seller> findByEmail(String email) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getEmail().equals(email))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<Seller> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    /**
     * Reconstitutes the domain seller. The registering administrator is not
     * rehydrated (kept as a lightweight reference) because onboarding data
     * is historical.
     */
    private Seller toDomain(SellerEntity entity) {
        Administrator placeholder = new Administrator("SYSTEM", "System",
                "system@nexusmarket.local", UserStatus.ACTIVO);
        return Seller.reconstitute(entity.getIdentifier(), entity.getFullName(),
                entity.getEmail(), UserStatus.fromCode(entity.getStatusCode()),
                placeholder);
    }
}