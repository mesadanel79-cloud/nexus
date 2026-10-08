# Inventory and Warehouse Services

## 1. Introduction

This document defines the services belonging to the **Inventory and Warehouse Management** subdomain of the NexusMarket Marketplace Information Management System (NMIMS).

The Inventory and Warehouse Management subdomain is responsible for managing the lifecycle and business operations associated with warehouses and the stock they hold.

The main business capabilities are:

* Register Warehouse.
* Consult Warehouse.
* Consult Stock Level.
* Increase Stock.
* Decrease Stock.
* Block Warehouse.
* Unblock Warehouse.
* Close Warehouse.

Warehouses are represented by the `Warehouse` Domain Model and inherit from `MarketplaceAsset`.

```text
MarketplaceAsset
      |
      +-- Warehouse
```

A Warehouse is owned by a `Customer` acting in the Seller role.

```text
Customer (Seller)
      |
      | owns
      v
Warehouse
```

Unlike a Bank Account, a Warehouse does not hold a single balance. Instead, it holds stock levels for individual `Product` items from the Catalog subdomain. Every stock-affecting operation must therefore reference both the `Warehouse` and the `Product` whose quantity is being changed.

```text
Warehouse
    |
    | holds stock of
    v
Product
```

The ownership relationship must be explicitly validated whenever an operation requires access to a seller's Warehouse.

Every significant state-changing operation performed on a Warehouse or its stock must generate a business `Operation` and the corresponding `AuditLog` according to the audit rules of the system.

---

# 2. Domain Model Context

## 2.1 Warehouse

`Warehouse` is a Domain Model representing a marketplace asset.

Conceptually:

```text
MarketplaceAsset
      |
      +-- Warehouse
             |
             +-- warehouseType
             +-- owner : Customer
             +-- location
             +-- warehouseStatus
             +-- openingDate
```

Common attributes that belong to `MarketplaceAsset` must not be duplicated in `Warehouse`.

The exact inheritance structure must be defined by the Domain Model.

---

## 2.2 StockEntry

Because a Warehouse holds stock for multiple products, the relationship between a Warehouse and a Product's quantity must be represented explicitly, rather than collapsed into a single balance field.

Conceptually:

```text
StockEntry
 |
 +-- warehouse : Warehouse
 +-- product : Product
 +-- quantity : StockQuantity
```

`StockEntry` plays the same conceptual role for Inventory that `currentBalance` plays for a Bank Account: it is the authoritative record of "how much" is available, but scoped per product rather than a single account-wide figure.

---

## 2.3 Warehouse Owner

The owner of a Warehouse is represented by a `Customer` Domain Model acting in the Seller role.

Correct:

```java
Warehouse.owner : Customer
```

Incorrect:

```java
Warehouse.ownerId : String
```

The Domain Model must represent the business relationship rather than reducing it to a primitive identifier.

Persistence adapters are responsible for translating this relationship into the persistence representation.

---

# 3. User, Customer, and Warehouse Relationship

Inventory operations involve three different concepts:

```text
User
 |
 | performs operation
 v
Customer (Seller)
 |
 | owns
 v
Warehouse
```

These concepts must not be treated as interchangeable.

### User

The `User` represents the actor requesting or performing the operation.

### Customer

The `Customer` represents the seller associated with the operation.

### Warehouse

The `Warehouse` represents the marketplace asset affected by the operation.

Therefore:

```text
User != Customer
Customer != Warehouse
User != Warehouse
```

A Customer User may be associated with a Customer, while an Employee User may operate on behalf of the platform according to their role and permissions.

---

# 4. Service Design Principle

Each Inventory service represents one cohesive business operation.

A service is responsible for determining and validating **all business conditions necessary to execute that operation correctly**.

The architecture must not artificially fragment one operation into multiple small services.

For example, it is acceptable for:

```text
DecreaseStockService
```

to perform:

```text
User validation
Customer validation
Authorization validation
Ownership validation
Warehouse validation
Warehouse status validation
Product validation
Quantity validation
Stock sufficiency validation
Decrease execution
Persistence
Operation registration
Audit registration
```

through private methods or cohesive collaborators.

It is not necessary to create:

```text
ValidateUserService
ValidateCustomerService
ValidateOwnershipService
ValidateWarehouseStatusService
ValidateStockLevelService
ValidateQuantityService
```

merely to separate validations.

The important requirement is:

> The application service must prevent the business operation from executing unless every required business condition is satisfied.

---

# 5. Standard Application Service Pattern

State-changing Inventory services should generally follow this pattern:

```text
Input Domain Models / Value Objects
                |
                v
Retrieve authoritative state
                |
                v
Validate requesting User
                |
                v
Validate Customer
                |
                v
Validate User-Customer relationship
                |
                v
Validate Customer-Asset relationship
                |
                v
Validate Warehouse
                |
                v
Validate Warehouse Status
                |
                v
Validate operation-specific business rules
                |
                v
Execute Domain behavior
                |
                v
Persist through Output Port
                |
                v
Register Operation
                |
                v
Register Audit
                |
                v
Return result
```

Not every service requires every validation shown above.

Each service must apply the validations relevant to its operation.

---

# 6. Input Contract

Inventory application services must operate using Domain Models and Value Objects.

They must not expose REST DTOs or persistence entities as application contracts.

Primitive identifiers must not be used as the primary application-level representation when the domain already provides a corresponding Domain Model or Value Object.

Incorrect:

```java
decreaseStock(
    String warehouseId,
    String productId,
    String userId,
    int quantity
);
```

Preferred:

```java
decreaseStock(
    User requestingUser,
    Customer seller,
    Warehouse warehouse,
    Product product,
    StockQuantity quantity
);
```

