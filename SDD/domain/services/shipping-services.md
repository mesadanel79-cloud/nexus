# Shipping Services

## 1. Purpose

This document defines the application services of the **Shipping
Management** subdomain of the NexusMarket Marketplace Information
Management System.

The services manage the shipment lifecycle:

``` text
Create Shipment
      |
      +--> APPROVED ------------------> Dispatch Shipment
      |
      +--> WAITING_FOR_APPROVAL
                |
                +--> Approve Shipment --> Dispatch Shipment
                |
                +--> Reject Shipment
                |
                +--> Expire Shipment

Track Shipment
```

`Shipment` is a `MarketplaceAsset` Domain Model. Significant shipment
lifecycle actions generate an `Operation`, which is subsequently
registered in the audit trail through the Operation and Audit services.

This document describes Domain/Application behavior and its interaction
with Input Ports and Output Ports. REST DTOs, persistence entities,
controllers, JPA mappings, SQL, MongoDB, and infrastructure-specific
implementations remain outside this layer.

------------------------------------------------------------------------

# 2. Authoritative Design Rules

## 2.1 Domain-first

Shipment services operate on Domain Models and Value Objects.

They must not receive:

-   REST Request DTOs.
-   Persistence entities.
-   Primitive identifiers as substitutes for Domain Models.
-   Individual attributes when the corresponding Domain Model exists.

Correct:

``` java
Shipment dispatch(Shipment shipment, User user, Customer customer);
```

Incorrect:

``` java
Shipment dispatch(
    String orderId,
    String warehouseId,
    BigDecimal fee,
    Integer userId
);
```

The application/adapter layer is responsible for translating external
representations into Domain Models before invoking the Input Port.

## 2.2 Enrichment before business validation

When the supplied Domain Model does not contain authoritative
information required by the use case, the service resolves the
corresponding Domain Model through an Output Port.

The general pattern is:

``` text
Input Domain Models
        |
        v
Resolve authoritative entities
        |
        v
Enrich execution context
        |
        v
Validate relationships and business rules
        |
        v
Execute domain behavior
        |
        v
Persist
        |
        v
Register Operation + Audit
        |
        v
Return Domain Model
```

Enrichment does not mean replacing Domain Models with primitive
identifiers. Identifiers may be used internally by an adapter to locate
an entity, but the service works with the resulting Domain Model.

## 2.3 Output Ports

A Shipment service must never access infrastructure directly.

``` text
Shipment Service
       |
       v
Output Port
       |
       v
Output Adapter
       |
       v
External Resource
```

Typical Shipment dependencies are:

``` text
ShipmentRepositoryPort
OrderRepositoryPort
WarehouseRepositoryPort
UserRepositoryPort
BusinessConfigurationPort
```

Additional authorization, notification, operation, or audit capabilities
are used through their respective domain ports/services when required by
the concrete implementation.

## 2.4 Single-service orchestration

Each service may contain the complete orchestration required for its use
case.

The objective is not to artificially fragment validations into many
services. The objective is to maintain:

-   high cohesion;
-   low coupling;
-   explicit business flow;
-   dependency inversion;
-   testability;
-   domain independence.

------------------------------------------------------------------------

# 3. Shipment Domain Context

Conceptually:

``` text
MarketplaceAsset
      |
      +--> Shipment
             |
             +--> order : Order
             +--> originWarehouse : Warehouse
             +--> shippingFee : BigDecimal
             +--> creationDate : LocalDateTime
             +--> approvalDate : LocalDateTime
             +--> shipmentStatus : ShipmentStatus
             +--> createdBy : User
             +--> approvedBy : User?
```

The Domain Model defines the relationships as objects:

``` text
Shipment
   |
   +--> Order
   +--> Warehouse
   +--> User
   +--> User
```

