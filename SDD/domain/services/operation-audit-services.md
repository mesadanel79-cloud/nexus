# Operation and Audit Services

## Introduction

This document defines the services responsible for managing **business operations and audit records** within the NexusMarket Marketplace Information Management System.

The purpose of this subdomain is to provide traceability for significant business actions performed over marketplace assets.

The system considers the following as marketplace assets:

- `Warehouse`
- `Order`
- `Shipment`
- `Return`

All of them inherit from:

```text
MarketplaceAsset
```

Every significant business action performed over a marketplace asset generates an `Operation`.

Relevant operations must also generate an `AuditLog` record.

Conceptually:

```text
MarketplaceAsset
      │
      ▼
Business Operation
      │
      ├── Operation
      │
      └── AuditLog
```

This subdomain is responsible for recording and consulting these events. It does not implement the business rules of the assets that originate them.

For example, the Inventory service decides whether a warehouse can be blocked. The Operation and Audit services are responsible for registering that the blocking operation occurred.

---

# Domain Model Context

The main Domain Models involved are:

```text
Operation
AuditLog
MarketplaceAsset
User
```

Conceptually:

```text
Operation
├── operationId
├── operationType
├── executionDate
├── performedBy : User
└── affectedAsset : MarketplaceAsset
```

And:

```text
AuditLog
├── auditId
├── operationType
├── operationDate
├── performedBy : User
├── userRole : SystemRole
├── affectedAsset : MarketplaceAsset
└── details : Map<String, Object>
```

The relationships must be represented using Domain Models.

For example:

```text
Operation.performedBy : User
Operation.affectedAsset : MarketplaceAsset
```

and:

```text
AuditLog.performedBy : User
AuditLog.affectedAsset : MarketplaceAsset
```

The Domain Model must not replace these relationships with primitive identifiers such as:

```text
String userId
String assetId
```

Persistence adapters are responsible for translating Domain relationships into database representations.

---

# Purpose of Operations

An `Operation` represents a business action executed by the system.

Examples include:

```text
WAREHOUSE_OPENING
STOCK_INCREASE
STOCK_DECREASE
WAREHOUSE_BLOCKING
WAREHOUSE_UNBLOCKING
WAREHOUSE_CLOSING
ORDER_CREATION
ORDER_CONFIRMATION
ORDER_CANCELLATION
ORDER_FULFILLMENT
ORDER_PAYMENT
ORDER_CLOSURE
SHIPMENT_CREATION
SHIPMENT_APPROVAL
SHIPMENT_REJECTION
SHIPMENT_DISPATCH
SHIPMENT_EXPIRATION
RETURN_REQUEST
RETURN_APPROVAL
RETURN_REJECTION
REFUND_REGISTRATION
RETURN_CLOSURE
```

The exact list of supported operation types is defined by the `OperationType` Value Object.

The Operation represents the business event independently from its persistence mechanism.

---

# Purpose of Audit Logs

An `AuditLog` represents the historical record of a significant business event.

Audit records provide traceability regarding:

* What operation occurred.
* When it occurred.
* Which user performed it.
* What role the user had.
* Which marketplace asset was affected.
* Additional operation-specific information.

The audit record is intended to be immutable after creation.

Conceptually:

```text
Operation
     │
     ▼
AuditLog
     │
     └── Immutable historical record
```

Audit records are persisted in MongoDB according to the architecture.

The Domain communicates with MongoDB exclusively through an Output Port.

---

# Service Design Principles

## Domain Model Parameters

All Operation and Audit services must receive Domain Models.

They must never receive:

* `String` identifiers.
* Primitive identifiers.
* Individual attributes as substitutes for Domain Models.
* REST DTOs.
* Persistence entities.

### Incorrect

```java
registerOperation(
    String userId,
    String assetId,
    OperationType operationType
);
```

### Correct

```java
registerOperation(Operation operation);
```

The same rule applies to audit operations.

### Incorrect

```java
createAudit(
    String userId,
    String assetId,
    OperationType operationType
);
```

### Correct

```java
createAudit(AuditLog auditLog);
```

---

# Business Responsibility Boundary

