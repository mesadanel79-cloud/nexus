package application.domain.valueobjects;

/**
 * SellerStatus Value Object.
 *
 * Represents the operational status of a seller within the marketplace.
 * Describes the current condition of the seller's commercial relationship
 * and is independent from UserStatus.
 *
 * Lifecycle: ACTIVO -> SUSPENDIDO -> DADO_DE_BAJA; ACTIVO -> DADO_DE_BAJA.
 *
 * Permitted values: ACTIVO, SUSPENDIDO, DADO_DE_BAJA.
 */
public final class SellerStatus extends DomainCatalog {

    public static final SellerStatus ACTIVO =
            new SellerStatus("ACTIVO", "Activo",
                    "Vendedor habilitado para publicar y vender productos.");
    public static final SellerStatus SUSPENDIDO =
            new SellerStatus("SUSPENDIDO", "Suspendido",
                    "Vendedor temporalmente inhabilitado para operar.");
    public static final SellerStatus DADO_DE_BAJA =
            new SellerStatus("DADO_DE_BAJA", "Dado de Baja",
                    "Vendedor retirado permanentemente del marketplace.");

    private static final SellerStatus[] VALUES = {ACTIVO, SUSPENDIDO, DADO_DE_BAJA};

    private SellerStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static SellerStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static SellerStatus fromCode(String code) {
        for (SellerStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown SellerStatus code: " + code);
    }
}