The exact signature may vary according to the project's domain model, but the architectural principle is mandatory.

---

# 7. Value Objects

Stock quantities should preferably be represented through a `StockQuantity` Value Object.

Conceptually:

```text
StockQuantity
 |
 +-- units
 +-- unitOfMeasure
```

Instead of:

```java
int units
```

the application may use:

```java
StockQuantity units
```

This allows inventory invariants to be encapsulated in the Domain.

Examples of StockQuantity rules include:

```text
units != null
units > 0
unitOfMeasure != null
```

Unit-of-measure compatibility between the requested movement and the existing `StockEntry` must be validated whenever the domain supports multiple units.

---

# 8. Authoritative State

Domain Models received by a service represent the operation context, but they must not automatically be considered the authoritative persisted state.

For state-changing operations, the service must retrieve the current Warehouse and StockEntry state through the corresponding Output Ports whenever current state is required.

For example:

```text
Input Warehouse
       |
       v
WarehouseRepositoryPort
       |
       v
Authoritative Warehouse
       |
       v
Business Validation
       |
       v
Domain Operation
```

This is particularly important for:

* Current stock level.
* Warehouse status.
* Warehouse ownership.
* Asset existence.
* Other persisted business state.

The service must not trust a caller-provided stock level or warehouse status when the current persisted state is required.

---

# 9. External Information

Information already contained in a Domain Model must be validated from that Domain Model whenever possible.

For example:

```text
Warehouse
 |
 +-- warehouseType
 +-- owner
 +-- location
 +-- warehouseStatus
 +-- openingDate
```

No external call is required merely to validate information already available and authoritative in the Domain Model.

However, external information required for a business decision must be obtained through an Output Port.

Example:

```text
Inventory Service
        |
        v
CustomerRepositoryPort
        |
        v
Customer Adapter
        |
        v
External Persistence
```

Application services must never access the database directly.

---

# 10. User Validation

When an operation is performed by a User, the service must validate the User according to the business requirements of that operation.

Relevant validations may include:

* User exists.
* User is active.
* User is authorized.
* User has the required role or permission.
* Customer User is associated with the relevant Customer (Seller).
* Employee User is authorized to perform the operation.

The service must not assume:

```text
User exists
    =
User is authorized
```

Authentication and authorization are different concepts.

The User represents the actor, while the Customer represents the seller.

---

# 11. Customer Validation

When an Inventory operation involves a Customer acting as Seller, the service must validate the Customer according to the business requirements.

Relevant validations may include:

* Customer exists.
* Customer is active or otherwise eligible.
* Customer is allowed to perform the operation.
* Customer is the owner of the Warehouse when ownership is required.

The service must not assume:

```text
Customer exists
    =
Customer is eligible
```

When authoritative Customer information is required, it must be obtained through:

```text
CustomerRepositoryPort
```

---

# 12. Customer-Warehouse Ownership

Ownership validation is mandatory whenever the operation is restricted to the warehouse owner.

The service must explicitly validate:

```text
Customer (Seller)
    |
    | owns
    v
Warehouse
```

The service must not assume ownership merely because:

* A Customer was supplied as an input.
* A Warehouse was supplied as an input.
* Both objects were provided in the same request.
* Their identifiers were supplied by the caller.

The reference implementation represented by `DecreaseStockService` establishes this principle.

The service retrieves the authoritative Warehouse and Customer and validates that the Customer corresponds to the Warehouse owner.

The important business rule is:

> A seller must not be allowed to operate on a Warehouse that belongs to another seller.

---

# 13. Asset Access

Existence, ownership, and authorization are different validations.

The following is insufficient:

```text
Warehouse exists
+
Customer exists
=
Customer can operate on Warehouse
```

The service must establish the complete relationship:

```text
Requesting User
       |
       v
Customer
       |
       | owns
       v
Warehouse
```

For employee operations, authorization may instead follow:

```text
Requesting User
       |
       v
Employee Role / Permission
       |
       v
Authorized Customer
       |
       v
Warehouse
```

The applicable rule depends on the operation and the user's role.

---

# 14. Warehouse Status

The conceptual Warehouse statuses are:

```text
ACTIVE
BLOCKED
CLOSED
```

The Domain is responsible for defining and protecting valid status transitions.

The application service must validate that the requested operation is allowed for the current status.

Example:

```text
ACTIVE
  |
  +-- increase stock allowed
  +-- decrease stock allowed
  +-- block allowed

BLOCKED
  |
  +-- increase stock prohibited
  +-- decrease stock prohibited
  +-- unblock allowed

CLOSED
  |
  +-- increase stock prohibited
  +-- decrease stock prohibited
  +-- unblock prohibited
```

The exact business matrix must follow the domain requirements.

---

# 15. Domain Behavior

Warehouse and stock state must be changed through valid Domain behavior.

Preferred:

```java
warehouse.increaseStock(product, quantity);
warehouse.decreaseStock(product, quantity);
warehouse.block();
warehouse.unblock();
warehouse.close();
```

Avoid treating unrestricted setters as business behavior:

```java
stockEntry.setQuantity(...);
warehouse.setWarehouseStatus(...);
```

The Domain Model should protect its own invariants.

The application service coordinates the operation:

```text
Retrieve
   ->
Validate
   ->
Execute Domain Behavior
   ->
Persist
```

---

# 16. Operation and Audit

Inventory operations that modify business state must generate an `Operation`.

Examples include:

```text
WAREHOUSE_OPENING
STOCK_INCREASE
STOCK_DECREASE
WAREHOUSE_BLOCK
WAREHOUSE_UNBLOCK
WAREHOUSE_CLOSURE
```