The service may receive a `Shipment` whose order/warehouse references
are sufficient to locate authoritative Domain Models through
`OrderRepositoryPort` and `WarehouseRepositoryPort`. The enrichment
process then works with the resolved `Order` and `Warehouse` Domain
Models.

Unlike `Transfer`, which relates two symmetric `BankAccount` Domain
Models (source and destination), `Shipment` relates two asymmetric
Domain Models: the `Warehouse` it originates from, and the `Order` it
fulfills logistically. The destination address is represented indirectly
through `Order.buyer` rather than a separate marketplace asset.

------------------------------------------------------------------------

# 4. Shipment Lifecycle

The supported lifecycle is:

``` text
PENDING
   |
   +------------------> REJECTED
   |
   v
WAITING_FOR_APPROVAL
   |
   +------------------> REJECTED
   |
   +------------------> EXPIRED
   |
   v
APPROVED
   |
   v
DISPATCHED
```

At creation, the current implementation determines the initial status
from the configured approval threshold:

``` text
shippingFee > threshold
      |
      +--> YES --> WAITING_FOR_APPROVAL
      |
      +--> NO  --> APPROVED
```

The threshold is typically used to require supervisor sign-off for
expensive or expedited shipments (e.g. international, express, or
high-value freight).

The lifecycle transitions themselves are Domain rules and must not be
decided by persistence adapters.

------------------------------------------------------------------------

# 5. Common Service Processing Pattern

Every Shipment service is documented using the same structure:

``` text
1. Input Domain Models
2. Resolve / Enrich authoritative entities
3. Validate existence
4. Validate relationships / ownership
5. Validate authorization or actor context
6. Validate use-case-specific business rules
7. Execute Domain behavior
8. Persist Domain Model
9. Register Operation
10. Register AuditLog
11. Return Domain Model
```

Not every step applies identically to every use case.

For example:

-   a consultation does not mutate the Domain Model and therefore does
    not normally generate an Operation/AuditLog;
-   expiration may be executed by a scheduled/application process and
    may not require an interactive `User`;
-   creation has a concrete enrichment/validation implementation
    described below.

The pattern defines the **shape of the orchestration**, not a claim that
every service currently implements every step.

------------------------------------------------------------------------

# 6. Create Shipment

## 6.1 Description

Creates a shipment request for a fulfilled order and establishes its
initial status.

The current implementation is the **reference implementation for
Shipment validation and enrichment orchestration**.

Its behavior is:

``` text
Shipment + User + Customer
          |
          v
Resolve authoritative User
          |
          v
Resolve Customer context
          |
          v
Resolve Order
          |
          v
Resolve origin Warehouse
          |
          v
Validate order and warehouse
          |
          v
Assign creation date
          |
          v
Determine approval requirement
          |
          v
Assign initial status
          |
          v
Persist Shipment
          |
          v
Register SHIPMENT_CREATION
          |
          v
Return saved Shipment
```

## 6.2 Input

The service receives:

``` text
Shipment shipment
User user
Customer customer
```

Conceptual Input Port:

``` java
interface CreateShipmentUseCase {

    Shipment execute(
        Shipment shipment,
        User user,
        Customer customer
    );
}
```

The `Shipment` contains the information required to create the request.

The `User` identifies the requesting actor (typically a `SELLER_OPERATOR`).

The `Customer` provides the seller context when it is not already
available from the requesting `User`.

## 6.3 Entity Enrichment

### 6.3.1 User enrichment

The service first resolves the authoritative stored `User`:

``` text
User input
   |
   v
UserRepositoryPort.findById(user)
   |
   +--> User not found -> EntityNotFoundException
   |
   v
Stored User
```

The stored Domain Model becomes the authoritative user context for the
remainder of the operation.

### 6.3.2 Customer resolution

The customer (seller) context is resolved using:

``` text
if customer != null
    use supplied customer
else if storedUser.customer != null
    use storedUser.customer
else
    reject request
```

If neither a `Customer` nor an associated `User.customer` is available:

``` text
DomainException
"Either customer or requesting user must be provided."
```

### 6.3.3 Order and Warehouse enrichment

The order and origin warehouse are resolved through:

``` text
OrderRepositoryPort.findByIdentifier(...)
WarehouseRepositoryPort.findByIdentifier(...)
```

Conceptually:

``` text
Shipment
   |
   +--> order reference
   |        |
   |        v
   |   OrderRepositoryPort
   |        |
   |        v
   |   order : Order
   |
   +--> originWarehouse reference
            |
            v
       WarehouseRepositoryPort
            |
            v
       originWarehouse : Warehouse
```

If either entity cannot be resolved, the service raises an
`EntityNotFoundException`.

## 6.4 Validations

The current implementation explicitly validates:

### Order status

``` text
order.orderStatus == FULFILLED
```

Otherwise:

``` text
DomainException
"Order is not fulfilled."
```

### Origin warehouse status

``` text
originWarehouse.warehouseStatus == ACTIVE
```

Otherwise:

``` text
DomainException
"Origin warehouse is not active."
```

### No duplicate shipment

``` text
no existing non-terminal Shipment already references this Order
```

Otherwise:

``` text
DomainException
"Order already has an active shipment."
```

### Customer (seller) ownership

The resolved origin warehouse owner must correspond to the resolved
customer:

``` text
originWarehouse.owner.identification
        ==
customer.identification
```

Otherwise:

``` text
DomainException
"The provided customer does not own the origin warehouse."
```

## 6.5 Important Current-Implementation Boundary

The current `CreateShipmentService` does **not** explicitly implement
the following validations that appeared in the previous conceptual
specification:

-   explicit `shippingFee > 0` validation;
-   carrier/capacity compatibility validation between the warehouse and
    the requested delivery priority;
-   a separate authorization-port call for the creating user.

They must therefore not be documented as currently executed validations
in this service.

They may be introduced later as explicit business rules, but doing so
would constitute a code change rather than a documentation change.

The current service always validates that the order is `FULFILLED` and
that the origin warehouse is `ACTIVE`.

## 6.6 Domain Mutation

The service assigns:

``` text
creationDate = LocalDateTime.now()
```

It then evaluates the configurable approval threshold:

``` text
shipment.shippingFee
      |
      v
BusinessConfigurationPort
      |
      v
getShipmentApprovalThreshold()
```

The decision is:

``` text
shipment.shippingFee > threshold
        |
        +--> YES --> WAITING_FOR_APPROVAL
        |
        +--> NO  --> APPROVED
```

The threshold must not be hardcoded.

## 6.7 Persistence

After validation and status assignment:

``` text
ShipmentRepositoryPort.save(shipment)
```

The returned saved Domain Model is used as the affected asset for the
subsequent operation.

## 6.8 Operation and Audit

Creation generates:

``` text
OperationType.SHIPMENT_CREATION
```

The operation contains conceptually:

``` text
operationType = SHIPMENT_CREATION
executionDate = now
performedBy = shipment.createdBy
affectedAsset = saved Shipment
```

The service delegates operation and audit registration to:

``` text
RegisterOperationAndAuditService
```

The current audit details include:

``` text
shippingFee
status
```

Where:

``` text
shippingFee = shipment.shippingFee
status = saved.shipmentStatus.code
```

## 6.9 Return

Returns:

``` text
saved Shipment
```

------------------------------------------------------------------------

# 7. Dispatch Shipment

## 7.1 Description

Dispatches a shipment that is already approved.

The intended orchestration is:

``` text
User + Shipment
      |
      v
Resolve authoritative context
      |
      v
Validate shipment state
      |
      v
Resolve order/warehouse
      |
      v
Validate order and warehouse
      |
      v
Set DISPATCHED
      |
      v
Persist shipment
      |
      v
Register SHIPMENT_DISPATCH
      |
      v
Return Shipment
```

## 7.2 Input

