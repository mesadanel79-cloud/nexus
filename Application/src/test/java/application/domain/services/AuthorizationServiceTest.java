package application.domain.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import application.domain.enums.ReturnReason;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.LogisticsOperator;
import application.domain.models.MarketplaceAsset;
import application.domain.models.MarketplaceWarehouse;
import application.domain.models.Order;
import application.domain.models.Return;
import application.domain.models.Seller;
import application.domain.models.Shipment;
import application.domain.models.Supervisor;
import application.domain.models.SystemUser;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.UserStatus;

/**
 * Unit tests for the Authorization domain service. The service is fully
 * testable without infrastructure because every decision is evaluated from
 * the Domain Models received.
 */
class AuthorizationServiceTest {

    private AuthorizationService service;

    private Administrator administrator;
    private SystemUser adminUser;
    private SystemUser buyerUser;
    private SystemUser sellerUser;
    private SystemUser logisticsUser;
    private SystemUser supervisorUser;
    private SystemUser inactiveAdminUser;

    private Buyer buyer;
    private SystemUser otherBuyerUser;
    private Order order;

    @BeforeEach
    void setUp() {
        service = new AuthorizationService();

        administrator = new Administrator("ADM-1", "Admin Uno",
                "admin1@nexus.local", UserStatus.ACTIVO);
        adminUser = new SystemUser("admin1", "hash", administrator);

        buyer = new Buyer("BUY-1", "Comprador Uno", "buyer1@nexus.local",
                UserStatus.ACTIVO, "Calle 1");
        buyerUser = new SystemUser("buyer1", "hash", buyer);

        Buyer otherBuyer = new Buyer("BUY-2", "Comprador Dos",
                "buyer2@nexus.local", UserStatus.ACTIVO, "Calle 2");
        otherBuyerUser = new SystemUser("buyer2", "hash", otherBuyer);

        Seller seller = administrator.onboardSeller("SEL-1", "Vendedor Uno",
                "seller1@nexus.local");
        sellerUser = new SystemUser("seller1", "hash", seller);

        LogisticsOperator operator = new LogisticsOperator("OP-1",
                "Operador Uno", "op1@nexus.local", UserStatus.ACTIVO);
        logisticsUser = new SystemUser("op1", "hash", operator);

        Supervisor supervisor = new Supervisor("SUP-1", "Supervisor Uno",
                "sup1@nexus.local", UserStatus.ACTIVO);
        supervisorUser = new SystemUser("sup1", "hash", supervisor);

        Administrator blocked = new Administrator("ADM-2", "Admin Dos",
                "admin2@nexus.local", UserStatus.BLOQUEADO);
        inactiveAdminUser = new SystemUser("admin2", "hash", blocked);

        order = new Order(1001, buyer, "Calle 1");
    }

    @Test
    void administratorIsAuthorizedForOrderOperations() {
        assertTrue(service.validatePermissions(adminUser,
                OperationType.CREACION_PEDIDO));
        assertTrue(service.validatePermissions(adminUser,
                OperationType.CANCELACION_PEDIDO));
    }

    @Test
    void buyerIsNotAuthorizedToApproveReturns() {
        assertFalse(service.validatePermissions(buyerUser,
                OperationType.APROBACION_DEVOLUCION));
        assertThrows(UnauthorizedOperationException.class,
                () -> service.authorize(buyerUser,
                        OperationType.APROBACION_DEVOLUCION));
    }

    @Test
    void nonOperationalUserIsNeverAuthorized() {
        assertFalse(service.validatePermissions(inactiveAdminUser,
                OperationType.CREACION_PEDIDO));
        assertFalse(service.validatePermissions(null,
                OperationType.CREACION_PEDIDO));
        assertFalse(service.validatePermissions(adminUser, null));
    }