The exact operation types must match the project's domain model.

An `Operation` should identify, as applicable:

```text
operationType
executionDate
performedBy
affectedAsset
operation-specific details
```

Relevant operations must also generate an `AuditLog`.

The audit record should provide sufficient information to determine:

```text
who performed the operation
what operation was performed
which asset was affected
when it occurred
relevant operation details
```

---

# 17. Transactional Consistency

State-changing operations must maintain consistency between:

```text
Warehouse / StockEntry
Operation
AuditLog
```

The following sequence is potentially unsafe:

```text
Update StockEntry
        |
        v
Register Operation
        |
        X
Register Audit fails
```

This could result in:

```text
StockEntry updated
Operation persisted
Audit missing
```

The implementation must use the appropriate transaction or consistency mechanism so that required state changes and traceability records are handled consistently.

This requirement does not imply creating additional business services.

---

# 18. Input Ports

The Inventory subdomain exposes the following Input Ports:

```text
RegisterWarehouseUseCase
ConsultWarehouseUseCase
ConsultStockLevelUseCase
IncreaseStockUseCase
DecreaseStockUseCase
BlockWarehouseUseCase
UnblockWarehouseUseCase
CloseWarehouseUseCase
```

Warehouse ownership validation is a business responsibility of the applicable service.

It does not need to be exposed as an independent business use case merely because ownership is a validation.

Therefore, the following should not automatically be treated as a separate application service:

```text
ValidateWarehouseOwnershipUseCase
```

The ownership rule is normally executed inside the service requiring it.

---

# 19. Output Ports

The Inventory subdomain may use the following Output Ports:

```text
WarehouseRepositoryPort
CustomerRepositoryPort
ProductRepositoryPort
OperationRepositoryPort
AuditRepositoryPort
```

These interfaces belong to the application/domain boundary.

Their implementations belong to adapters.

---

# 20. WarehouseRepositoryPort

`WarehouseRepositoryPort` is responsible for Warehouse and StockEntry persistence and retrieval.

Conceptually:

```java
public interface WarehouseRepositoryPort {

    Optional<Warehouse> findByIdentifier(Warehouse warehouse);

    Optional<StockEntry> findStockEntry(Warehouse warehouse, Product product);

    Warehouse save(Warehouse warehouse);

    Warehouse update(Warehouse warehouse);

    StockEntry updateStock(StockEntry stockEntry);
}
```

The exact method names and signatures may vary.

The port must operate using Domain Models.

The persistence implementation must remain hidden behind the port.

---

# 21. CustomerRepositoryPort

`CustomerRepositoryPort` provides authoritative Customer information when required by Inventory operations.

Conceptually:

```java
public interface CustomerRepositoryPort {

    Optional<Customer> findByIdentification(Customer customer);
}
```

The exact contract must follow the Customer Management subdomain specification.

The Inventory service may use this port to validate:

* Customer existence.
* Current Customer state.
* Customer eligibility.
* Customer relationship with the Warehouse.

---

# 22. ProductRepositoryPort

`ProductRepositoryPort` provides authoritative Product information from the Catalog subdomain when required by Inventory operations.

Conceptually:

```java
public interface ProductRepositoryPort {

    Optional<Product> findByIdentifier(Product product);
}
```

The Inventory service may use this port to validate:

* Product existence.
* Product status (e.g. not discontinued).
* Product ownership by the same Seller as the Warehouse.

---

# 23. OperationRepositoryPort

`OperationRepositoryPort` is responsible for Operation persistence.

Conceptually:

```text
Inventory Service
        |
        v
Operation
        |
        v
OperationRepositoryPort
        |
        v
Persistence Adapter
```

The service must never directly access the database.

---

# 24. AuditRepositoryPort

`AuditRepositoryPort` is responsible for AuditLog persistence.

Conceptually:

```text
Inventory Service
        |
        v
AuditLog
        |
        v
AuditRepositoryPort
        |
        v
Persistence Adapter
        |
        v
MongoDB
```

The Inventory service must not directly access MongoDB.

---

# 25. Register Warehouse

## 25.1 Purpose

Creates a new Warehouse associated with an eligible Customer acting as Seller.

The operation establishes the Warehouse as a marketplace asset and initializes its valid business state.

---

## 25.2 Input

The operation must receive the appropriate Domain Model representation.

Conceptually:

```text
Warehouse
```

The Warehouse should contain or reference:

* Warehouse type.
* Owner.
* Location.
* Warehouse status.
* Opening date.

The service must not require these values as unrelated primitive parameters.

---

## 25.3 Validations

The service must validate all rules required to create the warehouse, including when applicable:

* Requesting User.
* User status.
* User authorization.
* Customer existence.
* Customer status.
* Customer eligibility as Seller.
* Valid warehouse type.
* Valid location.
* Valid initial status.
* Valid opening date.
* Any warehouse-opening restrictions.

---

## 25.4 Customer Validation

The Warehouse owner is:

```text
Warehouse.owner : Customer
```

If the Domain Model already contains all required information, it should be validated directly.

If authoritative external Customer information is required:

```text
CustomerRepositoryPort
```

must be used.

---

## 25.5 Warehouse Creation

The warehouse must be initialized through valid Domain behavior.

The service must not manually manipulate persistence state.

Conceptually:

```text
Warehouse
      |
      v
Validate
      |
      v
Open/Create Domain State
      |
      v
WarehouseRepositoryPort
```

---

## 25.6 Operation and Audit

Registering a warehouse is a significant business operation.

The service must:

1. Persist the new Warehouse.
2. Register the corresponding `Operation`.
3. Register the required `AuditLog`.