``` text
User
Shipment
```

Conceptual Input Port:

``` java
interface DispatchShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

## 7.3 Entity Enrichment

When authoritative context is required, the service resolves:

``` text
Order
Warehouse
```

through:

``` text
OrderRepositoryPort
WarehouseRepositoryPort
```

The service must validate against the authoritative statuses rather
than relying on stale input state.

If the concrete implementation also requires authoritative user
information, it must resolve the user through:

``` text
UserRepositoryPort
```

following the same enrichment principle established by
`CreateShipmentService`.

## 7.4 Validations

The service must validate, according to the current Shipment
specification:

-   shipment exists;
-   shipment status is `APPROVED`;
-   origin warehouse is `ACTIVE`;
-   order remains `FULFILLED`;
-   `APPROVED -> DISPATCHED` is a valid Domain transition;
-   dispatching actor is authorized according to the applicable
    authorization rules.

## 7.5 Domain Behavior

The shipment dispatch must produce:

``` text
shipment status = DISPATCHED
```

Unlike `ExecuteTransferService`, dispatch does not move monetary value
between two symmetric assets — it records the logistics handoff of an
already-fulfilled order. No balance-style mutation occurs on `Order` or
`Warehouse`.

## 7.6 Persistence

The service persists the affected Domain Model through:

``` text
ShipmentRepositoryPort
```

The exact atomicity/transaction mechanism belongs to the
application/infrastructure implementation and is outside this Domain
Services document.

## 7.7 Operation and Audit

Generate:

``` text
OperationType.SHIPMENT_DISPATCH
```

The audit information should provide traceability for:

``` text
order
originWarehouse
dispatchDate
```

The operation/audit registration should follow the same orchestration
principle used by `CreateShipmentService`.

## 7.8 Return

Returns the dispatched:

``` text
Shipment
```

------------------------------------------------------------------------

# 8. Submit Shipment for Approval

## 8.1 Description

Places a shipment in:

``` text
WAITING_FOR_APPROVAL
```

when the applicable business rules require approval.

In the current creation implementation, this status is assigned directly
during `Create Shipment` when the shipping fee exceeds the configured
threshold.

Therefore, this service must not be confused with the creation-time
approval decision.

## 8.2 Input

``` text
Shipment
```

If an explicit application use case is retained, its Input Port should
be:

``` java
interface SubmitShipmentForApprovalUseCase {

    Shipment execute(Shipment shipment);
}
```

## 8.3 Entity Enrichment

Resolve the authoritative `Shipment` when the supplied model is not
authoritative.

Use:

``` text
ShipmentRepositoryPort
```

when the use case is invoked independently from creation.

## 8.4 Validations

The service validates:

-   shipment exists;
-   current status allows transition to `WAITING_FOR_APPROVAL`;
-   shipment actually requires approval according to the applicable
    business rule.

If the approval decision is based on shipping fee, the threshold must
come from:

``` text
BusinessConfigurationPort
```

and must not be hardcoded.

## 8.5 Domain Behavior

Transition:

``` text
current status
      |
      v
WAITING_FOR_APPROVAL
```

The Domain Model remains responsible for validating that the state
transition is legal.

## 8.6 Persistence

``` text
ShipmentRepositoryPort
```

## 8.7 Operation and Audit

If this action is only the internal result of `Create Shipment`, it is
already represented by:

``` text
SHIPMENT_CREATION
```

If the application exposes it as an independent business action, a
dedicated audit/operation decision must be made consistently with the
available `OperationType` catalog.

The service must not invent a new `OperationType` that is not supported
by the Domain Model.

## 8.8 Return

``` text
Shipment
```

------------------------------------------------------------------------

# 9. Approve Shipment

## 9.1 Description

Approves a shipment waiting for authorization and moves it to:

``` text
APPROVED
```

## 9.2 Input

``` text
User
Shipment
```

Conceptual Input Port:

``` java
interface ApproveShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

