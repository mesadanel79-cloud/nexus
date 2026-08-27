package application.domain.enums;

/**
 * ReturnReason primitive enum.
 *
 * Represents the reason declared by the buyer when requesting a return.
 * Modeled as a simple enum because it contains fixed technical values
 * without business catalog metadata.
 */
public enum ReturnReason {
    PRODUCTO_DANADO,
    PRODUCTO_INCORRECTO,
    CAMBIO_DE_OPINION,
    OTRO
}