These operations must maintain transactional consistency.

---

# 26. Consult Warehouse

## 26.1 Purpose

Retrieves an existing Warehouse to which the requesting actor is authorized to have access.

---

## 26.2 Input

The application-level contract must use the appropriate Domain Model.

Conceptually:

```text
Warehouse
```

The service must not expose:

```java
consultWarehouse(String warehouseId);
```

as the business use case contract.

---

## 26.3 Processing

The service should:

```text
1. Validate requesting User.
2. Retrieve authoritative Warehouse.
3. Validate Warehouse existence.
4. Resolve Customer when required.
5. Validate Customer.
6. Validate User authorization.
7. Validate Customer-Warehouse ownership or access relationship.
8. Return Warehouse.
```

---

## 26.4 Persistence

The warehouse must be retrieved through:

```text
WarehouseRepositoryPort
```

Persistence entities must never be returned outside the persistence adapter.

---

# 27. Consult Stock Level

## 27.1 Purpose

Returns the current stock quantity of a Product within an authorized Warehouse.

---

## 27.2 Processing

The service must:

```text
1. Validate requesting User.
2. Validate User status.
3. Validate authorization.
4. Retrieve authoritative Warehouse when required.
5. Validate Warehouse existence.
6. Resolve Customer when required.
7. Validate ownership/access.
8. Retrieve authoritative StockEntry for the given Product.
9. Return StockEntry.quantity.
```

---

## 27.3 Stock Authority

The authoritative stock level is:

```text
StockEntry.quantity
```

for the given `Warehouse`-`Product` pair.

The service must not trust a stock level supplied by the caller.

The service must not reconstruct the stock level from arbitrary persistence data unless explicitly required by the domain.

---

# 28. Increase Stock

## 28.1 Purpose

Increases the stock of a Product within an authorized Warehouse.

The operation increases:

```text
StockEntry.quantity
```

---

## 28.2 Input

The operation must use the appropriate Domain Models and Value Objects.

Conceptually:

```text
User
Customer
Warehouse
Product
StockQuantity
```

The exact method signature depends on the domain model.

---

## 28.3 Required Validations

The service must validate:

### User

* User exists.
* User is active.
* User is authorized.

### Customer

* Customer exists.
* Customer is active/eligible when required.
* Customer is authorized to operate on the warehouse when applicable.

### Ownership / Access

* Customer owns the Warehouse when customer ownership is required.
* Employee User has the required authorization when operating on behalf of the platform.

### Warehouse

* Warehouse exists.
* Warehouse is in a status that allows stock increases.

### Product

* Product exists in the Catalog.
* Product belongs to the same Seller as the Warehouse when applicable.

### Increase

* Quantity exists.
* Quantity is greater than zero.
* Unit of measure is compatible when applicable.
* Other increase-specific business rules are satisfied.

---

## 28.4 Warehouse Status

Under the default rules:

```text
ACTIVE  -> Stock increase allowed
BLOCKED -> Stock increase rejected
CLOSED  -> Stock increase rejected
```

---

## 28.5 Domain Behavior

The quantity must be changed through Domain behavior:

```java
warehouse.increaseStock(product, quantity);
```

The service must not directly calculate and assign the quantity.

---

## 28.6 Persistence

After successful Domain execution:

```text
WarehouseRepositoryPort.updateStock(stockEntry)
```

must persist the new state.

---

## 28.7 Operation and Audit

A successful increase must generate:

```text
Operation
    operationType = STOCK_INCREASE
    performedBy = requestingUser
    affectedAsset = warehouse
```

Relevant details should include:

```text
product
quantity
quantityBefore
quantityAfter
```

The corresponding audit record must also be generated.

---

# 29. Decrease Stock

## 29.1 Purpose

Decreases the stock of a Product within an authorized Warehouse.

The operation decreases:

```text
StockEntry.quantity
```

`DecreaseStockService` is the reference implementation pattern for state-changing Inventory services.

---

## 29.2 Input

The operation must use Domain Models and Value Objects.

Conceptually:

```text
User
Customer
Warehouse
Product
StockQuantity
```

The service must not expose a primitive application contract such as:

```java
decreaseStock(
    String warehouseId,
    String productId,
    int quantity
);
```

---

## 29.3 Reference Execution Pattern

The reference implementation follows this conceptual flow:

```text
Requesting User
      |
      v
Customer
      |
      v
Warehouse
      |
      v
Retrieve authoritative Warehouse
      |
      v
Validate Warehouse existence
      |
      v
Retrieve authoritative Customer
      |
      v
Validate Customer existence
      |
      v
Validate Customer owns Warehouse
      |
      v
Validate User
      |
      v
Validate authorization
      |
      v
Validate WarehouseStatus
      |
      v
Validate Product
      |
      v
Validate decrease quantity
      |
      v
Validate sufficient stock
      |
      v
Execute decrease
      |
      v
Persist StockEntry
      |
      v
Register Operation
      |
      v
Register Audit
```

---

## 29.4 Required Validations

The service must validate:

### Requesting User

* User is present.
* User exists/represents a valid actor.
* User is active.
* User has the required authorization.

### Customer

* Customer exists.
* Customer is valid.
* Customer is eligible when required.

### Ownership

The Customer involved in the operation must correspond to the Warehouse owner when the operation is seller-restricted.

### Warehouse

* Warehouse exists.
* Current state is authoritative.
* Warehouse status allows stock decreases.

### Decrease

* Quantity is present.
* Quantity is greater than zero.
* Unit of measure is compatible when applicable.
* Quantity does not exceed available stock.

---

## 29.5 Insufficient Stock