## 9.3 Entity Enrichment

When the supplied entities are not authoritative, resolve:

``` text
User
Shipment
```

through their respective Output Ports.

The authoritative shipment state is required before applying the
approval transition.

## 9.4 Actor Validation

The approving user must be active and have the required authorization.

For the seller-shipment scenario defined in the current specification:

``` text
User.role == SELLER_SUPERVISOR
AND
User.status == ACTIVE
AND
Shipment.shipmentStatus == WAITING_FOR_APPROVAL
```

The authorization policy must remain within the appropriate
authorization abstraction when additional external information is
required.

## 9.5 Domain Validations

Validate:

-   shipment exists;
-   shipment status is `WAITING_FOR_APPROVAL`;
-   approving user is not the creator when the applicable
    segregation-of-duties rule applies;
-   `WAITING_FOR_APPROVAL -> APPROVED` is a valid transition.

## 9.6 Domain Behavior

Set:

``` text
shipment.shipmentStatus = APPROVED
shipment.approvalDate = current date/time
shipment.approvedBy = user
```

The exact state transition must be accepted by the Domain Model.

## 9.7 Persistence

``` text
ShipmentRepositoryPort
```

## 9.8 Operation and Audit

Generate:

``` text
OperationType.SHIPMENT_APPROVAL
```

Audit details should include:

``` text
previousStatus
newStatus
approvedBy
approvalDate
```

## 9.9 Return

``` text
Shipment
```

------------------------------------------------------------------------

# 10. Reject Shipment

## 10.1 Description

Rejects a shipment that is awaiting approval.

## 10.2 Input

``` text
User
Shipment
```

Conceptual Input Port:

``` java
interface RejectShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

## 10.3 Entity Enrichment

Resolve authoritative:

``` text
User
Shipment
```

when the supplied models do not contain authoritative state required by
the operation.

## 10.4 Actor Validation

The rejecting user must have the authorization required for shipment
rejection.

The authorization rules must be resolved consistently with the approval
process.

## 10.5 Domain Validations

The current specification defines:

-   shipment exists;
-   shipment status is `WAITING_FOR_APPROVAL` or another explicitly
    supported rejection state;
-   rejection is a valid transition from the current status.

The service must not introduce additional lifecycle states unless they
are supported by `ShipmentStatus`.

## 10.6 Domain Behavior

Transition:

``` text
WAITING_FOR_APPROVAL
        |
        v
REJECTED
```

The Domain Model is responsible for validating the transition.

## 10.7 Persistence

``` text
ShipmentRepositoryPort
```

## 10.8 Operation and Audit

Generate:

``` text
OperationType.SHIPMENT_REJECTION
```

Audit details should include:

``` text
previousStatus
newStatus
rejectedBy
rejectionDate
```

## 10.9 Return

``` text
Shipment
```

------------------------------------------------------------------------

# 11. Expire Shipment

## 11.1 Description

Marks a shipment as expired when it remains in:

``` text
WAITING_FOR_APPROVAL
```

beyond the permitted approval period.

## 11.2 Input

``` text
Shipment
```

Conceptual Input Port:

``` java
interface ExpireShipmentUseCase {

    Shipment execute(Shipment shipment);
}
```

## 11.3 Entity Enrichment

Resolve the authoritative shipment when required:

``` text
ShipmentRepositoryPort
```

If expiration requires configurable timing information, obtain it
through:

``` text
BusinessConfigurationPort
```

## 11.4 Validations

Validate:

-   shipment exists;
-   shipment status is `WAITING_FOR_APPROVAL`;
-   the approval window has elapsed;
-   `WAITING_FOR_APPROVAL -> EXPIRED` is a valid Domain transition.

## 11.5 Expiration Rule

The expiration decision must use the configured approval period when
that period is externally configurable.

Conceptually:

``` text
creation/approval reference time
             +
configured approval period
             |
             v
