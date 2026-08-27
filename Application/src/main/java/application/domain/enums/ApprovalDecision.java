package application.domain.enums;

/**
 * ApprovalDecision primitive enum.
 *
 * Represents the outcome of an evaluation process, used when deciding
 * upon a return request. Modeled as a simple enum because it contains
 * fixed technical values without business catalog metadata.
 */
public enum ApprovalDecision {
    APROBADO,
    RECHAZADO
}