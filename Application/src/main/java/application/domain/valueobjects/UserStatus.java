package application.domain.valueobjects;

/**
 * UserStatus Value Object.
 *
 * Represents the current access status of a person within NexusMarket.
 * Independent from CommercialStatus and SellerStatus: a person may be
 * blocked in the system while its commercial relationship remains active,
 * or vice versa.
 *
 * Permitted values: ACTIVO, INACTIVO, BLOQUEADO.
 */
public final class UserStatus extends DomainCatalog {

    public static final UserStatus ACTIVO =
            new UserStatus("ACTIVO", "Activo",
                    "El usuario puede acceder al sistema con normalidad.");
    public static final UserStatus INACTIVO =
            new UserStatus("INACTIVO", "Inactivo",
                    "El usuario existe pero no puede realizar operaciones en el sistema.");
    public static final UserStatus BLOQUEADO =
            new UserStatus("BLOQUEADO", "Bloqueado",
                    "El acceso del usuario ha sido suspendido.");

    private static final UserStatus[] VALUES = {ACTIVO, INACTIVO, BLOQUEADO};

    private UserStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static UserStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static UserStatus fromCode(String code) {
        for (UserStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown UserStatus code: " + code);
    }
}