package application.domain.ports.in;

import application.domain.models.MarketplaceAsset;
import application.domain.models.SystemUser;
import application.domain.valueobjects.OperationType;

/**
 * Input Port (Authorization subdomain): determines whether a system user is
 * authorized to consult or operate on a specific marketplace asset
 * (Warehouse, Order, Shipment, Return).
 *
 * The asset is always received as a MarketplaceAsset Domain Model, never as
 * a primitive asset identifier.
 */
public interface ValidateAssetAccessUseCase {

    /** True when the user may consult the information of the asset. */
    boolean canConsultAsset(SystemUser user, MarketplaceAsset asset);

    /**
     * True when the user may execute the requested operation over the
     * asset, combining role permissions with asset visibility.
     */
    boolean canExecuteAssetOperation(SystemUser user, MarketplaceAsset asset,
                                     OperationType operationType);

    /**
     * Validates the operation over the asset and fails with
     * UnauthorizedOperationException when it is not allowed.
     */
    void ensureCanExecuteAssetOperation(SystemUser user, MarketplaceAsset asset,
                                        OperationType operationType);
}
