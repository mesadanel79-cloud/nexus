package application.domain.valueobjects;

/**
 * WarehouseType Value Object.
 *
 * Represents the business type of a warehouse (SDD Inventory subdomain).
 * Controlled catalog: MARKETPLACE, SELLER.
 */
public final class WarehouseType extends DomainCatalog {

    public static final WarehouseType MARKETPLACE =
            new WarehouseType("MARKETPLACE", "Marketplace",
                    "Bodega operada por la plataforma para multiples vendedores.");
    public static final WarehouseType SELLER =
            new WarehouseType("SELLER", "Vendedor",
                    "Bodega propia de un vendedor del marketplace.");

    private static final WarehouseType[] VALUES = {MARKETPLACE, SELLER};

    private WarehouseType(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static WarehouseType[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static WarehouseType fromCode(String code) {
        for (WarehouseType type : VALUES) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown WarehouseType code: " + code);
    }
}
