package application.domain.valueobjects;

/**
 * DeliveryPriority Value Object.
 *
 * Represents the delivery priority of an order (SDD Order subdomain).
 * Controlled catalog: STANDARD, EXPRESS.
 */
public final class DeliveryPriority extends DomainCatalog {

    public static final DeliveryPriority STANDARD =
            new DeliveryPriority("STANDARD", "Estandar",
                    "Entrega con tiempos estimados normales, sin costo adicional.");
    public static final DeliveryPriority EXPRESS =
            new DeliveryPriority("EXPRESS", "Expresa",
                    "Entrega prioritaria con menor tiempo estimado y posibles requisitos de bodega.");

    private static final DeliveryPriority[] VALUES = {STANDARD, EXPRESS};

    private DeliveryPriority(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static DeliveryPriority[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static DeliveryPriority fromCode(String code) {
        for (DeliveryPriority priority : VALUES) {
            if (priority.getCode().equals(code)) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Unknown DeliveryPriority code: " + code);
    }
}
