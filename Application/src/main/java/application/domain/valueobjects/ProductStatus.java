package application.domain.valueobjects;

/**
 * ProductStatus Value Object.
 *
 * Represents the lifecycle status of a product within the catalog.
 * Describes the current visibility condition of the product and is
 * independent from the seller that publishes it.
 *
 * Lifecycle: PUBLICADO <-> SUSPENDIDO; PUBLICADO/SUSPENDIDO -> DESCONTINUADO.
 *
 * Permitted values: PUBLICADO, SUSPENDIDO, DESCONTINUADO.
 */
public final class ProductStatus extends DomainCatalog {

    public static final ProductStatus PUBLICADO =
            new ProductStatus("PUBLICADO", "Publicado",
                    "El producto es visible en el catalogo publico.");
    public static final ProductStatus SUSPENDIDO =
            new ProductStatus("SUSPENDIDO", "Suspendido",
                    "El producto se encuentra oculto temporalmente del catalogo.");
    public static final ProductStatus DESCONTINUADO =
            new ProductStatus("DESCONTINUADO", "Descontinuado",
                    "El producto ha sido retirado permanentemente del catalogo.");

    private static final ProductStatus[] VALUES = {PUBLICADO, SUSPENDIDO, DESCONTINUADO};

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }

    /** All controlled values of this catalog. */
    public static ProductStatus[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static ProductStatus fromCode(String code) {
        for (ProductStatus status : VALUES) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ProductStatus code: " + code);
    }
}