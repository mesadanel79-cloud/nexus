package application.domain.valueobjects;

/**
 * InventoryMovementType Value Object.
 *
 * Represents the type of significant movement executed on an Inventory
 * record. Movements represent business events that occurred on stock and
 * are independent from the current inventory state:
 *
 * - Inventory.availableQuantity describes the current stock state.
 * - InventoryMovement.movementType = SALIDA_POR_VENTA describes the event
 *   that caused the decrease.
 *
 * Permitted values: INGRESO, RESERVA, SALIDA_POR_VENTA, AJUSTE, DEVOLUCION.
 */
public final class InventoryMovementType extends DomainCatalog {

    public static final InventoryMovementType INGRESO =
            new InventoryMovementType("INGRESO", "Ingreso",
                    "Entrada de nuevas existencias a la bodega.");
    public static final InventoryMovementType RESERVA =
            new InventoryMovementType("RESERVA", "Reserva",
                    "Apartado de existencias para un pedido en curso.");
    public static final InventoryMovementType SALIDA_POR_VENTA =
            new InventoryMovementType("SALIDA_POR_VENTA", "Salida por Venta",
                    "Disminucion de existencias por el despacho de un pedido.");
    public static final InventoryMovementType AJUSTE =
            new InventoryMovementType("AJUSTE", "Ajuste",
                    "Correccion manual de existencias.");
    public static final InventoryMovementType DEVOLUCION =
            new InventoryMovementType("DEVOLUCION", "Devolucion",
                    "Reingreso de existencias por una devolucion aprobada.");

    private static final InventoryMovementType[] VALUES =
            {INGRESO, RESERVA, SALIDA_POR_VENTA, AJUSTE, DEVOLUCION};

    private InventoryMovementType(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static InventoryMovementType[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static InventoryMovementType fromCode(String code) {
        for (InventoryMovementType type : VALUES) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown InventoryMovementType code: " + code);
    }
}