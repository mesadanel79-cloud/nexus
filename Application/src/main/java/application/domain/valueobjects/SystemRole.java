package application.domain.valueobjects;

/**
 * SystemRole Value Object.
 *
 * Represents the responsibilities and permissions assigned to a person
 * within NexusMarket. The role belongs to Person because it represents what
 * that person means within the system (RG-02: a single role per user).
 *
 * Permitted values (controlled by the domain):
 * COMPRADOR, VENDEDOR, OPERADOR_LOGISTICO, ADMINISTRADOR, SUPERVISOR.
 */
public final class SystemRole extends DomainCatalog {

    public static final SystemRole COMPRADOR =
            new SystemRole("COMPRADOR", "Comprador",
                    "Persona que adquiere productos publicados en el catalogo.");
    public static final SystemRole VENDEDOR =
            new SystemRole("VENDEDOR", "Vendedor",
                    "Responsable de registrar y administrar sus propios productos.");
    public static final SystemRole OPERADOR_LOGISTICO =
            new SystemRole("OPERADOR_LOGISTICO", "Operador Logistico",
                    "Encargado de la operacion fisica de bodegas y despachos.");
    public static final SystemRole ADMINISTRADOR =
            new SystemRole("ADMINISTRADOR", "Administrador",
                    "Responsable de la administracion de vendedores y bodegas del marketplace.");
    public static final SystemRole SUPERVISOR =
            new SystemRole("SUPERVISOR", "Supervisor",
                    "Perfil de consulta y seguimiento operativo, sin permisos de administracion.");

    private static final SystemRole[] VALUES =
            {COMPRADOR, VENDEDOR, OPERADOR_LOGISTICO, ADMINISTRADOR, SUPERVISOR};

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static SystemRole[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static SystemRole fromCode(String code) {
        for (SystemRole role : VALUES) {
            if (role.getCode().equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown SystemRole code: " + code);
    }
}