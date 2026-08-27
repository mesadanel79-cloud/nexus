package application.domain.valueobjects;

/**
 * RefundStatus Value Object.
 *
 * Represents the status of a refund originated by an approved return.
 *
 * Lifecycle: PENDIENTE -> PROCESADO | RECHAZADO.
 *
 * Permitted values: PENDIENTE, PROCESADO, RECHAZADO.
 */
public final class RefundStatus extends DomainCatalog {

    public static final RefundStatus PENDIENTE =
            new RefundStatus("PENDIENTE", "Pendiente",
                    "Reembolso registrado, aun no procesado.");
    public static final RefundStatus PROCESADO =
            new RefundStatus("PROCESADO", "Procesado",
                    "Reembolso efectuado al comprador.");
    public static final RefundStatus RECHAZADO =
            new RefundStatus("RECHAZADO", "Rechazado",
                    "Reembolso denegado.");

    private static final RefundStatus[] VALUES = {PENDIENTE, PROCESADO, RECHAZADO};

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static RefundStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static RefundStatus fromCode(String code) {
        for (RefundStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown RefundStatus code: " + code);
    }
}