expiration instant
             |
             v
current time >= expiration instant
```

The configuration must not be hardcoded in the service.

## 11.6 Domain Behavior

Transition:

``` text
WAITING_FOR_APPROVAL
        |
        v
EXPIRED
```

Expiration does not dispatch the shipment and does not affect warehouse
stock, which was already decreased during `Fulfill Order`.

``` text
order status: unchanged
warehouse stock: unchanged
```

## 11.7 Persistence

``` text
ShipmentRepositoryPort
```

## 11.8 Operation and Audit

Generate:

``` text
OperationType.SHIPMENT_EXPIRATION
```

Audit details should include:

``` text
reason
expirationDate
```

## 11.9 Return

``` text
Shipment
```

------------------------------------------------------------------------

# 12. Track Shipment

## 12.1 Description

Retrieves a `Shipment` Domain Model, including its current status and
movement history.

Tracking is read-only and therefore does not mutate the shipment
lifecycle.

## 12.2 Input

``` text
User
Shipment
```

The requesting `User` is required when authorization must be evaluated.

Conceptual Input Port:

``` java
interface TrackShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

## 12.3 Entity Enrichment

Resolve the authoritative shipment through:

``` text
ShipmentRepositoryPort
```

If authorization requires additional authoritative context, resolve the
corresponding Domain Models through their Output Ports.

## 12.4 Validations

Validate:

-   shipment exists;
-   requesting user exists/has an applicable system identity when
    required;
-   access to the shipment is permitted (buyer of the order, seller who
    owns the origin warehouse, or authorized employee);
-   any required relationship between the user and the order/warehouse
    involved in the shipment is valid.

## 12.5 Domain Behavior

No Domain mutation is performed.

``` text
Shipment
   |
   v
ShipmentRepositoryPort
   |
   v
Shipment
```

## 12.6 Persistence

Read only:

``` text
ShipmentRepositoryPort
```

No update is performed.

## 12.7 Operation and Audit

Tracking does not normally generate a business `Operation` or
`AuditLog`, unless an explicit security/audit policy defines read
auditing as a business requirement.

## 12.8 Return

``` text
Shipment
```

The returned object must be a Domain Model and not a persistence entity.

------------------------------------------------------------------------

# 13. Input Ports

The Shipment subdomain exposes these use cases:

``` text
CreateShipmentUseCase
DispatchShipmentUseCase
SubmitShipmentForApprovalUseCase
ApproveShipmentUseCase
RejectShipmentUseCase
ExpireShipmentUseCase
TrackShipmentUseCase
```

Recommended contracts:

``` java
public interface CreateShipmentUseCase {

    Shipment execute(
        Shipment shipment,
        User user,
        Customer customer
    );
}
```

``` java
public interface DispatchShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

``` java
public interface SubmitShipmentForApprovalUseCase {

    Shipment execute(
        Shipment shipment
    );
}
```

``` java
public interface ApproveShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

``` java
public interface RejectShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

``` java
public interface ExpireShipmentUseCase {

    Shipment execute(
        Shipment shipment
    );
}
```

``` java
public interface TrackShipmentUseCase {

    Shipment execute(
        User user,
        Shipment shipment
    );
}
```

The exact method names may follow the project's naming convention, but
the Input Ports must preserve the Domain Model contract.

------------------------------------------------------------------------

# 14. Output Ports

## 14.1 ShipmentRepositoryPort

Responsible for persistence and retrieval of `Shipment` Domain Models.

Conceptual capabilities:

``` java
public interface ShipmentRepositoryPort {

    Shipment save(Shipment shipment);

    Optional<Shipment> findByIdentifier(Shipment shipment);

    List<Shipment> findByOrder(Order order);

    List<Shipment> findByOriginWarehouse(Warehouse warehouse);

    List<Shipment> findPendingApproval();

    List<Shipment> findExpiredCandidates();

