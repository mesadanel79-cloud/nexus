package application.domain.services;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import application.domain.exceptions.AuditLogNotFoundException;
import application.domain.exceptions.InvalidAuditLogException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.OperationNotFoundException;
import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Operation;
import application.domain.models.SystemUser;
import application.domain.ports.in.ConsultAuditLogUseCase;
import application.domain.ports.in.ConsultOperationUseCase;
import application.domain.ports.in.RegisterAuditEventUseCase;
import application.domain.ports.in.RegisterOperationUseCase;
import application.domain.ports.out.AuditRepository;
import application.domain.ports.out.OperationRepository;

/**
 * Domain Service (Operation and Audit subdomain): provides traceability for
 * significant business actions performed over marketplace assets.
 *
 * Business rules enforced:
 * - Every significant marketplace action generates an Operation.
 * - Relevant business operations also generate an immutable AuditLog.
 * - The Operation and Audit service does not decide whether the originating
 *   business action is valid; the originating service keeps that
 *   responsibility. This service only registers and retrieves the
 *   traceability records.
 *
 * The service communicates exclusively through output ports: it never
 * accesses MySQL, MongoDB, SQL or JPA directly.
 */
public class OperationAuditService implements RegisterOperationUseCase,
        ConsultOperationUseCase, RegisterAuditEventUseCase,
        ConsultAuditLogUseCase {

    private final OperationRepository operationRepository;
    private final AuditRepository auditRepository;

    public OperationAuditService(OperationRepository operationRepository,
                                 AuditRepository auditRepository) {
        this.operationRepository = operationRepository;
        this.auditRepository = auditRepository;
    }

    @Override
    public Operation registerOperation(Operation operation) {
        if (operation == null) {
            throw new InvalidOperationException(
                    "The operation to register must not be null");
        }
        return operationRepository.save(operation);
    }

    @Override
    public Operation registerOperationWithAudit(Operation operation) {
        Operation registered = registerOperation(operation);
        registerAuditEvent(
                AuditLog.deriveFrom(registered, buildDetails(registered)));
        return registered;
    }

    @Override
    public AuditLog registerAuditEvent(AuditLog auditLog) {
        if (auditLog == null) {
            throw new InvalidAuditLogException(
                    "The audit record to register must not be null");
        }
        return auditRepository.save(auditLog);
    }

    @Override
    public Operation consultOperation(String operationId) {
        return operationRepository.findById(operationId)
                .orElseThrow(() -> new OperationNotFoundException(
                        "Operation not found: " + operationId));
    }

    @Override
    public List<Operation> consultOperations() {
        return operationRepository.findAll();
    }

    @Override
    public List<Operation> consultOperationsByAsset(MarketplaceAsset asset) {
        requireAsset(asset);
        return operationRepository.findByAffectedAsset(asset);
    }

    @Override
    public List<Operation> consultOperationsByPerformer(SystemUser user) {
        if (user == null) {
            throw new InvalidOperationException(
                    "The performing user must not be null");
        }
        return operationRepository.findByPerformer(user);
    }

    @Override
    public AuditLog consultAuditLog(String auditId) {
        return auditRepository.findById(auditId)
                .orElseThrow(() -> new AuditLogNotFoundException(
                        "Audit record not found: " + auditId));
    }

    @Override
    public List<AuditLog> consultAuditLogs() {
        return auditRepository.findAll();
    }

    @Override
    public List<AuditLog> consultAuditLogsByAsset(MarketplaceAsset asset) {
        requireAsset(asset);
        return auditRepository.findByAffectedAsset(asset);
    }

    /**
     * Builds the operation-specific information preserved by the audit
     * record, as required by AuditLog.details.
     */
    private Map<String, Object> buildDetails(Operation operation) {
        Map<String, Object> details = new LinkedHashMap<>();
        details.put("operationId", operation.getOperationId());
        details.put("operationType", operation.getOperationType().getCode());
        details.put("executionDate", operation.getExecutionDate().toString());
        details.put("performedBy", operation.getPerformedBy().getUsername());
        details.put("assetType", operation.getAffectedAsset().getAssetType());
        details.put("assetIdentifier",
                operation.getAffectedAsset().getAssetIdentifier());
        return details;
    }

    private void requireAsset(MarketplaceAsset asset) {
        if (asset == null) {
            throw new InvalidOperationException(
                    "The affected marketplace asset must not be null");
        }
    }
}
