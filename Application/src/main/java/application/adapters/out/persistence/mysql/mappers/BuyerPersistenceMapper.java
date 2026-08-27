package application.adapters.out.persistence.mysql.mappers;

import application.adapters.out.persistence.mysql.entities.BuyerEntity;
import application.domain.models.Buyer;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Mapper: converts between the Buyer domain model and the BuyerEntity
 * persistence entity. Domain models never leak into the database schema
 * and entities never leak into the domain.
 */
public final class BuyerPersistenceMapper {

    private BuyerPersistenceMapper() {
    }

    public static BuyerEntity toEntity(Buyer buyer) {
        return new BuyerEntity(
                buyer.getIdentifier(),
                buyer.getFullName(),
                buyer.getEmail(),
                buyer.getRole().getCode(),
                buyer.getStatus().getCode(),
                buyer.getCommercialStatus().getCode(),
                buyer.getMainAddress());
    }

    public static Buyer toDomain(BuyerEntity entity) {
        return new Buyer(
                entity.getIdentifier(),
                entity.getFullName(),
                entity.getEmail(),
                UserStatus.fromCode(entity.getStatusCode()),
                entity.getMainAddress());
    }
}