The Operation and Audit services must not determine whether a marketplace operation is business-valid.

For example:

```text
Inventory Service
        │
        ├── Determines whether stock decrease is valid
        │
        ├── Updates Warehouse
        │
        ▼
Operation and Audit Services
        │
        ├── Register operation
        │
        └── Register audit
```

The Operation and Audit subdomain is responsible for **traceability**, not for deciding whether the originating business action is allowed.

---

# External Information

The services must use the Domain Models received as their primary source of business information.

If information required for the operation or audit process is not available in the Domain Model, the service must use an Output Port.

The service must never access:

* MySQL.
* MongoDB.
* SQL.
* JPA.
* REST.
* Infrastructure components.

directly.

The communication flow is:

```text
Operation/Audit Service
          │
          ▼
      Output Port
          │
          ▼
    Output Adapter
          │
          ▼
   External Resource
```

---

# 1. Register Operation

## Description

Registers a business operation performed over a marketplace asset.

The operation must contain the Domain information required for traceability.

---

## Input

```text
Operation
```

The service must not receive individual values such as:

```java
registerOperation(
    String userId,
    String assetId,
    OperationType type
);
```

Instead:

```java
registerOperation(Operation operation);
```

---

## Domain Validations

The service validates information available in the `Operation` Domain Model.

Examples:

* Operation type exists.
* Execution date is valid.
* Performing user is valid.
* Affected asset is valid.
* Required relationships are present.

---

## User Relationship

The operation contains:

```text
Operation.performedBy : User
```

The service must work with the `User` Domain Model.

If additional persisted information is required, the service must use:

```text
UserRepository
```

through an Output Port.

---

## Asset Relationship

The operation contains:

```text
Operation.affectedAsset : MarketplaceAsset
```

The service must not receive a primitive asset identifier.

The concrete asset may be:

```text
Warehouse
Order
Shipment
Return
```

The service works against the abstraction:

```text
MarketplaceAsset
```

---

## Persistence

The operation is persisted through:

```text
OperationRepository
```

---

## Audit

Depending on the audit rules, registering an operation may also result in an `AuditLog`.

Conceptually:

```text
Operation
     │
     ├── OperationRepository
     │
     └── AuditLog
            │
            ▼
       AuditRepository
```

---

# 2. Register Audit Log

## Description

Creates an immutable audit record for a significant business event.

---

## Input

```text
AuditLog
```

The service must receive the complete Domain Model.

### Incorrect

```java
registerAudit(
    String userId,
    String assetId,
    OperationType operationType
);
```

### Correct

```java
registerAudit(AuditLog auditLog);
```

---

## Domain Validations

The service validates:

* Operation type.
* Operation date.
* Performing user.
* User role.
* Affected asset.
* Required audit details.

The validation must use the information contained in the Domain Model whenever possible.

---

## Immutability

Once an `AuditLog` is persisted, it must not be modified as part of normal business processing.

Conceptually:

```text
AuditLog
   │
   ▼
Create
   │
   ▼
Persist
   │
   ▼
Immutable historical record
```

The service must not expose an update operation for existing audit records unless a future business requirement explicitly defines one.

---

## Persistence

The audit record is persisted through:

```text
AuditRepository
```

The concrete adapter stores the record in MongoDB.

---

# 3. Register Operation and Audit

## Description

Coordinates the registration of a business operation and its corresponding audit record.

This service is useful when the originating business service must guarantee that an operation and its audit information are both registered as part of the same application flow.

---

## Input

The service receives the appropriate Domain Model representing the operation.

Conceptually:

```text
Operation
```

The service must not receive primitive identifiers.

---

## Processing

```text
Operation
    │
    ▼
Register Operation
    │
    ├── Persist Operation
    │
    └── Create AuditLog
             │
             ▼
       Persist AuditLog
```

The AuditLog can be constructed from the information contained in the `Operation` and the corresponding Domain Models.

---

## Audit Information

The generated audit record must include the information required by the `AuditLog` Domain Model:

```text
operationType
operationDate
performedBy
userRole
affectedAsset
details
```

---

# 4. Consult Operations