A decrease must satisfy:

```text
decreaseQuantity <= currentStock
```

Otherwise:

```text
InsufficientStockException
```

must be raised.

---

## 29.6 Warehouse Status

Under the default rules:

```text
ACTIVE  -> Stock decrease allowed
BLOCKED -> Stock decrease rejected
CLOSED  -> Stock decrease rejected
```

---

## 29.7 Domain Behavior

The preferred implementation is:

```java
warehouse.decreaseStock(product, quantity);
```

The Domain Model must guarantee that the Warehouse's stock remains valid after the operation.

---

## 29.8 Operation and Audit

A successful decrease must generate an Operation.

Conceptually:

```text
Operation
 |
 +-- operationType = STOCK_DECREASE
 +-- performedBy = requestingUser
 +-- affectedAsset = warehouse
 +-- executionDate
```

Relevant details should include:

```text
product
quantity
quantityBefore
quantityAfter
```

A corresponding AuditLog must be generated according to the audit rules.

---

# 30. Block Warehouse

## 30.1 Purpose

Changes an eligible Warehouse from its current state to:

```text
BLOCKED
```

---

## 30.2 Required Validations

The service must validate:

* Requesting User.
* User status.
* User authorization.
* Warehouse existence.
* Customer relationship when applicable.
* Customer ownership when applicable.
* Current warehouse status.
* Valid status transition.
* Additional blocking business rules.

---

## 30.3 Default Transition

```text
ACTIVE -> BLOCKED
```

The Domain must reject invalid transitions.

Examples:

```text
BLOCKED -> BLOCKED
CLOSED -> BLOCKED
```

unless explicitly permitted by the business rules.

---

## 30.4 Domain Behavior

Preferred:

```java
warehouse.block();
```

rather than:

```java
warehouse.setWarehouseStatus(BLOCKED);
```

---

## 30.5 Operation and Audit

A successful block operation must generate:

```text
Operation
    operationType = WAREHOUSE_BLOCK
    performedBy = requestingUser
    affectedAsset = warehouse
```

and the corresponding audit information.

---

# 31. Unblock Warehouse

## 31.1 Purpose

Changes an eligible blocked Warehouse back to:

```text
ACTIVE
```

---

## 31.2 Required Validations

The service must validate:

* Requesting User.
* User status.
* User authorization.
* Warehouse existence.
* Customer relationship when applicable.
* Customer ownership when applicable.
* Current warehouse status.
* Valid status transition.
* Additional unblocking business rules.

---

## 31.3 Default Transition

```text
BLOCKED -> ACTIVE
```

Invalid examples include:

```text
ACTIVE -> ACTIVE
CLOSED -> ACTIVE
```

unless explicitly permitted by the Domain.

---

## 31.4 Domain Behavior

Preferred:

```java
warehouse.unblock();
```

---

## 31.5 Operation and Audit

A successful unblock operation must generate an Operation and the corresponding AuditLog according to the audit rules.

---

# 32. Close Warehouse

## 32.1 Purpose

Closes a Warehouse by transitioning it to:

```text
CLOSED
```

---

## 32.2 Required Validations

The service must validate:

### User

* User exists.
* User is active.
* User is authorized.

### Customer

* Customer exists.
* Customer is valid.
* Customer owns the warehouse when ownership is required.

### Warehouse

* Warehouse exists.
* Current status allows closure.
* The transition to `CLOSED` is valid.

### Closure Rules

The service must validate all applicable closure conditions, such as:

* Remaining stock requirements.
* Pending fulfillments referencing the warehouse.
* Pending operations.
* Asset restrictions.
* Customer eligibility.
* Other domain-specific closure conditions.

---

## 32.3 Remaining Stock

If the business rule requires all stock entries to be empty:

```text
sum(StockEntry.quantity for all products) == 0
```

must be satisfied before closure.

The service must not assume that the warehouse can be closed simply because the user requested it.

---

## 32.4 Domain Behavior

Preferred:

```java
warehouse.close();
```

The Domain Model must prevent invalid transitions.

---

## 32.5 Operation and Audit

A successful closure must generate:

```text
Operation
    operationType = WAREHOUSE_CLOSURE
    performedBy = requestingUser
    affectedAsset = warehouse
```

and the corresponding AuditLog.

---

# 33. Warehouse Ownership Validation

Ownership validation is not merely an identifier comparison.

The business rule is:

```text
Warehouse.owner == Customer involved in operation
```

The service must use authoritative Domain information to establish this relationship.

A typical validation flow is:

```text
Input Customer
      |
      v
CustomerRepositoryPort
      |
      v
Authoritative Customer
      |
      v
Authoritative Warehouse
      |
      v
Validate ownership relationship
```

The reference `DecreaseStockService` demonstrates this concept by retrieving the authoritative Customer and Warehouse before validating the ownership relationship.

---

# 34. Authorization

Authorization is operation-specific.

A Customer User (Seller) should generally be able to operate only on warehouses belonging to the Customer associated with that User.

An employee User may operate according to their role and permissions.

The service must therefore distinguish:

```text
Identity
Authorization
Ownership
Asset State
```

These are separate business conditions.

The service may perform all these validations within one cohesive operation.

---

# 35. Exception Model

The Inventory subdomain may use exceptions such as:

```text
WarehouseNotFoundException
InvalidWarehouseException
InvalidWarehouseStatusException
InvalidWarehouseStatusTransitionException
InvalidWarehouseOwnershipException
InsufficientStockException
InvalidStockIncreaseException
InvalidStockDecreaseException
WarehouseAlreadyClosedException
WarehouseAlreadyBlockedException
ProductNotFoundException
SellerNotEligibleException
UnauthorizedWarehouseOperationException
InvalidUserStatusException
```

