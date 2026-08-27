package application.domain.valueobjects;

/**
 * OrderStatus Value Object.
 *
 * Represents the execution status of an order. The status describes the
 * current situation of the order, while associated operations (inventory
 * movements, invoicing, shipment) provide the historical record of actions
 * executed along its lifecycle.
 *
 * Lifecycle: CARRITO -> PENDIENTE_DE_PAGO -> PAGADO -> DESPACHADO
 *            -> ENTREGADO_FINALIZADO.
 *
 * Permitted values: CARRITO, PENDIENTE_DE_PAGO, PAGADO, DESPACHADO,
 * ENTREGADO_FINALIZADO.
 */
public final class OrderStatus extends DomainCatalog {

    public static final OrderStatus CARRITO =
            new OrderStatus("CARRITO", "Carrito",
                    "Seleccion provisional de productos, aun no confirmada como pedido.");
    public static final OrderStatus PENDIENTE_DE_PAGO =
            new OrderStatus("PENDIENTE_DE_PAGO", "Pendiente de Pago",
                    "Pedido creado, en espera de confirmacion financiera.");
    public static final OrderStatus PAGADO =
            new OrderStatus("PAGADO", "Pagado",
                    "Pago confirmado; inicia el proceso de alistamiento.");
    public static final OrderStatus DESPACHADO =
            new OrderStatus("DESPACHADO", "Despachado",
                    "Pedido despachado fisicamente desde la bodega.");
    public static final OrderStatus ENTREGADO_FINALIZADO =
            new OrderStatus("ENTREGADO_FINALIZADO", "Entregado / Finalizado",
                    "Entrega concluida satisfactoriamente.");

    private static final OrderStatus[] VALUES =
            {CARRITO, PENDIENTE_DE_PAGO, PAGADO, DESPACHADO, ENTREGADO_FINALIZADO};

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static OrderStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown OrderStatus code: " + code);
    }
}