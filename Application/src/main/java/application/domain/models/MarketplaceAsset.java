package application.domain.models;

/**
 * MarketplaceAsset - Domain Model abstraction.
 *
 * Represents any marketplace entity over which significant business
 * operations are executed and therefore traced by the Operation and Audit
 * subdomain.
 *
 * Concrete marketplace assets are:
 * - Warehouse
 * - Order
 * - Shipment
 * - Return
 *
 * Operation.affectedAsset and AuditLog.affectedAsset are expressed in terms
 * of this abstraction so the traceability model does not depend on a
 * specific asset type, and never on primitive identifiers.
 */
public interface MarketplaceAsset {

    /** Unique identifier of the affected marketplace asset. */
    String getAssetIdentifier();

    /** Business type of the asset (e.g. ORDER, SHIPMENT, RETURN, WAREHOUSE). */
    String getAssetType();
}
