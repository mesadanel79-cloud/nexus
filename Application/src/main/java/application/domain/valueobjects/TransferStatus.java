package application.domain.valueobjects;

/**
 * TransferStatus Value Object (Bank subdomain).
 *
 * Represents the execution status of a Transfer. Transfers are atomic:
 * they are either executed in full or rejected in full.
 *
 * Permitted values: EJECUTADA, RECHAZADA.
 */
public final class TransferStatus extends DomainCatalog {

    public static final TransferStatus EJECUTADA =
            new TransferStatus("EJECUTADA", "Ejecutada",
                    "La transferencia se ejecuto de forma atomica sobre ambas cuentas.");
    public static final TransferStatus RECHAZADA =
            new TransferStatus("RECHAZADA", "Rechazada",
                    "La transferencia no se ejecuto porque fallo una validacion de negocio.");

    private static final TransferStatus[] VALUES = {EJECUTADA, RECHAZADA};

    private TransferStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static TransferStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static TransferStatus fromCode(String code) {
        for (TransferStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TransferStatus code: " + code);
    }
}