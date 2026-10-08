# Authorization Services

## Introduction

This document defines the services responsible for **authorization** within the NexusMarket Marketplace Information Management System.

Authorization determines whether an authenticated `User` is allowed to perform a specific business operation according to the user's `SystemRole` and the business rules associated with that operation.

Authentication and authorization are separate responsibilities:

```text
Authentication
     │
     ▼
Who is the User?
     │
     ▼
Authorization
     │
     ▼
What can the User do?
```

Authentication is responsible for validating credentials and establishing the identity of the user.

Authorization is responsible for determining whether that authenticated user has permission to execute a requested operation.

The authorization services do not implement the business operation itself. They determine whether the operation may be initiated by the current `User`.

---

# Domain Model Context

Authorization is based primarily on the following Domain Models:

```text
User
SystemRole
MarketplaceAsset
Operation
```

The `User` Domain Model contains:

```text
User
├── userId
├── username
├── password
├── role : SystemRole
├── status : UserStatus
└── customer : Customer
```

The user's authorization role is represented by:

```text
User.role : SystemRole
```

The service must therefore work with the `User` Domain Model rather than receiving a primitive role or user identifier.

---

# Authorization Principles

## User as Domain Model

Authorization services must receive a `User` Domain Model.

### Incorrect

```java
authorize(
    String username,
    String role
);
```

### Correct

```java
authorize(
    User user,
    ...
);
```

The role must be obtained from:

```text
User.role
```

and not passed independently as a `String`.

---

# Operation as Domain Model

When authorization is required for a business operation, the authorization service must receive the corresponding Domain Model representing the operation or business context.

For example:

```text
Warehouse
Order
Shipment
Return
```

These models inherit from:

```text
MarketplaceAsset
```

Therefore authorization may be evaluated using:

```text
User
MarketplaceAsset
```

or a more specific Domain Model when the authorization rule requires it.

---

# No Primitive Identifiers

Authorization services must not use primitive identifiers as substitutes for Domain relationships.

### Incorrect

```java
authorizeShipment(
    String userId,
    String shipmentId
);
```

### Correct

```java
authorizeShipment(
    User user,
    Shipment shipment
);
```

The same principle applies to:

* Customer.
* Warehouse.
* Order.
* Shipment.
* Return.
* Operation.
* User.

---

# Authorization Scope

Authorization is divided into two distinct scopes that must not be conflated.

## Read Authorization

Determines whether a user is allowed to **consult or read** information belonging to a domain object.

Examples:

```text
canConsultCustomer(User user, Customer customer)
canConsultOrder(User user, Order order)
canConsultWarehouse(User user, Warehouse warehouse)
canConsultShipment(User user, Shipment shipment)
canConsultAuditLog(User user)
```

Read authorization typically depends on the user's role and the ownership relationship between the user's associated customer and the domain object.

## Execute Authorization

Determines whether a user is allowed to **execute or modify** a domain operation.

Examples:

```text
canExecuteStockIncrease(User user, Warehouse warehouse)
canExecuteStockDecrease(User user, Warehouse warehouse)
canApproveReturn(User user, Return returnRequest)
canApproveShipment(User user, Shipment shipment)
canBlockWarehouse(User user, Warehouse warehouse)
```

Execute authorization typically depends on the user's role, the current state of the affected domain object, and the applicable business rules.

---

# Authorization Responsibilities

Authorization services are responsible for:

* Determining whether a user has the required role.
* Validating that the user is allowed to **consult** a particular domain object (read scope).
* Validating that the user is allowed to **execute** a particular business operation (execute scope).
* Validating authorization according to the affected Domain Model.
* Supporting role-based authorization.
* Supporting authorization rules specific to marketplace assets.
* Supporting approval-related authorization.
* Preventing users from executing operations outside their responsibilities.
* Preventing users from consulting information outside their visibility scope.

Authorization services are not responsible for:

* Authenticating credentials.
* Validating passwords.
* Generating JWT tokens.
* Persisting users.
* Executing marketplace operations.
* Updating marketplace assets.
* Implementing REST security filters.

---

# System Roles

Authorization uses the `SystemRole` Value Object.

The currently defined roles are:

```text
BUYER
SELLER
SUPPORT_AGENT
ACCOUNT_MANAGER
SELLER_OPERATOR
SELLER_SUPERVISOR
PLATFORM_ADMIN
```

The authorization service must use the `SystemRole` Domain Value Object instead of raw strings.

---

# Role Authorization Rules