## Description

Retrieves operations recorded by the system.

The returned information must be represented using the `Operation` Domain Model.

Persistence entities must never be exposed.

---

## Input

The service must receive the appropriate Domain Model representing the query criteria.

The query must not be represented solely by primitive identifiers.

For example, instead of:

```java
findOperations(String userId);
```

the application should use the appropriate Domain Model representation.

---

## Processing

```text
Domain Model
     │
     ▼
OperationRepository
     │
     ▼
List<Operation>
```

The repository returns Domain Models to the service.

---

# 5. Consult Audit Logs

## Description

Retrieves audit records for historical traceability.

The returned information must be represented by the `AuditLog` Domain Model.

---

## Input

The service receives the appropriate Domain Model representing the audit query.

It must not define application-level contracts based on primitive identifiers alone.

---

## Processing

```text
Domain Model
     │
     ▼
AuditRepository
     │
     ▼
List<AuditLog>
```

---

# 6. Consult Asset Operations

## Description

Retrieves operations associated with a specific marketplace asset.

The affected asset is represented by:

```text
Operation.affectedAsset : MarketplaceAsset
```

The service must work with the Domain Model rather than receiving a raw asset identifier.

---

## Input

```text
MarketplaceAsset
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
MarketplaceAsset
      │
      ▼
Operation Service
      │
      ▼
OperationRepository
      │
      ▼
List<Operation>
```

---

# 7. Consult User Operations

## Description

Retrieves operations performed by a specific system user.

The user is represented by the:

```text
User
```

Domain Model.

---

## Input

```text
User
```

The service must not receive:

```java
findOperationsByUser(String userId);
```

as its Domain-level contract.

Instead, the operation must receive the appropriate Domain Model.

---

## Processing

```text
User
 │
 ▼
Operation Service
 │
 ▼
OperationRepository
 │
 ▼
List<Operation>
```

---

# 8. Consult Asset Audit Trail

## Description

Retrieves audit records associated with a marketplace asset.

The affected asset is represented through:

```text
AuditLog.affectedAsset : MarketplaceAsset
```

---

## Input

```text
MarketplaceAsset
```

The service must not receive a primitive asset identifier.

---

## Processing

```text
MarketplaceAsset
      │
      ▼
Audit Service
      │
      ▼
AuditRepository
      │
      ▼
List<AuditLog>
```

---

# 9. Consult User Audit Trail

## Description

Retrieves audit records associated with operations performed by a specific user.

The user is represented through:

```text
AuditLog.performedBy : User
```

---

## Input

```text
User
```

---

## Processing

```text
User
 │
 ▼
Audit Service
 │
 ▼
AuditRepository
 │
 ▼
List<AuditLog>
```

---

# Operation Repository

## Description

`OperationRepository` defines the persistence contract required by the Domain to store and retrieve business operations.

It belongs to:

```text
domain/ports/out/
```

Conceptually:

```java
interface OperationRepository {

    Operation save(Operation operation);

    List<Operation> find(Operation operation);
}
```

The exact methods must be defined according to the use cases required by the system.

The implementation belongs to the persistence adapter.

---

# Audit Repository

## Description

`AuditRepository` defines the persistence contract required by the Domain to store and retrieve audit records.

It belongs to:

```text
domain/ports/out/
```

Conceptually:

```java
interface AuditRepository {

    AuditLog save(AuditLog auditLog);

    List<AuditLog> find(AuditLog auditLog);
}
```

The implementation belongs to the MongoDB persistence adapter.

---

# User Repository

## Description

The Operation and Audit services may require information about the `User` that performed an operation.

When the supplied `User` Domain Model does not contain sufficient information, the service may use:

```text
UserRepository
```

through an Output Port.

The Operation and Audit services must not query the user database directly.

---

# Asset Repositories

The affected asset is represented by the abstract:

```text
MarketplaceAsset
```

The concrete asset can be:

```text
Warehouse
Order
Shipment
Return
```

If external asset information is required, the appropriate repository Output Port must be used.

Conceptually:

