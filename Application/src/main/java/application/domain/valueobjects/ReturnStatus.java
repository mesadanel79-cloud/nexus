package application.domain.valueobjects;

/**
 * ReturnStatus Value Object.
 *
 * Represents the status of a return request.
 *
 * Lifecycle: SOLICITADA -> EN_REVISION -> APROBADA | RECHAZADA.
 *
 * Permitted values: SOLICITADA, EN_REVISION, APROBADA, RECHAZADA.
 */
public final class ReturnStatus extends DomainCatalog {

    public static final ReturnStatus SOLICITADA =
            new ReturnStatus("SOLICITADA", "Solicitada",
                    "Devolucion registrada por el comprador.");
    public static final ReturnStatus EN_REVISION =
            new ReturnStatus("EN_REVISION", "En Revision",
                    "Devolucion en evaluacion por el personal interno.");
    public static final ReturnStatus APROBADA =
            new ReturnStatus("APROBADA", "Aprobada",
                    "Devolucion aceptada; habilita la generacion de un Refund.");
    public static final ReturnStatus RECHAZADA =
            new ReturnStatus("RECHAZADA", "Rechazada",
                    "Devolucion no aceptada.");

    private static final ReturnStatus[] VALUES =
            {SOLICITADA, EN_REVISION, APROBADA, RECHAZADA};

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static ReturnStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static ReturnStatus fromCode(String code) {
        for (ReturnStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ReturnStatus code: " + code);
    }
}