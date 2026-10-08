package application.domain.valueobjects;

/**
 * OperationType Value Object.
 *
 * Represents the type of a significant business operation executed over a
 * marketplace asset (Warehouse, Order, Shipment, Return).
 *
 * Every significant business action generates an Operation, and relevant
 * operations also generate an AuditLog. The catalog below is the controlled
 * list of operation types admitted by the domain (Operation and Audit
 * subdomain).
 *
 * The names follow the business operations described by the SDD
 * (WAREHOUSE_OPENING, STOCK_INCREASE, STOCK_DECREASE, ... RETURN_CLOSURE);
 * the codes are kept in the same language used by the rest of the domain
 * catalogs.
 *
 * Permitted values: the constants declared below.
 */
public final class OperationType extends DomainCatalog {

    public static final OperationType APERTURA_BODEGA =
            new OperationType("APERTURA_BODEGA", "Apertura de Bodega",
                    "Registro inicial de una bodega del marketplace o de un vendedor.");
    public static final OperationType INGRESO_STOCK =
            new OperationType("INGRESO_STOCK", "Ingreso de Stock",
                    "Entrada de unidades de un producto a una bodega.");
    public static final OperationType RETIRO_STOCK =
            new OperationType("RETIRO_STOCK", "Retiro de Stock",
                    "Salida de unidades de un producto de una bodega.");
    public static final OperationType BLOQUEO_BODEGA =
            new OperationType("BLOQUEO_BODEGA", "Bloqueo de Bodega",
                    "Cambio del estado operativo de una bodega a bloqueada.");
    public static final OperationType DESBLOQUEO_BODEGA =
            new OperationType("DESBLOQUEO_BODEGA", "Desbloqueo de Bodega",
                    "Restablecimiento del estado operativo de una bodega.");
    public static final OperationType CIERRE_BODEGA =
            new OperationType("CIERRE_BODEGA", "Cierre de Bodega",
                    "Cierre definitivo de una bodega segun las reglas de negocio.");
    public static final OperationType CREACION_PEDIDO =
            new OperationType("CREACION_PEDIDO", "Creacion de Pedido",
                    "Registro de un nuevo pedido de un comprador.");
    public static final OperationType CONFIRMACION_PEDIDO =
            new OperationType("CONFIRMACION_PEDIDO", "Confirmacion de Pedido",
                    "Confirmacion de un pedido tras las validaciones aplicables.");
    public static final OperationType CANCELACION_PEDIDO =
            new OperationType("CANCELACION_PEDIDO", "Cancelacion de Pedido",
                    "Cancelacion de un pedido y registro de la decision.");
    public static final OperationType CUMPLIMIENTO_PEDIDO =
            new OperationType("CUMPLIMIENTO_PEDIDO", "Cumplimiento de Pedido",
                    "Alistamiento de las unidades del pedido desde la bodega.");
    public static final OperationType PAGO_PEDIDO =
            new OperationType("PAGO_PEDIDO", "Pago de Pedido",
                    "Registro de un pago aplicado a un pedido existente.");
    public static final OperationType CIERRE_PEDIDO =
            new OperationType("CIERRE_PEDIDO", "Cierre de Pedido",
                    "Conclusion del ciclo de vida de un pedido.");
    public static final OperationType CREACION_ENVIO =
            new OperationType("CREACION_ENVIO", "Creacion de Envio",
                    "Registro de una solicitud de envio para un pedido.");
    public static final OperationType APROBACION_ENVIO =
            new OperationType("APROBACION_ENVIO", "Aprobacion de Envio",
                    "Aprobacion de un envio que requiere autorizacion.");
    public static final OperationType RECHAZO_ENVIO =
            new OperationType("RECHAZO_ENVIO", "Rechazo de Envio",
                    "Rechazo de un envio que esperaba aprobacion.");
    public static final OperationType DESPACHO_ENVIO =
            new OperationType("DESPACHO_ENVIO", "Despacho de Envio",
                    "Salida fisica del envio desde la bodega de origen.");
    public static final OperationType EXPIRACION_ENVIO =
            new OperationType("EXPIRACION_ENVIO", "Expiracion de Envio",
                    "Expiracion de un envio que esperaba aprobacion.");
    public static final OperationType SOLICITUD_DEVOLUCION =
            new OperationType("SOLICITUD_DEVOLUCION", "Solicitud de Devolucion",
                    "Registro de una solicitud de devolucion por parte del comprador.");
    public static final OperationType APROBACION_DEVOLUCION =
            new OperationType("APROBACION_DEVOLUCION", "Aprobacion de Devolucion",
                    "Aprobacion de una devolucion con las validaciones aplicables.");
    public static final OperationType RECHAZO_DEVOLUCION =
            new OperationType("RECHAZO_DEVOLUCION", "Rechazo de Devolucion",
                    "Rechazo de una devolucion y registro de la decision.");
    public static final OperationType REGISTRO_REEMBOLSO =
            new OperationType("REGISTRO_REEMBOLSO", "Registro de Reembolso",
                    "Registro de un reembolso asociado a una devolucion aprobada.");
    public static final OperationType CIERRE_DEVOLUCION =
            new OperationType("CIERRE_DEVOLUCION", "Cierre de Devolucion",
                    "Conclusion del ciclo de vida de una devolucion.");