```text
Operation/Audit Service
          │
          ├── WarehouseRepository
          │
          ├── OrderRepository
          │
          ├── ShipmentRepository
          │
          └── ReturnRepository
```

The service must not access any of these persistence mechanisms directly.

---

# Input Ports

The Operation and Audit subdomain exposes the following conceptual use cases:

```text
RegisterOperationUseCase
RegisterAuditLogUseCase
RegisterOperationAndAuditUseCase
ConsultOperationsUseCase
ConsultAuditLogsUseCase
ConsultAssetOperationsUseCase
ConsultUserOperationsUseCase
ConsultAssetAuditTrailUseCase
ConsultUserAuditTrailUseCase
```

---

# Example Input Ports

```java
interface RegisterOperationUseCase {

    Operation register(Operation operation);
}
```

```java
interface RegisterAuditLogUseCase {

    AuditLog register(AuditLog auditLog);
}
```

```java
interface RegisterOperationAndAuditUseCase {

    Operation register(Operation operation);
}
```

The exact return type and coordination strategy may be refined during implementation.

---

# Business Service Integration

The Operation and Audit services are not invoked independently of the business services that generate the events.

For example, an Inventory operation may follow:

```text
Inventory Service
        │
        ├── Validate business rules
        │
        ├── Update Warehouse
        │
        └── Register Operation
                │
                └── Register Audit
```

Similarly, an Order fulfillment:

```text
Order Service
     │
     ├── Validate fulfillment
     │
     ├── Update Order
     │
     └── Register Operation
             │
             └── Register Audit
```

And a Shipment approval:

```text
Shipment Service
       │
       ├── Validate approval
       │
       ├── Update Shipment
       │
       └── Register Operation
               │
               └── Register Audit
```

The originating service remains responsible for its own business rules.

The Operation and Audit services are responsible for traceability.

---

# Operation Types

The `OperationType` Value Object defines the business operations that can be registered.

The currently defined values include:

```text
WAREHOUSE_OPENING
STOCK_INCREASE
STOCK_DECREASE
WAREHOUSE_BLOCKING
WAREHOUSE_UNBLOCKING
WAREHOUSE_CLOSING
ORDER_CREATION
ORDER_CONFIRMATION
ORDER_CANCELLATION
ORDER_FULFILLMENT
ORDER_PAYMENT
ORDER_CLOSURE
SHIPMENT_CREATION
SHIPMENT_APPROVAL
SHIPMENT_REJECTION
SHIPMENT_DISPATCH
SHIPMENT_EXPIRATION
RETURN_REQUEST
RETURN_APPROVAL
RETURN_REJECTION
REFUND_REGISTRATION
RETURN_CLOSURE
```

The Operation and Audit services must not create arbitrary operation type strings.

They must use the `OperationType` Domain Value Object.

---

# Audit Details

The `AuditLog` contains:

```text
details : Map<String, Object>
```

This field allows operation-specific information to be preserved without changing the core audit structure for every new business event.

Examples may include:

```text
Stock Decrease
├── product
├── quantity
└── warehouse

Order Fulfillment
├── orderLines
└── fulfillmentWarehouse

Shipment Dispatch
├── order
└── carrier

Refund Registration
├── amount
└── returnReason
```

The exact content of `details` must be defined by the originating business service and the corresponding Domain rules.

The Audit service must not introduce business meaning that does not belong to the originating operation.

---

# Audit User Role

The `AuditLog` contains:

```text
userRole : SystemRole
```

The role must come from the `User` Domain Model at the time the operation is registered.

Conceptually:

```text
User
 ├── role
 │
 ▼
Operation
 │
 ▼
AuditLog
 └── userRole
```

The role stored in the audit record represents the user's role associated with the operation.

---

# Audit Immutability

Audit records represent historical events.

Therefore:

1. An audit record is created when the event occurs.
2. It is persisted.
3. It is not modified as part of normal business processing.
4. Historical information must remain available for traceability.

Conceptually:

```text
Business Event
      │
      ▼
Create AuditLog
      │
      ▼
Persist
      │
      ▼
Immutable History
```

---

# Persistence Architecture

Operations and audit records may use different persistence technologies.

Conceptually:

