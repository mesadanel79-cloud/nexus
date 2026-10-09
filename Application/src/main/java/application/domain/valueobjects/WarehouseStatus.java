package application.domain.valueobjects;

/**
 * WarehouseStatus Value Object.
 *
 * Represents the operational status of a warehouse, either a marketplace
 * warehouse or a seller warehouse.
 *
 * Permitted values: ACTIVA, INACTIVA, BLOQUEADA, CERRADA.
 *
 * Only an ACTIVA warehouse is operational: blocked, closed or inactive
 * warehouses cannot receive inventory movements.
 */
public final class WarehouseStatus extends DomainCatalog {

    public static final WarehouseStatus ACTIVA =
            new WarehouseStatus("ACTIVA", "Activa",
                    "La bodega se encuentra operativa y puede recibir movimientos de inventario.");
    public static final WarehouseStatus INACTIVA =
            new WarehouseStatus("INACTIVA", "Inactiva",
                    "La bodega se encuentra temporalmente fuera de operacion.");
    public static final WarehouseStatus BLOQUEADA =
            new WarehouseStatus("BLOQUEADA", "Bloqueada",
                    "La bodega fue bloqueada y no admite movimientos de inventario.");
    public static final WarehouseStatus CERRADA =
            new WarehouseStatus("CERRADA", "Cerrada",
                    "Cierre definitivo de la bodega; estado terminal que no se reversa.");

    private static final WarehouseStatus[] VALUES =
            {ACTIVA, INACTIVA, BLOQUEADA, CERRADA};

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