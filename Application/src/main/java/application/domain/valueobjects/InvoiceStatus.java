package application.domain.valueobjects;

/**
 * InvoiceStatus Value Object.
 *
 * Represents the commercial status of an Invoice.
 *
 * Lifecycle: EMITIDA -> ANULADA.
 *
 * Permitted values: EMITIDA, ANULADA.
 */
public final class InvoiceStatus extends DomainCatalog {

    public static final InvoiceStatus EMITIDA =
            new InvoiceStatus("EMITIDA", "Emitida",
                    "Factura vigente generada para una orden confirmada.");
    public static final InvoiceStatus ANULADA =
            new InvoiceStatus("ANULADA", "Anulada",
                    "Factura cancelada; no representa una obligacion vigente.");

    private static final InvoiceStatus[] VALUES = {EMITIDA, ANULADA};

    private InvoiceStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static InvoiceStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static InvoiceStatus fromCode(String code) {
        for (InvoiceStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown InvoiceStatus code: " + code);
    }
}