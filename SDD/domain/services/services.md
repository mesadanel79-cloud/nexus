# Services

## Introduction

This document provides a conceptual overview of the services that compose the NexusMarket Marketplace Information Management System.

The services described here define the main business capabilities exposed by the system. At this level, each service is described only in terms of its purpose and responsibility within the domain.

The detailed definition of each service—including inputs, outputs, business rules, validations, authorization requirements, domain interactions, exceptions, persistence considerations, and technical implementation—will be documented in separate files organized by **subdomain**.

The service documentation is therefore divided conceptually into the following subdomains:

- **Customer Management**
- **User and Authentication Management**
- **Catalog Management**
- **Inventory and Warehouse Management**
- **Order Management**
- **Invoicing Management**
- **Shipping Management**
- **Returns and Refunds Management**
- **Operation and Audit Management**
- **Authorization**

---

# Customer Management Services

## Register Buyer

Creates a new marketplace customer representing a natural person who purchases products and establishes the customer's initial information and account status.

## Register Seller

Creates a new marketplace customer representing a business or individual seller and associates the seller's legal or storefront information.

## Consult Customer

Retrieves the information of a marketplace customer according to the access permissions of the requesting user.

## Update Customer

Updates the information maintained for an existing marketplace customer according to the applicable business rules.

## Change Customer Status

Changes the operational status of a customer, such as activating, deactivating, or blocking the customer's marketplace relationship.

## Consult Customer Products

Retrieves the marketplace products and activity associated with a customer, such as listed catalog items, orders, and shipments.

---

# User and Authentication Management Services

## Register Customer User

Creates a system user associated with an existing `Customer` (buyer or seller).

This service allows a customer-related system identity to be created without requiring the user to be an internal platform employee.

## Register Employee User

Creates a system user representing an internal platform employee.

This service is restricted to the `PLATFORM_ADMIN` role according to the business authorization rules.

## Login

Authenticates a system user using their registered credentials and establishes an authenticated session.

## Logout

Terminates the authenticated user's current session and prevents further operations through that session.

## Consult User

Retrieves information about a system user according to the permissions of the requesting user.

## Change User Status

Changes the status of a system user's access to the application, such as activating, deactivating, or blocking the user.

---

# Catalog Management Services

## Register Product

Creates a new product listing associated with a seller and establishes its initial catalog information and status.

## Update Product

Updates the information maintained for an existing product listing according to the applicable business rules.

## Consult Product

Retrieves information about a product listing according to the permissions of the requesting user.

## Consult Catalog

Retrieves the set of product listings available in the catalog according to the applicable search and access rules.

## Change Product Status

Changes the availability status of a product listing, such as publishing, unpublishing, or discontinuing it.

---

# Inventory and Warehouse Management Services

## Register Warehouse

Creates a new warehouse associated with a seller and establishes its initial configuration and status.

## Consult Warehouse

Retrieves information about a warehouse according to the permissions of the requesting user.

## Consult Stock Level

Retrieves the current available stock of a product within a warehouse.

## Increase Stock

Adds units of a product to a warehouse's inventory and generates the corresponding business operation and audit record.

## Decrease Stock

Removes units of a product from a warehouse's inventory after validating the applicable stock and transaction conditions.

## Block Warehouse

Changes the operational status of a warehouse to blocked and records the corresponding business operation.

## Unblock Warehouse

Restores a blocked warehouse to an operational state and records the corresponding business operation.

## Close Warehouse

Permanently closes a warehouse according to the applicable business rules and records the corresponding operation.

---

# Order Management Services

## Create Order

Creates a new order for a buyer and starts the corresponding order lifecycle.

## Consult Order

Retrieves information about an order according to the requesting user's permissions.

## Confirm Order

Confirms an order after applying the required business validations and authorization rules.

## Cancel Order

Cancels an order and records the corresponding decision and operation.

## Fulfill Order

Allocates the ordered items from the designated warehouse and records the corresponding business operation and audit event.

## Register Order Payment

Registers a payment made against an existing order and updates the corresponding order information.

## Close Order

Completes the order lifecycle when the applicable conditions for closure have been satisfied.

---

# Invoicing Management Services

## Generate Invoice

Creates an invoice for a confirmed order and establishes its initial state.

## Consult Invoice

Retrieves information about an invoice according to the requesting user's permissions.

## Void Invoice

Cancels a previously generated invoice and records the corresponding business operation.

## Consult Invoice History

Retrieves the invoices associated with a customer according to the requesting user's permissions.

---

# Shipping Management Services

## Create Shipment

Creates a shipment request for an order and establishes its initial state.

## Dispatch Shipment

Executes an authorized shipment by releasing the order from the source warehouse to the carrier.

## Submit Shipment for Approval

Places a shipment into an approval state when the applicable business rules require authorization before dispatch.

## Approve Shipment

Approves a shipment that requires authorization and allows it to proceed according to the applicable business rules.

## Reject Shipment

Rejects a shipment awaiting approval and records the corresponding business operation.

## Track Shipment

Retrieves the current status and movement history of a shipment.

---

# Returns and Refunds Management Services

## Request Return

Creates a return request for an order and starts the corresponding return lifecycle.

## Consult Return

Retrieves information about a return request according to the requesting user's permissions.

## Approve Return

Approves a return request after applying the required business validations and authorization rules.

## Reject Return

Rejects a return request and records the corresponding decision and operation.

## Register Refund

Registers a refund associated with an approved return and records the corresponding business operation and audit event.

## Close Return

Completes the return lifecycle when the applicable conditions for closure have been satisfied.

---

# Operation and Audit Management Services

## Register Operation

Creates a business operation representing a significant action performed over a marketplace product or service.

Operations provide traceability between the user performing an action and the affected marketplace entity.

## Consult Operations

Retrieves the operations associated with marketplace entities according to the requesting user's permissions.

## Register Audit Event

Creates an immutable audit record for a significant business operation.

The audit record preserves information such as the operation, user, role, affected entity, timestamp, and operation-specific details.

## Consult Audit Log

Retrieves historical audit records according to the access permissions of the requesting user.

---

# Authorization Services

## Validate Permissions

Determines whether a user has permission to perform a specific business operation based on the user's role and status.

## Validate Customer Access

Determines whether a user is authorized to access information belonging to a specific customer.

## Validate Product Access

Determines whether a user is authorized to access or operate on a specific marketplace entity, such as a product, order, or shipment.

## Validate Approval Authorization

Determines whether a user has the required authority to approve a business operation, such as a shipment or a return requiring approval.

---

# Service Organization

The services described in this document provide the **high-level service catalog** of the system.

They intentionally do not describe implementation details or complete business workflows.

Detailed specifications will be maintained in separate Markdown files organized by subdomain. For example:

```text
services/
│   └── customer-services.md
│
│   └── user-authentication-services.md
│
│   └── catalog-services.md
│
│   └── inventory-warehouse-services.md
│
│   └── order-services.md
│
│   └── invoicing-services.md
│
│   └── shipping-services.md
│
│   └── returns-refunds-services.md
│
│   └── operation-audit-services.md
│
    └── authorization-services.md
```