    void update(Shipment shipment);
}
```

The exact methods must match the actual project interface. This document
must not require methods that the implementation does not expose.

## 14.2 OrderRepositoryPort

Provides authoritative `Order` Domain Models required by Shipment
services.

Used for:

``` text
- resolving the order being shipped;
- validating current order status (FULFILLED);
- confirming the buyer context for delivery.
```

## 14.3 WarehouseRepositoryPort

Provides authoritative `Warehouse` Domain Models required by Shipment
services.

Used for:

``` text
- resolving the origin warehouse;
- validating current warehouse status;
- validating warehouse ownership by the seller.
```

Example capability used by `CreateShipmentService`:

``` java
Optional<Warehouse> findByIdentifier(...);
```

## 14.4 UserRepositoryPort

Provides authoritative `User` Domain Models when the actor supplied to a
service must be resolved.

`CreateShipmentService` explicitly uses:

``` java
Optional<User> findById(User user);
```

The exact interface signature is governed by the actual project code.

## 14.5 BusinessConfigurationPort

Provides configurable business values required by Shipment services.

The current creation implementation uses:

``` java
BigDecimal getShipmentApprovalThreshold();
```

The threshold must remain external/configurable and must not be
hardcoded.

If approval expiration is also configurable, the port may expose the
corresponding capability, provided that it exists in the project
contract.

------------------------------------------------------------------------

# 15. Operation and Audit Integration

Shipment services that mutate the Shipment lifecycle must follow this
general pattern:

``` text
Validate
   |
   v
Enrich
   |
   v
Authorize
   |
   v
Execute Domain Behavior
   |
   v
Persist
   |
   v
Register Operation
   |
   v
Register AuditLog
   |
   v
Return Domain Model
```

The operation must reference the affected `Shipment` as a
`MarketplaceAsset`.

The available Shipment operation types include:

``` text
SHIPMENT_CREATION
SHIPMENT_APPROVAL
SHIPMENT_REJECTION
SHIPMENT_DISPATCH
SHIPMENT_EXPIRATION
```

The Operation/Audit service is responsible for the cross-cutting
persistence/orchestration of these records.

A Shipment service must not directly depend on MongoDB or another audit
database.

------------------------------------------------------------------------

# 16. Service Validation Matrix

| Service | Actor | Input | Enrichment | Main validations | Mutation | Persistence | Operation | Audit |
|---|---|---|---|---|---|---|---|---|
| Create Shipment | User + Customer context | Shipment + User + Customer | User, Customer, Order, origin Warehouse | Order FULFILLED, warehouse ACTIVE, no duplicate shipment, warehouse ownership | Set date/status | Shipment | Yes | Yes |
| Dispatch Shipment | User | User + Shipment | Shipment/order/warehouse as required | APPROVED, warehouse ACTIVE, order FULFILLED, authorization, valid transition | DISPATCHED | Shipment | Yes | Yes |
| Submit for Approval | Context-dependent | Shipment | Shipment/configuration as required | Valid transition, approval required | WAITING_FOR_APPROVAL | Shipment | Conditional | Conditional |
| Approve Shipment | User | User + Shipment | User + authoritative Shipment | Authorization, WAITING_FOR_APPROVAL, valid transition | APPROVED, approval data | Shipment | Yes | Yes |
| Reject Shipment | User | User + Shipment | User + authoritative Shipment | Authorization, valid rejection state/transition | REJECTED | Shipment | Yes | Yes |
| Expire Shipment | System/process | Shipment | Shipment + configuration as required | WAITING_FOR_APPROVAL, expiration elapsed, valid transition | EXPIRED | Shipment | Yes | Yes |
| Track Shipment | User | User + Shipment | Authoritative Shipment/context as required | Existence and access | None | Read only | No | No |

------------------------------------------------------------------------

# 17. Exception and Error Handling

Shipment services should express business/application failures through
Domain/Application exceptions.

The current creation implementation explicitly uses:

``` text
EntityNotFoundException
DomainException
```

Examples:

``` text
User not found
Order not found
Origin warehouse not found
Either customer or requesting user must be provided
Order is not fulfilled
Origin warehouse is not active
Order already has an active shipment
Customer does not own origin warehouse
```

Exception messages are implementation details and may evolve, but the
business meaning of the failure must remain explicit.

------------------------------------------------------------------------

# 18. Architectural Flow

## 18.1 General inbound flow

``` text
External Request
       |
       v
