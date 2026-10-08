package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.adapters.out.persistence.memory.AuditRepositoryAdapter;
import application.adapters.out.persistence.memory.OperationRepositoryAdapter;
import application.domain.exceptions.AuditLogNotFoundException;
import application.domain.exceptions.InvalidOperationException;
import application.domain.exceptions.OperationNotFoundException;
import application.domain.models.Administrator;
import application.domain.models.AuditLog;
import application.domain.models.MarketplaceAsset;
import application.domain.models.MarketplaceWarehouse;
import application.domain.models.Operation;
import application.domain.models.SystemUser;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserStatus;

/**
 * Unit tests for the Operation and Audit domain service, using the
 * in-memory output adapters. The service is fully testable without MySQL,
 * MongoDB or any infrastructure component.
 */
class OperationAuditServiceTest {

    private OperationAuditService service;
    private Administrator administrator;
    private SystemUser adminUser;
    private MarketplaceAsset warehouse;

    @BeforeEach
    void setUp() {
        service = new OperationAuditService(
                new OperationRepositoryAdapter(),
                new AuditRepositoryAdapter());
        administrator = new Administrator("ADM-1", "Admin Uno",
                "admin1@nexus.local", UserStatus.ACTIVO);
        adminUser = new SystemUser("admin1", "hash", administrator);
        warehouse = new MarketplaceWarehouse("WH-1", "Bodega Central",
                "Bogota", administrator);
    }

    @Test
    void registerOperationWithAuditTracesTheAssetAndTheUser() {
        Operation operation = new Operation(OperationType.BLOQUEO_BODEGA,
                adminUser, warehouse);

        Operation registered = service.registerOperationWithAudit(operation);

        assertNotNull(registered.getOperationId());
        assertEquals(OperationType.BLOQUEO_BODEGA,
                registered.getOperationType());

        List<Operation> operations = service.consultOperationsByAsset(warehouse);
        assertEquals(1, operations.size());
        assertEquals(registered.getOperationId(),
                operations.get(0).getOperationId());

        List<AuditLog> auditLogs = service.consultAuditLogsByAsset(warehouse);
        assertEquals(1, auditLogs.size());

        AuditLog auditLog = auditLogs.get(0);
        assertEquals(OperationType.BLOQUEO_BODEGA, auditLog.getOperationType());
        assertEquals(adminUser.getUsername(),
                auditLog.getPerformedBy().getUsername());
        assertEquals(administrator.getRole(), auditLog.getUserRole());
        assertEquals("WAREHOUSE", auditLog.getAffectedAsset().getAssetType());
        assertEquals("WH-1",
                auditLog.getAffectedAsset().getAssetIdentifier());
        assertEquals(registered.getOperationId(),
                auditLog.getDetails().get("operationId"));
    }

    @Test
    void auditRecordIsImmutableAfterRegistration() {
        service.registerOperationWithAudit(new Operation(
                OperationType.CIERRE_BODEGA, adminUser, warehouse));
        AuditLog auditLog = service.consultAuditLogs().get(0);

        assertThrows(UnsupportedOperationException.class,
                () -> auditLog.getDetails().put("extra", "value"));
    }

    @Test
    void consultOperationsByPerformerFiltersByUser() {
        service.registerOperation(new Operation(OperationType.APERTURA_BODEGA,
                adminUser, warehouse));

        Administrator otherAdmin = new Administrator("ADM-9", "Admin Otro",
                "admin9@nexus.local", UserStatus.ACTIVO);
        SystemUser otherUser = new SystemUser("admin9", "hash", otherAdmin);

        assertEquals(1, service.consultOperationsByPerformer(adminUser).size());
        assertEquals(0, service.consultOperationsByPerformer(otherUser).size());
    }

    @Test
    void missingOrInvalidRecordsAreReported() {
        assertThrows(OperationNotFoundException.class,
                () -> service.consultOperation("OP-404"));
        assertThrows(AuditLogNotFoundException.class,
                () -> service.consultAuditLog("AUD-404"));
        assertThrows(InvalidOperationException.class,
                () -> service.registerOperation(null));
        assertThrows(InvalidOperationException.class,
                () -> service.consultOperationsByAsset(null));
    }
}
