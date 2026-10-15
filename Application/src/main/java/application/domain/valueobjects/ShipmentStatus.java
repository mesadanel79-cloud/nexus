package application.domain.valueobjects;

/**
 * ShipmentStatus Value Object.
 *
 * Represents the execution status of the logistics process of a shipment,
 * managed by a LogisticsOperator.
 *
 * Lifecycle: EN_PREPARACION -> DESPACHADO -> EN_TRANSITO -> ENTREGADO.
 *
 * Permitted values: EN_PREPARACION, DESPACHADO, EN_TRANSITO, ENTREGADO.
 */
public final class ShipmentStatus extends DomainCatalog {

    public static final ShipmentStatus EN_PREPARACION =
            new ShipmentStatus("EN_PREPARACION", "En Preparacion",
                    "Empaque del pedido en curso dentro de la bodega.");
    public static final ShipmentStatus DESPACHADO =
            new ShipmentStatus("DESPACHADO", "Despachado",
                    "Pedido despachado y en transito hacia el comprador.");
    public static final ShipmentStatus EN_TRANSITO =
            new ShipmentStatus("EN_TRANSITO", "En Transito",
                    "Pedido en proceso de transporte hacia el destino final.");
    public static final ShipmentStatus ENTREGADO =
            new ShipmentStatus("ENTREGADO", "Entregado",
                    "Pedido entregado al comprador.");
    public static final ShipmentStatus EN_ESPERA_APROBACION =
            new ShipmentStatus("EN_ESPERA_APROBACION", "En Espera de Aprobacion",
                    "Envio pendiente de aprobacion antes del despacho.");
    public static final ShipmentStatus APROBADO =
            new ShipmentStatus("APROBADO", "Aprobado",
                    "Envio aprobado y listo para despacho.");
    public static final ShipmentStatus RECHAZADO =
            new ShipmentStatus("RECHAZADO", "Rechazado",
                    "Envio rechazado durante la aprobacion.");
    public static final ShipmentStatus EXPIRADO =
            new ShipmentStatus("EXPIRADO", "Expirado",
                    "Envio expirado por falta de aprobacion en la ventana permitida.");

    private static final ShipmentStatus[] VALUES =
            {EN_PREPARACION, DESPACHADO, EN_TRANSITO, ENTREGADO,
                    EN_ESPERA_APROBACION, APROBADO, RECHAZADO, EXPIRADO};

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static ShipmentStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static ShipmentStatus fromCode(String code) {
        for (ShipmentStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ShipmentStatus code: " + code);
    }
}