Input Adapter
       |
       v
Request Mapper
       |
       v
Domain Models
       |
       v
Input Port
       |
       v
Shipment Service
```

REST DTOs therefore stop at the adapter/application boundary.

## 18.2 Create Shipment flow

``` text
HTTP / Application Request
          |
          v
Request Mapper
          |
          v
Shipment + User + Customer
          |
          v
CreateShipmentUseCase
          |
          v
CreateShipmentService
          |
          +--> UserRepositoryPort
          |       |
          |       v
          |    User
          |
          +--> Customer context resolution
          |
          +--> OrderRepositoryPort
          |       |
          |       v
          |    Order
          |
          +--> WarehouseRepositoryPort
          |       |
          |       v
          |    Origin Warehouse
          |
          +--> Validate
          |
          +--> BusinessConfigurationPort
          |       |
          |       v
          |   Approval Threshold
          |
          +--> ShipmentRepositoryPort
          |       |
          |       v
          |   Saved Shipment
          |
          +--> RegisterOperationAndAuditService
          |       |
          |       +--> Operation
          |       |
          |       +--> AuditLog
          |
          v
       Shipment
```

------------------------------------------------------------------------

# 19. Consistency Rules

The Shipment Services document must remain synchronized with:

1.  `Shipment` Domain Model.
2.  `ShipmentStatus`.
3.  `OperationType`.
4.  `ShipmentRepositoryPort`.
5.  `OrderRepositoryPort`.
6.  `WarehouseRepositoryPort`.
7.  `UserRepositoryPort`.
8.  `BusinessConfigurationPort`.
9.  Authorization rules.
10. Operation and Audit services.
11. The actual service implementations.

## 19.1 Implementation is authoritative for implemented behavior

When documenting a service that already has an implementation, the
implementation is the source of truth for:

-   processing order;
-   entity enrichment;
-   entity resolution;
-   implemented validations;
-   state assignment;
-   repository calls;
-   operation creation;
-   audit details.

The documentation must not silently add behavior that the code does not
execute.

## 19.2 Specification is authoritative for intended behavior not yet implemented

For services without a concrete implementation available for
verification, this document describes the behavior established by the
current domain/service specification.

Such behavior must be verified against the implementation before being
treated as implemented functionality.

## 19.3 No artificial reconciliation

If code and specification differ:

``` text
Code behavior
     |
     v
Document as implemented behavior
```

and, separately:

``` text
Specification requirement
     |
     v
Document as intended/future behavior
```

The two must not be silently merged.

------------------------------------------------------------------------

# 20. Reference Pattern for New Shipment Services

Every new Shipment service should explicitly define:

``` text
Service Name
    |
    +--> Description
    +--> Input Domain Models
    +--> Entity Enrichment
    +--> Existence Validation
    +--> Related Entity Validation
    +--> Relationship / Ownership Validation
    +--> Actor / Authorization Validation
    +--> Domain Validation
    +--> Domain Behavior
    +--> Persistence
    +--> Operation
    +--> Audit
    +--> Exceptions
    +--> Return Value
```

The sections that do not apply to a read-only or system-triggered
operation must be explicitly marked as not applicable rather than
omitted.

This establishes a consistent documentation pattern across the Shipment
subdomain and aligns the services with the Hexagonal Architecture + DDD
principles of domain-first design, dependency inversion, technological
independence, high cohesion, and low coupling.
