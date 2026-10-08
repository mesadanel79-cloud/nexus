package application.domain.services;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.MarketplaceAsset;
import application.domain.models.Order;
import application.domain.models.Person;
import application.domain.models.Return;
import application.domain.models.SellerWarehouse;
import application.domain.models.Shipment;
import application.domain.models.SystemUser;
import application.domain.ports.in.ValidateApprovalAuthorizationUseCase;
import application.domain.ports.in.ValidateAssetAccessUseCase;
import application.domain.ports.in.ValidateCustomerAccessUseCase;
import application.domain.ports.in.ValidatePermissionsUseCase;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ReturnStatus;
import application.domain.valueobjects.ShipmentStatus;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;

/**
 * Domain Service (Authorization subdomain): determines what an
 * authenticated system user is allowed to do within NexusMarket.
 *
 * Authorization is separate from authentication: authentication establishes
 * the identity of the user, authorization evaluates the permissions of that
 * user. This service never validates credentials, never generates tokens and
 * never executes the business operation itself.
 *
 * Business rules enforced:
 * - Only users whose person is ACTIVO may execute protected operations.
 * - The SystemRole is always obtained from the User domain model
 *   (SystemUser.person.role); roles are never received as raw strings.
 * - Approval authority is separate from the approval operation: return
 *   approval requires the ADMINISTRADOR role and a return under review;
 *   shipment approval requires ADMINISTRADOR or SUPERVISOR authority.
 * - Order operations are split between the participant and the internal
 *   lifecycle: buyers create, pay and cancel their own orders, while
 *   confirmation, fulfillment and closure are restricted to staff roles.
 *
 * All authorization decisions are evaluated directly from the Domain Models
 * received, so the service is fully testable without infrastructure.
 */