    @Test
    void warehouseOperationsRequireAdministratorOrLogisticsOperator() {
        assertTrue(service.validatePermissions(adminUser,
                OperationType.BLOQUEO_BODEGA));
        assertTrue(service.validatePermissions(logisticsUser,
                OperationType.BLOQUEO_BODEGA));
        assertFalse(service.validatePermissions(buyerUser,
                OperationType.BLOQUEO_BODEGA));
        assertFalse(service.validatePermissions(sellerUser,
                OperationType.BLOQUEO_BODEGA));
    }

    @Test
    void stockOperationsAllowSellerAndLogisticsOperator() {
        assertTrue(service.validatePermissions(sellerUser,
                OperationType.INGRESO_STOCK));
        assertTrue(service.validatePermissions(logisticsUser,
                OperationType.RETIRO_STOCK));
        assertFalse(service.validatePermissions(buyerUser,
                OperationType.INGRESO_STOCK));
    }

    @Test
    void buyerCanOnlyConsultItsOwnOrder() {
        assertTrue(service.canConsultAsset(buyerUser, order));
        assertFalse(service.canConsultAsset(otherBuyerUser, order));
        assertTrue(service.canConsultAsset(adminUser, order));
        assertTrue(service.canConsultAsset(supervisorUser, order));
        assertTrue(service.canConsultAsset(logisticsUser, order));
    }

    @Test
    void buyerCannotConsultMarketplaceWarehouse() {
        MarketplaceAsset marketplaceWarehouse = new MarketplaceWarehouse(
                "WH-1", "Bodega Central", "Bogota", administrator);
        assertTrue(service.canConsultAsset(logisticsUser, marketplaceWarehouse));
        assertFalse(service.canConsultAsset(buyerUser, marketplaceWarehouse));
    }

    @Test
    void customerAccessFollowsTheOwnershipRelationship() {
        assertTrue(service.canConsultCustomer(buyerUser, buyer));
        assertTrue(service.canConsultCustomer(adminUser, buyer));
        assertTrue(service.canConsultCustomer(supervisorUser, buyer));
        assertFalse(service.canConsultCustomer(buyerUser,
                otherBuyerUser.getPerson()));
        assertThrows(UnauthorizedOperationException.class,
                () -> service.ensureCanConsultCustomer(buyerUser,
                        otherBuyerUser.getPerson()));
    }

    @Test
    void assetOperationCombinesPermissionAndVisibility() {
        assertTrue(service.canExecuteAssetOperation(adminUser, order,
                OperationType.CONFIRMACION_PEDIDO));
        // The buyer owns the order but has no permission to confirm it.
        assertFalse(service.canExecuteAssetOperation(buyerUser, order,
                OperationType.CONFIRMACION_PEDIDO));
        // A buyer from another customer cannot even see the asset.
        assertFalse(service.canExecuteAssetOperation(otherBuyerUser, order,
                OperationType.CREACION_PEDIDO));
    }

    @Test
    void returnApprovalRequiresAdministratorAndReturnUnderReview() {
        Return returnRequest = new Return("RET-1", order,
                ReturnReason.PRODUCTO_DANADO);
        assertFalse(service.canApproveReturn(adminUser, returnRequest));

        returnRequest.markUnderReview();
        assertTrue(service.canApproveReturn(adminUser, returnRequest));
        assertFalse(service.canApproveReturn(supervisorUser, returnRequest));
        assertFalse(service.canApproveReturn(inactiveAdminUser,
                returnRequest));
    }

    @Test
    void shipmentApprovalRequiresApprovalAuthorityAndWaitingShipment() {
        LogisticsOperator operator = new LogisticsOperator("OP-2",
                "Operador Dos", "op2@nexus.local", UserStatus.ACTIVO);
        Shipment shipment = new Shipment(order, operator);

        assertTrue(service.canApproveShipment(adminUser, shipment));
        assertTrue(service.canApproveShipment(supervisorUser, shipment));
        assertFalse(service.canApproveShipment(buyerUser, shipment));

        order.confirmPayment();
        shipment.dispatch();
        assertFalse(service.canApproveShipment(adminUser, shipment));
    }
}
