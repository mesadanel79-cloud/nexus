package application.domain.valueobjects;

/**
 * WarehouseStatus Value Object.
 *
 * Represents the operational status of a warehouse, either a marketplace
 * warehouse or a seller warehouse.
 *
 * Permitted values: ACTIVA, INACTIVA.
 */
public final class WarehouseStatus extends DomainCatalog {

    public static final WarehouseStatus ACTIVA =
            new WarehouseStatus("ACTIVA", "Activa",
                    "La bodega se encuentra operativa y puede recibir movimientos de inventario.");
    public static final WarehouseStatus INACTIVA =
            new WarehouseStatus("INACTIVA", "Inactiva",
                    "La bodega se encuentra temporalmente fuera de operacion.");

    private static final WarehouseStatus[] VALUES = {ACTIVA, INACTIVA};

    private WarehouseStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static WarehouseStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static WarehouseStatus fromCode(String code) {
        for (WarehouseStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown WarehouseStatus code: " + code);
    }
}