public class AuthorizationService implements ValidatePermissionsUseCase,
        ValidateAssetAccessUseCase, ValidateCustomerAccessUseCase,
        ValidateApprovalAuthorizationUseCase {

    @Override
    public boolean validatePermissions(SystemUser user,
                                       OperationType operationType) {
        if (!isOperational(user) || operationType == null) {
            return false;
        }
        SystemRole role = user.getPerson().getRole();
        if (operationType.isApprovalOperation()) {
            if (operationType.equals(OperationType.APROBACION_ENVIO)
                    || operationType.equals(OperationType.RECHAZO_ENVIO)) {
                return isAdministrator(role) || isSupervisor(role);
            }
            return isAdministrator(role);
        }
        if (operationType.isWarehouseOperation()) {
            return isAdministrator(role) || isLogisticsOperator(role);
        }
        if (operationType.isStockOperation()) {
            return isAdministrator(role) || isLogisticsOperator(role)
                    || isSeller(role);
        }
        if (operationType.isOrderOperation()) {
            return allowsOrderOperation(role, operationType);
        }
        if (operationType.isShipmentOperation()) {
            return isAdministrator(role) || isSupervisor(role)
                    || isLogisticsOperator(role) || isSeller(role);
        }
        if (operationType.isReturnOperation()) {
            return isAdministrator(role) || isSupervisor(role)
                    || isBuyer(role);
        }
        return false;
    }

    /**
     * Order operations are separated between the actions performed by the
     * marketplace participant and the internal steps of the order lifecycle:
     * - CREACION_PEDIDO, PAGO_PEDIDO and CANCELACION_PEDIDO are executed by
     *   the buyer (or by staff acting on behalf of the platform).
     * - CONFIRMACION_PEDIDO, CUMPLIMIENTO_PEDIDO and CIERRE_PEDIDO are
     *   internal lifecycle steps: the buyer that owns the order never holds
     *   permission to execute them.
     */
    private boolean allowsOrderOperation(SystemRole role,
                                         OperationType operationType) {
        boolean participantAction = operationType.equals(OperationType.CREACION_PEDIDO)
                || operationType.equals(OperationType.PAGO_PEDIDO)
                || operationType.equals(OperationType.CANCELACION_PEDIDO);
        if (participantAction) {
            return isAdministrator(role) || isSupervisor(role) || isBuyer(role);
        }
        if (operationType.equals(OperationType.CUMPLIMIENTO_PEDIDO)) {
            return isAdministrator(role) || isSupervisor(role)
                    || isLogisticsOperator(role);
        }
        // CONFIRMACION_PEDIDO and CIERRE_PEDIDO: internal staff steps only.
        return isAdministrator(role) || isSupervisor(role);
    }

    @Override
    public void authorize(SystemUser user, OperationType operationType) {
        if (!validatePermissions(user, operationType)) {
            throw new UnauthorizedOperationException(
                    "User " + describe(user)
                            + " is not authorized to execute operation "
                            + (operationType == null
                                    ? "null" : operationType.getCode()));
        }
    }

    @Override
    public boolean canConsultAsset(SystemUser user, MarketplaceAsset asset) {
        if (!isOperational(user) || asset == null) {
            return false;
        }
        SystemRole role = user.getPerson().getRole();
        if (isAdministrator(role) || isSupervisor(role)
                || isLogisticsOperator(role)) {
            return true;
        }
        Person requester = user.getPerson();
        if (asset instanceof Order order) {
            return isBuyer(role) && belongsTo(order.getBuyer(), requester);
        }
        if (asset instanceof Return returnRequest) {
            return isBuyer(role)
                    && belongsTo(returnRequest.getOrder().getBuyer(), requester);
        }
        if (asset instanceof Shipment shipment) {
            return isBuyer(role)
                    && belongsTo(shipment.getOrder().getBuyer(), requester);
        }
        if (asset instanceof SellerWarehouse sellerWarehouse) {
            return isSeller(role)
                    && belongsTo(sellerWarehouse.getOwner(), requester);
        }
        return false;
    }

    @Override
    public boolean canExecuteAssetOperation(SystemUser user,
                                            MarketplaceAsset asset,
                                            OperationType operationType) {
        return validatePermissions(user, operationType)
                && canConsultAsset(user, asset);
    }

    @Override
    public void ensureCanExecuteAssetOperation(SystemUser user,
                                               MarketplaceAsset asset,
                                               OperationType operationType) {
        if (!canExecuteAssetOperation(user, asset, operationType)) {
            throw new UnauthorizedOperationException(
                    "User " + describe(user)
                            + " is not authorized to execute operation "
                            + (operationType == null
                                    ? "null" : operationType.getCode())
                            + " over asset " + describe(asset));
        }
    }

    @Override
    public boolean canConsultCustomer(SystemUser user, Person customer) {
        if (!isOperational(user) || customer == null) {
            return false;
        }
        SystemRole role = user.getPerson().getRole();
        if (isAdministrator(role) || isSupervisor(role)
                || isLogisticsOperator(role)) {
            return true;
        }
        return belongsTo(customer, user.getPerson());
    }

    @Override
    public void ensureCanConsultCustomer(SystemUser user, Person customer) {
        if (!canConsultCustomer(user, customer)) {
            throw new UnauthorizedOperationException(
                    "User " + describe(user)
                            + " is not authorized to consult the customer "
                            + describe(customer));
        }
    }

    @Override
    public boolean canApproveReturn(SystemUser user, Return returnRequest) {
        if (!isOperational(user) || returnRequest == null) {
            return false;
        }
        return isAdministrator(user.getPerson().getRole())
                && ReturnStatus.EN_REVISION
                        .equals(returnRequest.getReturnStatus());
    }

    @Override
    public boolean canApproveShipment(SystemUser user, Shipment shipment) {
        if (!isOperational(user) || shipment == null) {
            return false;
        }
        SystemRole role = user.getPerson().getRole();
        return (isAdministrator(role) || isSupervisor(role))
                && ShipmentStatus.EN_PREPARACION
                        .equals(shipment.getShipmentStatus());
    }

    @Override
    public void ensureCanApproveReturn(SystemUser user, Return returnRequest) {
        if (!canApproveReturn(user, returnRequest)) {
            throw new UnauthorizedOperationException(
                    "User " + describe(user)
                            + " is not authorized to approve the return "
                            + (returnRequest == null
                                    ? "null" : returnRequest.getReturnId()));
        }
    }

    @Override
    public void ensureCanApproveShipment(SystemUser user, Shipment shipment) {
        if (!canApproveShipment(user, shipment)) {
            throw new UnauthorizedOperationException(
                    "User " + describe(user)
                            + " is not authorized to approve the shipment "
                            + (shipment == null
                                    ? "null" : shipment.getShipmentId()));
        }
    }

    /** Only an operational (ACTIVO) user may execute protected operations. */
    private boolean isOperational(SystemUser user) {
        return user != null && user.getPerson() != null
                && UserStatus.ACTIVO.equals(user.getPerson().getStatus());
    }

    private boolean isAdministrator(SystemRole role) {
        return SystemRole.ADMINISTRADOR.equals(role);
    }

    private boolean isSupervisor(SystemRole role) {
        return SystemRole.SUPERVISOR.equals(role);
    }

    private boolean isLogisticsOperator(SystemRole role) {
        return SystemRole.OPERADOR_LOGISTICO.equals(role);
    }

    private boolean isSeller(SystemRole role) {
        return SystemRole.VENDEDOR.equals(role);
    }

    private boolean isBuyer(SystemRole role) {
        return SystemRole.COMPRADOR.equals(role);
    }

    /**
     * Evaluates the ownership relationship represented by the Domain Models
     * (user.person against the owner of the asset or customer).
     */
    private boolean belongsTo(Person owner, Person requester) {
        if (owner == null || requester == null) {
            return false;
        }
        return owner == requester
                || owner.getIdentifier().equals(requester.getIdentifier());
    }

    private String describe(SystemUser user) {
        return user == null ? "unknown" : user.getUsername();
    }

    private String describe(Person person) {
        return person == null ? "unknown" : person.getIdentifier();
    }

    private String describe(MarketplaceAsset asset) {
        return asset == null
                ? "unknown"
                : asset.getAssetType() + ":" + asset.getAssetIdentifier();
    }
}