Exceptions must communicate business failures clearly.

The implementation must not intentionally rely on technical exceptions such as:

```text
NullPointerException
SQL Exception
JPA Exception
Mongo Exception
```

to represent domain rule violations.

---

# 36. Persistence Boundary

The required architecture is:

```text
Application Service
        |
        v
Output Port
        |
        v
Persistence Adapter
        |
        v
Database
```

For Warehouses:

```text
InventoryService
        |
        v
WarehouseRepositoryPort
        |
        v
WarehousePersistenceAdapter
        |
        v
Database
```

For Customers:

```text
InventoryService
        |
        v
CustomerRepositoryPort
        |
        v
CustomerPersistenceAdapter
        |
        v
Database
```

For Products:

```text
InventoryService
        |
        v
ProductRepositoryPort
        |
        v
ProductPersistenceAdapter
        |
        v
Database
```

For Operations:

```text
InventoryService
        |
        v
OperationRepositoryPort
        |
        v
OperationPersistenceAdapter
```

For Audit:

```text
InventoryService
        |
        v
AuditRepositoryPort
        |
        v
AuditPersistenceAdapter
        |
        v
MongoDB
```

---

# 37. Persistence Entities

Persistence entities must never leave their persistence adapter.

Incorrect:

```text
Controller
    |
    v
Persistence Entity
    |
    v
Application Service
```

Correct:

```text
Controller
    |
    v
Request Mapper
    |
    v
Domain Model
    |
    v
Application Service
    |
    v
Output Port
    |
    v
Persistence Adapter
    |
    v
Persistence Entity
```

The adapter is responsible for mapping:

```text
Domain Model <-> Persistence Entity
```

---

# 38. Controller Responsibilities

Controllers are responsible only for transport concerns.

They may:

1. Receive external requests.
2. Perform transport-level validation.
3. Map request data to Domain Models and Value Objects.
4. Invoke an Input Port.
5. Map the Domain result to the external response.

Controllers must not implement:

* Warehouse ownership validation.
* Customer eligibility rules.
* Stock decrease rules.
* Stock increase rules.
* Quantity mutation.
* Warehouse status transitions.
* Authorization business decisions.
* Operation registration.
* Audit registration.
* Persistence.

---

# 39. Increase Stock Processing Flow

```text
Requesting User
       |
       v
Customer
       |
       v
Warehouse
       |
       v
Retrieve authoritative state
       |
       v
Validate User
       |
       v
Validate Customer
       |
       v
Validate ownership/access
       |
       v
Validate WarehouseStatus
       |
       v
Validate Product
       |
       v
Validate StockQuantity
       |
       v
Execute increase
       |
       v
Persist StockEntry
       |
       v
Register Operation
       |
       v
Register Audit
```

---

# 40. Decrease Stock Processing Flow

```text
Requesting User
       |
       v
Customer
       |
       v
Warehouse
       |
       v
Retrieve authoritative Warehouse
       |
       v
Validate Warehouse existence
       |
       v
Retrieve authoritative Customer
       |
       v
Validate Customer existence
       |
       v
Validate Customer ownership
       |
       v
Validate User
       |
       v
Validate authorization
       |
       v
Validate WarehouseStatus
       |
       v
Validate Product
       |
       v
Validate decrease quantity
       |
       v
Validate sufficient stock
       |
       v
Execute decrease
       |
       v
Persist StockEntry
       |
       v
Register Operation
       |
       v
Register Audit
```

This is the reference flow for Inventory state-changing services.

---

# 41. Blocking Processing Flow

```text
Requesting User
       |
       v
Warehouse
       |
       v
Retrieve authoritative state
       |
       v
Validate User
       |
       v
Validate authorization
       |
       v
Validate ownership/access
       |
       v
Validate current status
       |
       v
Validate status transition
       |
       v
Execute block()
       |
       v
Persist Warehouse
       |
       v
Register Operation
       |
       v
Register Audit
```

---

# 42. Consultation Flow

```text
Requesting User
       |
       v
Validate User
       |
       v
Validate Authorization
       |
       v
Retrieve Warehouse
       |
       v
Validate existence
       |
       v
Validate Customer relationship
       |
       v
Validate Asset Access
       |
       v
Return Domain Model
```

Consultation operations do not normally modify Warehouse or stock state.

However, if the business audit policy requires auditing sensitive consultations, an AuditLog must be generated.

---

# 43. Validation Matrix

The following matrix defines the minimum validation dimensions.

| Validation             |             Register |          Consult |    Stock Level |  Increase | Decrease |          Block |        Unblock |    Close |
| ----------------------- | --------------------: | ---------------: | --------------: | --------: | -------: | --------------: | --------------: | -------: |
| User                    |                   Yes |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| User status             |                   Yes |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| Authorization           |                   Yes |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| Customer                |                   Yes |    When required |   When required |       Yes |      Yes |  When required |  When required |      Yes |
| Customer status         |                   Yes |    When required |   When required |       Yes |      Yes |  When required |  When required |      Yes |
| Ownership/access        |                   N/A |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| Warehouse existence     |                   N/A |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| Warehouse status        |         Initial state |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| Product existence       |                   N/A |               No |             Yes |       Yes |      Yes |              No |              No |       No |
| Operation rules         |                   Yes |              Yes |             Yes |       Yes |      Yes |             Yes |             Yes |      Yes |
| Operation registration  |                   Yes |       Usually no |      Usually no |       Yes |      Yes |             Yes |             Yes |      Yes |
| Audit                   |    Required by policy | Policy dependent | Policy dependent |  Required | Required |        Required |        Required | Required |