The following rules define what each role is authorized to do within the system.

## BUYER

* May consult and operate exclusively on their own orders.
* May not access orders belonging to other customers.

## SELLER

* Treated as the owning identity of marketplace assets (warehouses, catalog products).
* Operations are typically performed through associated `SELLER_OPERATOR` or `SELLER_SUPERVISOR` users.

## SUPPORT_AGENT

* May consult any customer and their orders.
* May perform order support, warehouse registration, and warehouse open/close operations.
* May not approve returns or shipments requiring authorization.

## ACCOUNT_MANAGER

* May consult **any customer** without restriction.
* May consult any marketplace asset associated with any customer.
* May perform catalog and order-related operations within their responsibilities.
* Customer access is unrestricted — no assignment or ownership relationship is required.

## SELLER_OPERATOR

* May perform operations on behalf of the `Seller` associated with their `User.customer`.
* Access is restricted to assets belonging to the associated `Seller`.

## SELLER_SUPERVISOR

* May approve shipments on behalf of the `Seller` associated with their `User.customer`.
* Access for approval is restricted to shipments initiated within their associated seller's scope.

## PLATFORM_ADMIN

* May consult any customer and any marketplace asset.
* Is the only role authorized to approve or reject returns.
* May register and manage internal employee users.

---

# User Status

Authorization must also consider the `UserStatus` associated with the authenticated `User`.

Supported statuses are:

```text
ACTIVE
INACTIVE
BLOCKED
```

A user that is not operationally active must not be authorized to execute protected business operations.

Conceptually:

```text
User
 │
 ├── status
 │
 └── role
       │
       ▼
Authorization
```

---

# 1. Authorize Operation

## Description

Determines whether a `User` is authorized to perform a specific business operation.

The operation context must be represented by a Domain Model.

---

## Input

Conceptually:

```text
User
Operation
```

The service must not receive:

```java
authorize(
    String userId,
    String operationType
);
```

Instead, the authorization context must be represented by Domain Models.

---

## Processing

```text
User
 │
 ├── status
 └── role
       │
       ▼
Authorization Service
       │
       ├── Validate User Status
       │
       ├── Validate Role
       │
       └── Validate Operation Context
```

---

## Result

The service determines whether authorization is granted.

Conceptually:

```text
AUTHORIZED
UNAUTHORIZED
```

The exact representation of the result should be defined by the Domain model and service contract.

---

# 2. Authorize Asset Operation

## Description

Determines whether a `User` may execute an operation over a `MarketplaceAsset`.

Marketplace assets include:

```text
Warehouse
Order
Shipment
Return
```

---

## Input

```text
User
MarketplaceAsset
Operation
```

The concrete asset may be:

```text
Warehouse
Order
Shipment
Return
```

---

## Processing

```text
User
 │
 ▼
Authorization Service
 │
 ├── Validate User Status
 │
 ├── Validate User Role
 │
 ├── Validate Asset
 │
 └── Validate Operation
```

---

# 3. Authorize Warehouse Operation

## Description

Determines whether a `User` is authorized to perform an operation over a `Warehouse`.

The service receives:

```text
User
Warehouse
Operation
```

Authorization may depend on:

* User role.
* User status.
* Warehouse ownership.
* Type of warehouse.
* Type of operation.
* Other Domain rules.

Information already available in the Domain Models must be used directly.

If external information is required, the service must use an Output Port.

---

# 4. Authorize Return Operation

## Description

Determines whether a `User` may execute an operation over a `Return`.

The service receives:

```text
User
Return
Operation
```

Examples of protected return operations include:

```text
RETURN_REQUEST
RETURN_APPROVAL
RETURN_REJECTION
REFUND_REGISTRATION
```

Authorization for `RETURN_APPROVAL` and `RETURN_REJECTION` requires:

```text
User.role == PLATFORM_ADMIN
```

Authorization may additionally depend on the current state of the return.

---

# 5. Authorize Shipment Operation

## Description

Determines whether a `User` may execute an operation over a `Shipment`.

The service receives:

```text
User
Shipment
Operation
```

Authorization may depend on:

* User role.
* User status.
* Shipment state.
* Customer relationship.
* Seller relationship.
* Shipment approval requirements.

---

# 6. Authorize Shipment Approval

## Description

Determines whether a `User` is authorized to approve a shipment requiring authorization.

The service receives:

```text
User
Shipment
```

The authorization decision must be based on the Domain Models.

For example, the role:

```text
SELLER_SUPERVISOR
```

