package application.domain.valueobjects;

/**
 * BankAccountStatus Value Object (Bank subdomain).
 *
 * Represents the operational status of a BankAccount.
 *
 * Lifecycle: ACTIVA <-> BLOQUEADA; ACTIVA/BLOQUEADA -> CERRADA.
 *
 * Permitted values: ACTIVA, BLOQUEADA, CERRADA.
 */
public final class BankAccountStatus extends DomainCatalog {

    public static final BankAccountStatus ACTIVA =
            new BankAccountStatus("ACTIVA", "Activa",
                    "La cuenta puede recibir depositos, retiros y transferencias.");
    public static final BankAccountStatus BLOQUEADA =
            new BankAccountStatus("BLOQUEADA", "Bloqueada",
                    "La cuenta suspende temporalmente sus operaciones.");
    public static final BankAccountStatus CERRADA =
            new BankAccountStatus("CERRADA", "Cerrada",
                    "La cuenta fue cerrada permanentemente.");

    private static final BankAccountStatus[] VALUES = {ACTIVA, BLOQUEADA, CERRADA};

    private BankAccountStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static BankAccountStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static BankAccountStatus fromCode(String code) {
        for (BankAccountStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown BankAccountStatus code: " + code);
    }
}