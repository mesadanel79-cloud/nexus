package application.domain.valueobjects;

/**
 * CommercialStatus Value Object.
 *
 * Represents the current state of a buyer's commercial relationship with
 * the marketplace. Independent from UserStatus: it describes the buyer's
 * ability to make purchases, not its system access.
 *
 * Permitted values: HABILITADO, SUSPENDIDO.
 */
public final class CommercialStatus extends DomainCatalog {

    public static final CommercialStatus HABILITADO =
            new CommercialStatus("HABILITADO", "Habilitado",
                    "El comprador puede realizar compras con normalidad.");
    public static final CommercialStatus SUSPENDIDO =
            new CommercialStatus("SUSPENDIDO", "Suspendido",
                    "El comprador no puede realizar compras temporalmente.");

    private static final CommercialStatus[] VALUES = {HABILITADO, SUSPENDIDO};

    private CommercialStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static CommercialStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static CommercialStatus fromCode(String code) {
        for (CommercialStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown CommercialStatus code: " + code);
    }
}