```text
                  Domain
                    │
          ┌─────────┴─────────┐
          │                   │
          ▼                   ▼
OperationRepository    AuditRepository
          │                   │
          ▼                   ▼
     MySQL Adapter       MongoDB Adapter
          │                   │
          ▼                   ▼
        MySQL              MongoDB
```

The Domain remains unaware of the concrete databases.

---

# Operation Registration Flow

```text
Marketplace Asset Service
          │
          ▼
      Operation
          │
          ▼
RegisterOperationUseCase
          │
          ▼
Operation Service
          │
          ├── Validate Domain Model
          │
          ├── OperationRepository
          │
          └── AuditRepository
```

---

# Audit Consultation Flow

```text
REST Request
     │
     ▼
Request DTO
     │
     ▼
Request Mapper
     │
     ▼
Domain Model
     │
     ▼
ConsultAuditLogsUseCase
     │
     ▼
Audit Service
     │
     ▼
AuditRepository
     │
     ▼
MongoDB Adapter
     │
     ▼
MongoDB
```

The persistence document must be mapped back to `AuditLog` before leaving the persistence adapter.

---

# Validation Strategy

## Domain Validation

The Operation and Audit services validate the information that belongs to their Domain Models.

For `Operation`:

```text
operationId
operationType
executionDate
performedBy
affectedAsset
```

For `AuditLog`:

```text
auditId
operationType
operationDate
performedBy
userRole
affectedAsset
details
```

---

## External Validation

External information must be obtained through Output Ports.

Examples:

```text
UserRepository
OperationRepository
AuditRepository
WarehouseRepository
OrderRepository
ShipmentRepository
ReturnRepository
```

The service must never query the database directly.

---

# Exceptions

Conceptual exceptions for this subdomain include:

```text
InvalidOperationException
OperationNotFoundException
InvalidOperationTypeException
InvalidOperationUserException
InvalidAffectedAssetException
InvalidAuditLogException
AuditLogNotFoundException
AuditLogAlreadyExistsException
InvalidAuditInformationException
```

The complete exception catalog should be defined separately in the Domain Exceptions documentation.

---

# Architectural Constraints

The following rules are mandatory for the Operation and Audit subdomain:

1. `Operation` is a Domain Model.
2. `AuditLog` is a Domain Model.
3. `Operation.performedBy` must be represented by `User`.
4. `Operation.affectedAsset` must be represented by `MarketplaceAsset`.
5. `AuditLog.performedBy` must be represented by `User`.
6. `AuditLog.affectedAsset` must be represented by `MarketplaceAsset`.
7. Domain relationships must not be replaced by primitive identifiers.
8. Services must receive Domain Models or Value Objects.
9. Services must never receive primitive identifiers as substitutes for Domain Models.
10. Services must never receive individual attributes when the information belongs to a Domain Model.
11. REST DTOs must never enter the Domain layer.
12. Persistence entities and MongoDB documents must never enter the Domain layer.
13. Business services that originate operations remain responsible for their own business rules.
14. Operation and Audit services are responsible for traceability.
15. `OperationType` must be represented by the `OperationType` Domain Value Object.
16. Audit records must use the `SystemRole` associated with the performing `User`.
17. Audit records are immutable after persistence.
18. Operation persistence must occur through `OperationRepository`.
19. Audit persistence must occur through `AuditRepository`.
20. Services must always use Output Ports to communicate with external resources.
21. Services must never access MySQL directly.
22. Services must never access MongoDB directly.
23. Services must never access SQL, JPA, or database repositories directly.
24. Operation persistence may be implemented using MySQL.
25. Audit persistence is implemented using MongoDB according to the architecture.
26. Persistence adapters are responsible for mapping persistence representations to Domain Models.
27. Persistence entities and documents must never be exposed through the API.
28. Significant marketplace business actions must generate an `Operation`.
29. Relevant business operations must generate an `AuditLog`.
30. Audit information must preserve the historical context of the operation.
31. The Domain must remain independent from persistence technologies.
32. The Operation and Audit subdomain must be testable without MySQL, MongoDB, REST, or infrastructure components.