`When required` means the validation depends on the business context of the operation.

---

# 44. Business Rules Summary

## BR-001 — Warehouse is a Domain Model

`Warehouse` must belong to the Domain Model.

---

## BR-002 — Warehouse Inherits MarketplaceAsset

```text
MarketplaceAsset
      |
      +-- Warehouse
```

---

## BR-003 — Owner is Customer

```text
Warehouse.owner : Customer
```

Ownership must not be modeled only as a primitive identifier.

---

## BR-004 — User and Customer are Different Concepts

A User represents the actor.

A Customer represents the seller.

---

## BR-005 — Asset Ownership Must Be Verified

A Customer must not be allowed to operate on a Warehouse belonging to another Customer.

---

## BR-006 — User Authorization Must Be Verified

The existence of a User does not imply authorization.

---

## BR-007 — Customer Eligibility Must Be Verified

The existence of a Customer does not imply eligibility.

---

## BR-008 — Asset State Must Be Authoritative

For state-changing operations, the service must use the current authoritative Warehouse and StockEntry state.

---

## BR-009 — Blocked Warehouses Have Operational Restrictions

Operations prohibited by `BLOCKED` status must be rejected.

---

## BR-010 — Closed Warehouses Have Operational Restrictions

Operations prohibited by `CLOSED` status must be rejected.

---

## BR-011 — Invalid Status Transitions Are Rejected

The Domain must prevent invalid transitions.

---

## BR-012 — Stock Increases Must Be Valid

Stock increases must satisfy:

```text
quantity > 0
```

and all other applicable inventory rules.

---

## BR-013 — Stock Decreases Must Be Valid

Stock decreases must satisfy:

```text
quantity > 0
quantity <= currentStock
```

and all other applicable inventory rules.

---

## BR-014 — Stock Must Be Changed Through Domain Behavior

The application service must use valid Domain behavior rather than unrestricted persistence-style setters.

---

## BR-015 — Significant Operations Generate Operation Records

State-changing Inventory operations must generate an `Operation`.

---

## BR-016 — Relevant Operations Generate Audit Records

Operations subject to auditing must generate `AuditLog`.

---

## BR-017 — External Information Uses Output Ports

The application service must never directly access databases or infrastructure.

---

## BR-018 — Persistence Entities Stay in Adapters

Persistence entities must never cross into the application/domain layer.

---

## BR-019 — Controllers Do Not Implement Business Rules

Business rules belong to the Domain/Application layers.

---

## BR-020 — Services Must Be Cohesive

One application service may perform all validations required for its business operation.

The architecture must not be fragmented merely for the sake of creating smaller services.

---

# 45. Anti-Patterns

## 45.1 Direct Database Access

Invalid:

```java
EntityManager
JpaRepository
JdbcTemplate
MongoRepository
Connection
SQL
```

inside application services.

---

## 45.2 Primitive Application Contracts

Avoid:

```java
decreaseStock(
    String warehouseId,
    String productId,
    int quantity
);
```

when Domain Models and Value Objects represent those concepts.

---

## 45.3 Trusting Caller-Supplied State

Do not assume the caller-provided Warehouse or StockEntry contains the current:

* Stock quantity.
* Status.
* Owner.
* Other persisted business state.

---

## 45.4 Missing Ownership Validation

Invalid:

```text
Customer exists
+
Warehouse exists
=
Customer may operate on Warehouse
```

Ownership must be explicitly established.

---

## 45.5 Confusing User and Customer

Invalid:

```text
User == Customer
```

A User may be associated with a Customer, but they are different domain concepts.

---

## 45.6 Business Rules in Controllers

Invalid:

```text
Controller
 |
 +-- check stock
 +-- check ownership
 +-- change status
 +-- modify quantity
```

---

## 45.7 Direct Quantity Mutation

Avoid:

```java
stockEntry.setQuantity(
    stockEntry.getQuantity().subtract(quantity)
);
```

as the primary domain behavior.

Prefer:

```java
warehouse.decreaseStock(product, quantity);
```

---

## 45.8 Excessive Service Fragmentation

Avoid:

```text
DecreaseStockService
       |
       +-- ValidateUserService
       +-- ValidateCustomerService
       +-- ValidateOwnershipService
       +-- ValidateWarehouseStatusService
       +-- ValidateStockService
       +-- ValidateQuantityService
```

when these validations are only parts of the same decrease business operation.

Prefer:

```text
DecreaseStockService
       |
       +-- validateUser()
       +-- validateCustomer()
       +-- validateOwnership()
       +-- validateWarehouse()
       +-- validateDecrease()
       +-- executeDecrease()
```

This provides cohesion without unnecessary architectural fragmentation.

---

# 46. Testing Requirements

The Inventory subdomain must be testable without infrastructure.

Application services must be testable using mocks, fakes, or stubs for Output Ports.

Tests must not require:

```text
MySQL
MongoDB
REST
JPA
Hibernate
real persistence adapters
```

---

## 46.1 User Tests

Test:

* Valid User.
* Missing User.
* Inactive User.
* Unauthorized User.
* Authorized Customer User (Seller).
* Authorized Employee User.

---

## 46.2 Customer Tests

Test:

* Customer exists.
* Customer does not exist.
* Customer inactive.
* Customer not eligible.
* Customer does not own the Warehouse.

---

## 46.3 Warehouse Tests

Test:

* Warehouse exists.
* Warehouse does not exist.
* ACTIVE warehouse.
* BLOCKED warehouse.
* CLOSED warehouse.
* Invalid status transition.

---

## 46.4 Increase Stock Tests

Test:

* Valid increase.
* Zero quantity.
* Negative quantity.
* Invalid unit of measure.
* Increase into BLOCKED warehouse.
* Increase into CLOSED warehouse.
* Unauthorized increase.
* Increase in another seller's warehouse.
* Product not found.

---

## 46.5 Decrease Stock Tests

Test:

* Valid decrease.
* Zero quantity.
* Negative quantity.
* Invalid unit of measure.
* Insufficient stock.
* Decrease from BLOCKED warehouse.
* Decrease from CLOSED warehouse.
* Unauthorized decrease.
* Decrease from another seller's warehouse.
* Product not found.

---

## 46.6 Blocking Tests

Test:

* ACTIVE -> BLOCKED.
* BLOCKED -> BLOCKED rejected.
* CLOSED -> BLOCKED rejected.
* Unauthorized blocking.

---

## 46.7 Unblocking Tests

Test:

* BLOCKED -> ACTIVE.
* ACTIVE -> ACTIVE rejected.
* CLOSED -> ACTIVE rejected.
* Unauthorized unblocking.

---

## 46.8 Closing Tests

Test:

* Valid closure.
* Already closed warehouse.
* Invalid status.
* Non-zero remaining stock when zero stock is required.
* Pending fulfillments referencing the warehouse.
* Unauthorized closure.

---

## 46.9 Operation and Audit Tests

For every applicable state-changing operation, verify:

```text
Operation generated
Correct operation type
Correct requesting User
Correct affected Warehouse
Correct execution date
Correct operation details
AuditLog generated
```

---

# 47. Definition of Done

An Inventory service is complete only when:

* It represents a coherent business operation.
* It receives appropriate Domain Models and Value Objects.
* It does not expose REST DTOs as application contracts.
* It does not receive persistence entities.
* It does not directly depend on persistence technology.
* It retrieves authoritative state when required.
* It validates the requesting User.
* It validates User status.
* It validates authorization.
* It validates the Customer when applicable.
* It validates Customer status and eligibility when applicable.
* It validates Customer-Warehouse ownership when applicable.
* It validates Warehouse existence.
* It validates Warehouse status.
* It validates Product existence when applicable.
* It validates all operation-specific business rules.
* It executes valid Domain behavior.
* It persists through Output Ports.
* It generates the required Operation.
* It generates the required AuditLog.
* It maintains transactional consistency.
* It protects Domain invariants.
* It can be tested without infrastructure.

---

# 48. Final Service Catalog

The Inventory and Warehouse Management subdomain contains the following application services:

```text
Inventory and Warehouse Management
|
+-- Register Warehouse
|
+-- Consult Warehouse
|
+-- Consult Stock Level
|
+-- Increase Stock
|
+-- Decrease Stock
|
+-- Block Warehouse
|
+-- Unblock Warehouse
|
+-- Close Warehouse
```

Ownership validation, User validation, Customer validation, authorization validation, status validation, and operation-specific validation are **business responsibilities of the applicable service**.

They do not need to become independent application services simply because they are logically identifiable validations.

---

# 49. Reference Architecture

The final architecture for a state-changing Inventory operation should follow:

```text
                         INPUT
                           |
                           v
                  +------------------+
                  |   Input Adapter  |
                  |    Controller    |
                  +--------+---------+
                           |
                           | Domain Models / Value Objects
                           v
                  +------------------+
                  |   Input Port     |
                  +--------+---------+
                           |
                           v
                  +------------------+
                  | Application      |
                  |     Service      |
                  |                  |
                  | - User validation|
                  | - Customer       |
                  | - Authorization  |
                  | - Ownership      |
                  | - Asset state    |
                  | - Business rules |
                  | - Domain action  |
                  +--------+---------+
                           |
             +-------------+-------------+-------------+
             |             |             |             |
             v             v             v             v
       Warehouse       Customer       Product     Operation/Audit
      Repository      Repository     Repository      Registration
             |             |             |             |
             v             v             v             v
          Adapter       Adapter       Adapter       Output Ports
             |             |             |             |
             +-------------+-------------+-------------+
                           |
                           v
                     Infrastructure
```

The essential architectural boundary is:

```text
Domain/Application
        |
        | Output Ports
        v
Infrastructure Adapters
```

Never:

```text
Domain/Application
        |
        v
Database
```

---

# 50. Final Design Rule

The Inventory and Warehouse Management subdomain must follow this principle:

> **Each application service is a cohesive business operation responsible for determining whether the operation can be executed, validating all required User, Customer, ownership, authorization, asset-state, and operation-specific business conditions, executing valid Domain behavior, persisting through Output Ports, and generating the required Operation and Audit records.**

The design should therefore avoid both insufficient responsibility and excessive fragmentation.

### Insufficient

```text
find Warehouse
      |
      v
change quantity
```

### Excessively fragmented

```text
ValidateUserService
        |
ValidateCustomerService
        |
ValidateOwnershipService
        |
ValidateWarehouseService
        |
ValidateStockService
        |
ExecuteOperationService
```

### Preferred

```text
DecreaseStockService
|
+-- Retrieve authoritative state
+-- Validate User
+-- Validate Customer
+-- Validate authorization
+-- Validate ownership
+-- Validate Warehouse
+-- Validate WarehouseStatus
+-- Validate decrease rules
+-- Execute Domain behavior
+-- Persist StockEntry
+-- Register Operation
+-- Register Audit
```

This pattern is the reference for the remaining Inventory services.

The objective is not to minimize the number of methods or services.

The objective is to guarantee **business correctness while preserving DDD boundaries, Hexagonal Architecture, domain independence, cohesion, testability, and infrastructure isolation**.