may be relevant to seller shipment approval according to the defined business rules.

The service must not determine authorization from a raw string such as:

```text
"SELLER_SUPERVISOR"
```

Instead:

```text
User.role : SystemRole
```

must be evaluated.

---

# 7. Authorize Return Approval

## Description

Determines whether a `User` is authorized to approve a return.

The service receives:

```text
User
Return
```

The authorization must consider:

* User status must be `ACTIVE`.
* User role must be `PLATFORM_ADMIN`.
* Current Return status must allow approval (`UNDER_REVIEW`).

The rule is:

```text
User.role == PLATFORM_ADMIN
    AND
User.status == ACTIVE
    AND
Return.returnStatus == UNDER_REVIEW
```

The service must not approve the return itself.

Its responsibility ends with determining whether the user is authorized.

Conceptually:

```text
User + Return
      │
      ▼
Authorize Return Approval
      │
      ▼
Authorization Result
      │
      ▼
Return Service
      │
      └── Execute approval if authorized
```

---

# 8. Authorize Customer Operation

## Description

Determines whether a user may execute an operation on behalf of or over a `Customer`.

The relevant relationship is represented in the Domain Model.

For example:

```text
User.customer : Customer
```

The service must compare Domain Models rather than identifiers.

### Incorrect

```java
user.getCustomer().getIdentification()
    .equals(customer.getIdentification());
```

The authorization model should instead operate using the appropriate Domain relationship:

```text
User
 │
 └── customer : Customer
```

and:

```text
Customer
```

---

# 9. Authorize Seller Operation

## Description

Determines whether a user may perform an operation on behalf of a `Seller`.

This is particularly relevant to roles such as:

```text
SELLER_OPERATOR
SELLER_SUPERVISOR
```

The service must evaluate the relationships represented in the Domain Models.

Conceptually:

```text
User
 │
 ├── role
 │
 └── customer : Customer
                     │
                     ▼
                  Seller
```

If additional persisted information is required, the service must use the appropriate Output Port.

---

# 10. Validate User Authorization Status

## Description

Validates whether a `User` is currently eligible to execute protected operations.

The service evaluates:

```text
User.status
```

The supported statuses are:

```text
ACTIVE
INACTIVE
BLOCKED
```

Only users whose status satisfies the authorization rules may proceed.

No database lookup is required when the required status is already present in the `User` Domain Model.

---

# 11. Validate Role Authorization

## Description

Determines whether the `SystemRole` associated with a `User` permits the requested operation.

The service receives the `User` Domain Model and obtains:

```text
User.role
```

It must not receive the role independently.

---

## Conceptual Processing

```text
User
 │
 ▼
User.role
 │
 ▼
SystemRole
 │
 ▼
Authorization Rules
 │
 ▼
Authorized / Unauthorized
```

---

# 12. Validate Customer Ownership

## Description

Determines whether a customer is authorized to operate over a specific marketplace asset.

For example, a customer may only be authorized to perform operations over orders or warehouses that belong to that customer.

The service receives the relevant Domain Models.

Conceptually:

```text
User
 │
 └── customer : Customer
                     │
                     ▼
                Customer
                     │
                     ▼
                Warehouse
```

The comparison must use Domain relationships and attributes.

If determining ownership requires information not present in the supplied models, the service must use an Output Port.

---

# 13. Validate Seller Operator Authorization

## Description

Determines whether a `SELLER_OPERATOR` can perform an operation on behalf of a `Seller`.

The service receives the appropriate Domain Models:

```text
User
Seller
MarketplaceAsset
```

The service validates the relationship using Domain information.

---

# 14. Validate Seller Supervisor Authorization

## Description

Determines whether a `SELLER_SUPERVISOR` is authorized to perform approval operations for a seller.

The service receives:

```text
User
Shipment
```

and uses the Domain relationships to determine whether authorization is valid.

---

# 15. Validate Platform Admin Authorization

## Description

Determines whether a `PLATFORM_ADMIN` is authorized to perform internal marketplace operations requiring administrator privileges.

This authorization is particularly relevant to operations such as:

```text
RETURN_APPROVAL
RETURN_REJECTION
```

The service receives:

```text
User
Return
```

and validates the user's role and the current return state.

---

# Authorization and Business Services

Authorization is performed before executing protected business operations.

For example:

```text
Request
   │
   ▼
Controller
   │
   ▼
Input Port
   │
   ▼
Authorization Service
   │
   ├── Authorized
   │       │
   │       ▼
   │   Business Service
   │
   └── Unauthorized
           │
           ▼
       Business Exception
```