    private static final OperationType[] VALUES = {
            APERTURA_BODEGA, INGRESO_STOCK, RETIRO_STOCK, BLOQUEO_BODEGA,
            DESBLOQUEO_BODEGA, CIERRE_BODEGA, CREACION_PEDIDO,
            CONFIRMACION_PEDIDO, CANCELACION_PEDIDO, CUMPLIMIENTO_PEDIDO,
            PAGO_PEDIDO, CIERRE_PEDIDO, CREACION_ENVIO, APROBACION_ENVIO,
            RECHAZO_ENVIO, DESPACHO_ENVIO, EXPIRACION_ENVIO,
            SOLICITUD_DEVOLUCION, APROBACION_DEVOLUCION, RECHAZO_DEVOLUCION,
            REGISTRO_REEMBOLSO, CIERRE_DEVOLUCION};

    private OperationType(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static OperationType[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static OperationType fromCode(String code) {
        for (OperationType type : VALUES) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown OperationType code: " + code);
    }

    /** True for operations over a warehouse (opening, blocking, closing). */
    public boolean isWarehouseOperation() {
        return equals(APERTURA_BODEGA) || equals(BLOQUEO_BODEGA)
                || equals(DESBLOQUEO_BODEGA) || equals(CIERRE_BODEGA);
    }

    /** True for operations that change the stored stock of a warehouse. */
    public boolean isStockOperation() {
        return equals(INGRESO_STOCK) || equals(RETIRO_STOCK);
    }

    /** True for operations over an order. */
    public boolean isOrderOperation() {
        return equals(CREACION_PEDIDO) || equals(CONFIRMACION_PEDIDO)
                || equals(CANCELACION_PEDIDO) || equals(CUMPLIMIENTO_PEDIDO)
                || equals(PAGO_PEDIDO) || equals(CIERRE_PEDIDO);
    }

    /** True for operations over a shipment. */
    public boolean isShipmentOperation() {
        return equals(CREACION_ENVIO) || equals(APROBACION_ENVIO)
                || equals(RECHAZO_ENVIO) || equals(DESPACHO_ENVIO)
                || equals(EXPIRACION_ENVIO);
    }

    /** True for operations over a return or its refund. */
    public boolean isReturnOperation() {
        return equals(SOLICITUD_DEVOLUCION) || equals(APROBACION_DEVOLUCION)
                || equals(RECHAZO_DEVOLUCION) || equals(REGISTRO_REEMBOLSO)
                || equals(CIERRE_DEVOLUCION);
    }

    /** True when the operation requires explicit approval authority. */
    public boolean isApprovalOperation() {
        return equals(APROBACION_DEVOLUCION) || equals(RECHAZO_DEVOLUCION)
                || equals(REGISTRO_REEMBOLSO) || equals(APROBACION_ENVIO)
                || equals(RECHAZO_ENVIO);
    }
}