The authorization service does not execute the business operation.

---

# Authorization and Authentication

Authentication is responsible for identifying the user.

Authorization uses the authenticated `User` Domain Model.

Conceptually:

```text
Credentials
    │
    ▼
Authentication Service
    │
    ▼
User
    │
    ▼
Authorization Service
    │
    ▼
Business Service
```

The authorization service must not validate passwords.

Password validation belongs to the authentication services.

---

# JWT Relationship

The authentication process produces a JWT after successful credential validation.

The JWT may contain information required by the technical security mechanism.

However, the Domain authorization services must operate using the `User` Domain Model.

The Domain must not depend directly on:

* JWT libraries.
* HTTP headers.
* Authentication filters.
* Security frameworks.

The conversion from authenticated security information to a `User` Domain Model belongs to the application/security boundary.

Conceptually:

```text
JWT
 │
 ▼
Security Adapter
 │
 ▼
User Domain Model
 │
 ▼
Authorization Service
```

---

# Output Ports

Authorization services must use Output Ports whenever external information is required.

Possible Output Ports include:

```text
UserRepository
CustomerRepository
WarehouseRepository
OrderRepository
ShipmentRepository
ReturnRepository
```

The required repository depends on the authorization rule being evaluated.

For example:

```text
Authorization of warehouse operation
        │
        ▼
WarehouseRepository
```

or:

```text
Authorization of seller relationship
        │
        ▼
CustomerRepository
```

---

# UserRepository

## Description

Provides User information when the supplied `User` Domain Model does not contain sufficient information to evaluate authorization.

Conceptually:

```java
interface UserRepository {

    User find(User user);

    boolean exists(User user);
}
```

The exact contract must be defined according to the required use cases.

---

# CustomerRepository

## Description

Provides Customer information when authorization requires information not available in the supplied Customer Domain Model.

The repository is accessed only through an Output Port.

---

# WarehouseRepository

## Description

Provides Warehouse information when authorization requires external information about a warehouse.

The authorization service must never access warehouse persistence directly.

---

# OrderRepository

## Description

Provides Order information when authorization requires external information about an order.

For example, authorization may depend on the persisted state of the order.

---

# ShipmentRepository

## Description

Provides Shipment information when authorization requires external information about a shipment.

For example, shipment approval authorization may require the current persisted shipment state.

---

# ReturnRepository

## Description

Provides Return information when authorization requires external information about a return.

For example, return approval authorization may require the current persisted return state.

---

# Input Ports

The Authorization subdomain exposes the following conceptual use cases:

```text
AuthorizeOperationUseCase
AuthorizeAssetOperationUseCase
AuthorizeWarehouseOperationUseCase
AuthorizeReturnOperationUseCase
AuthorizeShipmentOperationUseCase
AuthorizeShipmentApprovalUseCase
AuthorizeReturnApprovalUseCase
AuthorizeCustomerOperationUseCase
AuthorizeSellerOperationUseCase
ValidateUserAuthorizationStatusUseCase
ValidateRoleAuthorizationUseCase
ValidateCustomerOwnershipUseCase
ValidateSellerOperatorAuthorizationUseCase
ValidateSellerSupervisorAuthorizationUseCase
ValidatePlatformAdminAuthorizationUseCase
```

---

# Example Input Ports

```java
interface AuthorizeOperationUseCase {

    void authorize(
        User user,
        Operation operation
    );
}
```

```java
interface AuthorizeWarehouseOperationUseCase {

    void authorize(
        User user,
        Warehouse warehouse,
        Operation operation
    );
}
```

```java
interface AuthorizeReturnApprovalUseCase {

    void authorize(
        User user,
        Return returnRequest
    );
}
```

```java
interface AuthorizeShipmentApprovalUseCase {

    void authorize(
        User user,
        Shipment shipment
    );
}
```

The exact return type may be defined by the authorization Domain Model or exception strategy adopted by the project.

---

# Authorization Flow

## General Flow

```text
Authenticated User
        │
        ▼
      User
        │
        ▼
Authorization Service
        │
        ├── Validate User Status
        │
        ├── Validate SystemRole
        │
        ├── Validate Domain Relationships
        │
        ├── Query Output Ports if necessary
        │
        ▼
Authorization Decision
        │
        ├── Authorized
        │       │
        │       ▼
        │   Business Service
        │
        └── Unauthorized
                │
                ▼
        Authorization Exception
```

---

# Authorization for Warehouse Operations

```text
User
 │
 ▼
Authorization Service
 │
 ├── UserStatus
 ├── SystemRole
 ├── Warehouse
 ├── Ownership
 │
 └── WarehouseRepository
          │
          ▼
Authorization Decision
```

The actual warehouse operation is subsequently executed by the Inventory service.

---

# Authorization for Return Operations

```text
User
 │
 ▼
Authorization Service
 │
 ├── UserStatus
 ├── SystemRole
 ├── Return
 │
 └── ReturnRepository
          │
          ▼
Authorization Decision
```

The Return service remains responsible for the actual return operation.

---

# Authorization for Shipment Operations

```text
User
 │
 ▼
Authorization Service
 │
 ├── UserStatus
 ├── SystemRole
 ├── Shipment
 ├── Customer relationship
 │
 └── ShipmentRepository
          │
          ▼
Authorization Decision
```

The Shipment service remains responsible for executing the shipment.

---

# Authorization Exceptions

Conceptual exceptions for this subdomain include:

```text
UnauthorizedOperationException
UserNotAuthorizedException
UserInactiveException
UserBlockedException
InsufficientRoleException
InvalidAuthorizationContextException
UnauthorizedAssetOperationException
UnauthorizedCustomerOperationException
UnauthorizedShipmentApprovalException
UnauthorizedReturnApprovalException
```

The complete exception catalog should be defined separately in the Domain Exceptions documentation.

---

# Separation from Authentication

The following responsibilities belong to Authentication:

```text
Validate username
Validate password
Load User by username
Generate JWT
```

The following responsibilities belong to Authorization:

```text
Validate User status
Validate SystemRole
Validate business permissions
Validate customer relationships
Validate asset authorization
Authorize business operations
Authorize approval operations
```

Conceptually:

```text
              Authentication
                    │
             username/password
                    │
                    ▼
                  User
                    │
                    ▼
              Authorization
                    │
          ┌─────────┼─────────┐
          ▼         ▼         ▼
     Warehouse    Order    Shipment
```

---

# Operation and Audit Integration

Authorization itself does not represent the successful execution of a business operation.

The originating business service is responsible for registering the operation after the authorized business action occurs.

For example:

```text
Shipment Request
      │
      ▼
Authorization
      │
      ▼
Shipment Service
      │
      ├── Execute Dispatch
      │
      ▼
Operation
      │
      ▼
AuditLog
```

An authorization failure may also be logged when required by the system's security/audit rules.

If such an event must be recorded, the authorization service must use the appropriate Operation and Audit Output Ports rather than accessing persistence directly.

---

# Architectural Constraints

The following rules are mandatory for the Authorization subdomain:

1. Authorization is separate from authentication.
2. Authentication determines the identity of the user.
3. Authorization determines whether the authenticated user may perform an operation.
4. Authorization services must receive Domain Models.
5. Authorization services must never receive primitive identifiers as substitutes for Domain Models.
6. `User` must be used instead of a raw `userId`.
7. `SystemRole` must be obtained from `User.role`.
8. Roles must never be represented as arbitrary strings inside authorization services.
9. `MarketplaceAsset` must be used instead of a raw asset identifier when the authorization context is a marketplace asset.
10. `Warehouse`, `Order`, `Shipment`, and `Return` must be used as their respective Domain Models when asset-specific authorization is required.
11. Customer relationships must be represented through Domain Models.
12. User relationships with customers must use the Domain relationship represented by `User`.
13. User status must be evaluated using `User.status`.
14. Authorization services must validate Domain information directly whenever it is already available.
15. External information must be obtained through Output Ports.
16. Authorization services must never access databases directly.
17. Authorization services must never access MySQL directly.
18. Authorization services must never access MongoDB directly.
19. Authorization services must never access JPA, SQL, or persistence repositories directly.
20. Output Ports must be owned by the Domain.
21. Adapters implement Output Ports.
22. JWT implementation details must remain outside the Domain.
23. Authorization services must not validate passwords.
24. Authorization services must not generate JWT tokens.
25. Authorization services must not execute marketplace operations.
26. Business services remain responsible for their own business rules.
27. Authorization must occur before protected business operations are executed.
28. Return approval authorization must be separate from the actual Return approval operation.
29. Shipment approval authorization must be separate from the actual Shipment approval operation.
30. Authorization failures must result in an appropriate Domain/Application authorization exception.
31. The Domain must remain independent of REST and security frameworks.
32. The Authorization subdomain must be fully testable without requiring infrastructure components.
