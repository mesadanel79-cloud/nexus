# Domain Model — NexusMarket

## Introduction

The Domain Model represents the core business entities of **NexusMarket**, the digital platform that acts as a commercial intermediary between buyers and sellers. These entities encapsulate the business rules, data, relationships, and lifecycle concepts described in the system's functional specification.

The model follows Object-Oriented Design and Domain-Driven Design (DDD) principles. Inheritance is used to represent genuine domain specialization, while explicit object relationships are preferred over generic identifier fields.

The model distinguishes between:

- **Person**, which represents identifiable participants of the system and their role.
- **Buyer**, a person who purchases products published in the catalog.
- **Seller**, a person or entity responsible for marketing products.
- **Internal Staff (InternalStaff)**, users who administer, supervise, or operate the platform (Administrator, Logistics Operator, Supervisor).
- **Warehouse**, physical locations where inventory is managed.
- **Product**, physical or digital goods offered in the catalog.
- **Inventory**, available stock for sale, distributed by warehouse.
- **Inventory Movements (InventoryMovement)**, records of every change in stock.
- **Shopping Cart (ShoppingCart)** and **Order**, which represent the central commercial cycle of the system.
- **Invoice**, **Shipment**, **Return**, and **Refund**, which represent the processes that follow order confirmation.

An order may generate multiple events throughout its lifecycle (payment, dispatch, delivery). Every inventory record must be mandatorily linked to a product and a specific warehouse.

## Domain Class Hierarchy

```
Person (Abstract)
├── Buyer
├── Seller
└── InternalStaff (Abstract)
     ├── Administrator
     ├── LogisticsOperator
     └── Supervisor

Warehouse (Abstract)
├── MarketplaceWarehouse
└── SellerWarehouse

Product (Abstract)
├── PhysicalProduct
└── DigitalProduct

Inventory
InventoryMovement

ShoppingCart
Order
Invoice
Shipment
Return
Refund
```

## Domain Relationships

```
Person
   │
   ├── Buyer
   │      ├── owns ───────────────> ShoppingCart
   │      └── places ─────────────> Order
   │
   ├── Seller
   │      ├── owns ───────────────> SellerWarehouse
   │      └── publishes ──────────> Product
   │
   └── InternalStaff
          ├── Administrator
          │      └── registers ───> Seller
          ├── LogisticsOperator
          │      └── manages ─────> Shipment
          └── Supervisor
                 └── consults ────> Order / Return / Refund

Product
   │
   └── stored in ──────────────────> Inventory ──> Warehouse

Inventory
   │
   └── tracked by ─────────────────> InventoryMovement

ShoppingCart
   │
   └── converted into ─────────────> Order

Order
   │
   ├── contains ────────────────────> OrderItem ──> Product
   ├── generates ───────────────────> Invoice
   ├── generates ───────────────────> Shipment (physical products only)
   └── may generate ────────────────> Return ──> Refund
```

## Entities

### Person (Abstract)

**Description**

Represents any identifiable participant within NexusMarket.

This abstract class centralizes the identity and contact information shared by buyers, sellers, and internal staff.

The role assigned to a person represents what that person means within the system and determines their responsibilities and permissions.

This class cannot be instantiated directly.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| identifier | String | Unique identifier of the person. |
| fullName | String | Official name of the person. |
| email | String | Primary access and communication channel. Unique across the platform. |
| role | SystemRole | Defines the participant's responsibilities and permissions. Unique per person. |
| status | UserStatus | Operational condition (Active, Blocked, Inactive). |

**Relationships**

- A Person may be specialized as a Buyer, a Seller, or InternalStaff.
- The role belongs to Person because it represents the person's meaning and responsibilities within the system.

**Business Rule**

- Each user shall have a single role within the system (RG-02).
- The identification document and email address must be unique across the platform.

---

### Buyer

**Description**

Represents a buyer who purchases products published in the catalog.

A buyer never manages information belonging to other buyers or to inventory.

**Inherits From**

Person

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| mainAddress | String | Usual location for deliveries. |
| additionalAddresses | List\<String\> | Secondary delivery locations. Empty by default. |
| commercialStatus | CommercialStatus | Condition of the buyer for making purchases. |
| cart | ShoppingCart | Buyer's active shopping cart. |
| orders | List\<Order\> | Orders placed by the buyer. Empty by default. |

**Relationships**

- A Buyer owns one ShoppingCart.
- A Buyer places zero or more Order instances, held in orders.

---

### Seller

**Description**

Represents a seller responsible for marketing products within the platform.

Sellers cannot self-register; they are onboarded by an Administrator.

**Inherits From**

Person

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| warehouses | List\<SellerWarehouse\> | Warehouses owned by the seller. Empty by default. |
| products | List\<Product\> | Products published by the seller. Empty by default. |
| sellerStatus | SellerStatus | Operational condition of the seller within the marketplace. |
| onboardingDate | LocalDate | Date on which the seller was registered. |
| registeredBy | Administrator | Administrator who onboarded the seller. |

**Relationships**

- A Seller owns zero or more SellerWarehouse instances.
- A Seller publishes zero or more Product instances.
- A Seller is onboarded by an Administrator.

**Business Rule**

- Sellers cannot self-register; they are onboarded by the Administrator.

---

### InternalStaff (Abstract)

**Description**

Represents NexusMarket's internal staff responsible for administering, operating, and supervising the platform.

This class cannot be instantiated directly.

**Inherits From**

Person

**Relationships**

- InternalStaff is specialized into Administrator, LogisticsOperator, and Supervisor.

---

### Administrator

**Description**

Responsible for registering sellers and administering marketplace warehouses.

**Inherits From**

InternalStaff

**Relationships**

- An Administrator registers zero or more Seller instances.
- An Administrator administers MarketplaceWarehouse.

---

### LogisticsOperator

**Description**

Responsible for the physical operation of warehouses and dispatch: product registration, inventory management, and shipment handling.

**Inherits From**

InternalStaff

**Relationships**

- A LogisticsOperator registers Product.
- A LogisticsOperator manages Inventory and handles Shipment.

---

### Supervisor

**Description**

Read-only, operational monitoring profile, without direct administration permissions.

**Inherits From**

InternalStaff

**Relationships**

- A Supervisor consults Order, Return, and Refund for monitoring purposes.

---

### Warehouse (Abstract)

**Description**

Represents a physical storage space where inventory is managed.

Marketplace warehouses and Seller warehouses are distinguished.

This class cannot be instantiated directly.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| identifier | String | Unique identifier of the warehouse. |
| name | String | Descriptive name of the warehouse. |
| location | String | Physical address of the warehouse. |
| status | WarehouseStatus | Operational condition of the warehouse. |

**Relationships**

- A Warehouse stores zero or more Inventory instances.

---

### MarketplaceWarehouse

**Description**

Warehouse directly administered by the marketplace organization.

**Inherits From**

Warehouse

**Relationships**

- A MarketplaceWarehouse is administered by one or more Administrator instances.

---

### SellerWarehouse

**Description**

Warehouse owned by a seller, registered either at onboarding time or afterward.

**Inherits From**

Warehouse

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| owner | Seller | Seller who owns the warehouse. |

**Relationships**

- A SellerWarehouse belongs to one Seller.

---

### Product (Abstract)

**Description**

Represents a good offered in the NexusMarket catalog. The catalog distinguishes between physical products, which require inventory and dispatch, and digital products, which are delivered immediately upon payment.

This class cannot be instantiated directly.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| identifier | String | Unique identifier of the product. |
| name | String | Commercial name of the product. |
| description | String | Functional description of the product. |
| price | BigDecimal | Unit sale price. |
| variants | List\<ProductVariant\> | Differences in color, size, model, etc. Empty by default. |
| status | ProductStatus | Published, Suspended, or Discontinued. |
| seller | Seller | Seller who publishes the product. |

**Relationships**

- A Product is published by one Seller.
- A Product may have zero or more ProductVariant instances.

---

### PhysicalProduct

**Description**

Product that requires physical inventory and a dispatch process.

**Inherits From**

Product

**Relationships**

- A PhysicalProduct is stored in zero or more Inventory instances.
- A PhysicalProduct generates a Shipment when dispatched.

---

### DigitalProduct

**Description**

Product delivered immediately upon payment confirmation; it does not require inventory or physical dispatch.

**Inherits From**

Product

---

### Inventory

**Description**

Represents the available stock of a physical product at a specific warehouse.

Inventory is distributed and must be mandatorily linked to a product and a warehouse.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| product | PhysicalProduct | Product the stock record corresponds to. |
| warehouse | Warehouse | Warehouse where the stock is held. |
| availableQuantity | Integer | Units available for sale. |
| reservedQuantity | Integer | Units reserved by orders in progress. |

**Relationships**

- An Inventory references one PhysicalProduct and one Warehouse.
- An Inventory generates zero or more InventoryMovement instances.

**Business Rule**

- Negative stock levels shall never be allowed under any circumstances.
- Inventory that does not exist or is marked as "Damaged" cannot be reserved.

---

### InventoryMovement

**Description**

Represents a significant change in the stock of an Inventory record. Provides traceability between products, warehouses, and users.

A movement represents an event that occurred; it is distinct from the current status of the inventory.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| movementId | Integer | Unique movement identifier. |
| movementType | InventoryMovementType | Category of the inventory movement. |
| quantity | Integer | Units affected by the movement. |
| executionDate | LocalDateTime | Date and time when the movement occurred. |
| performedBy | InternalStaff | User responsible for the movement. |
| affectedInventory | Inventory | Inventory record affected by the movement. |

**Relationships**

- An Inventory may generate zero or more InventoryMovement instances.
- Each InventoryMovement affects one Inventory.

**Examples of Movements**

- STOCK_IN
- RESERVATION
- SALE_OUTFLOW
- ADJUSTMENT
- RETURN

---

### ShoppingCart

**Description**

Represents the provisional selection of products by a buyer, prior to order confirmation.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| buyer | Buyer | Buyer who owns the cart. |
| items | List\<CartItem\> | Selected products and quantities. |
| lastUpdated | LocalDateTime | Last modification date of the cart. |

**Relationships**

- A ShoppingCart belongs to one Buyer.
- A ShoppingCart contains zero or more CartItem instances.
- A ShoppingCart may be converted into an Order upon purchase confirmation.

---

### Order

**Description**

Represents the formal commercial commitment between a buyer and one or more sellers. Its lifecycle is the central process of the system.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| orderId | Integer | Unique order identifier. |
| buyer | Buyer | Buyer who places the order. |
| items | List\<OrderItem\> | Confirmed products and quantities. |
| orderStatus | OrderStatus | Current state of the order's lifecycle. |
| creationDate | LocalDateTime | Date and time the order was created. |
| paymentConfirmationDate | LocalDateTime | Date on which payment was confirmed. |
| deliveryAddress | String | Delivery address selected by the buyer. |

**Relationships**

- An Order is placed by one Buyer.
- An Order contains one or more OrderItem instances.
- An Order generates one Invoice.
- An Order may generate one Shipment when it includes physical products.
- An Order may generate zero or more Return instances.

**Order Status Lifecycle**

- CART
- PENDING_PAYMENT
- PAID
- DISPATCHED
- DELIVERED_COMPLETED

**Business Rule**

- A completed order shall not be modified under any circumstances.

---

### Invoice

**Description**

Represents the commercial information associated with the sale of an order.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| invoiceId | String | Unique invoice identifier. |
| order | Order | Invoiced order. |
| issueDate | LocalDateTime | Date and time the invoice was issued. |
| totalAmount | BigDecimal | Total invoiced value. |
| currency | Currency | Currency in which the invoice is issued. |

**Relationships**

- An Invoice is generated from one Order.

---

### Shipment

**Description**

Represents the logistics process of dispatch, transport, and delivery for an order containing physical products.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| shipmentId | String | Unique shipment identifier. |
| order | Order | Order associated with the shipment. |
| logisticsOperator | LogisticsOperator | Operator responsible for the dispatch. |
| shipmentStatus | ShipmentStatus | Current status of the shipment. |
| dispatchDate | LocalDateTime | Date and time of physical departure from the warehouse. |
| deliveryDate | LocalDateTime | Date and time of confirmed delivery. |

**Relationships**

- A Shipment is generated from one Order.
- A Shipment is managed by one LogisticsOperator.

**Examples of States**

- IN_PREPARATION
- DISPATCHED
- IN_TRANSIT
- DELIVERED

---

### Return

**Description**

Represents a request to return one or more products from an order that has already been delivered.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| returnId | String | Unique return identifier. |
| order | Order | Order the return request applies to. |
| reason | ReturnReason | Declared reason for the return. |
| returnStatus | ReturnStatus | Current status of the request. |
| requestDate | LocalDateTime | Date and time the return was requested. |
| decision | ApprovalDecision | Outcome of the request's evaluation. |

**Relationships**

- A Return is associated with one Order.
- A Return may generate one Refund when approved.

---

### Refund

**Description**

Represents the monetary refund originated by an approved return.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| refundId | String | Unique refund identifier. |
| returnRequest | Return | Return that originated the refund. |
| refundedAmount | BigDecimal | Value refunded to the buyer. |
| processingDate | LocalDateTime | Date and time the refund was processed. |
| refundStatus | RefundStatus | Current status of the refund. |

**Relationships**

- A Refund is generated from one approved Return.

---

## Domain Value Objects

### Introduction

Value Objects represent immutable concepts within the NexusMarket domain. Unlike entities, they do not have their own identity; they are defined by their values and encapsulate controlled business concepts.

### Value Object Hierarchy

```
DomainCatalog (Abstract)
├── SystemRole
├── UserStatus
├── CommercialStatus
├── SellerStatus
├── WarehouseStatus
├── ProductStatus
├── InventoryMovementType
├── OrderStatus
├── ShipmentStatus
├── ReturnStatus
├── RefundStatus
└── Currency
```

### DomainCatalog (Abstract)

**Description**

Represents a generic business catalog used throughout the NexusMarket domain.

**Attributes**

| Attribute | Type | Description |
|---|---|---|
| code | String | Unique business identifier. |
| name | String | Human-readable name displayed within the application. |
| description | String | Business definition of the catalog value. |

### SystemRole

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| BUYER | Buyer | Person who purchases published products. |
| SELLER | Seller | Responsible for registering and managing their products. |
| LOGISTICS_OPERATOR | Logistics Operator | In charge of the physical operation of warehouses and dispatch. |
| ADMINISTRATOR | Administrator | Responsible for administering sellers and warehouses. |
| SUPERVISOR | Supervisor | Read-only, operational monitoring profile. |

### UserStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| ACTIVE | Active | User can access the system normally. |
| BLOCKED | Blocked | User access has been suspended. |
| INACTIVE | Inactive | User exists but cannot operate. |

### CommercialStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| ENABLED | Enabled | The buyer can make purchases. |
| SUSPENDED | Suspended | The buyer cannot make purchases temporarily. |

### SellerStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| ACTIVE | Active | Seller enabled to publish and sell. |
| SUSPENDED | Suspended | Seller temporarily disabled. |
| DEACTIVATED | Deactivated | Seller permanently removed from the marketplace. |

### WarehouseStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| ACTIVE | Active | Warehouse operational. |
| INACTIVE | Inactive | Warehouse temporarily out of operation. |

### ProductStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| PUBLISHED | Published | Product visible in the public catalog. |
| SUSPENDED | Suspended | Product temporarily hidden from the catalog. |
| DISCONTINUED | Discontinued | Product permanently removed from the catalog. |

### InventoryMovementType

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| STOCK_IN | Stock In | Entry of new stock into the warehouse. |
| RESERVATION | Reservation | Setting aside stock for an order in progress. |
| SALE_OUTFLOW | Sale Outflow | Stock decrease due to an order's dispatch. |
| ADJUSTMENT | Adjustment | Manual correction of stock. |
| RETURN | Return | Stock re-entry due to a return. |

### OrderStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| CART | Cart | Provisional selection of products. |
| PENDING_PAYMENT | Pending Payment | Awaiting financial confirmation. |
| PAID | Paid | Preparation processes begin. |
| DISPATCHED | Dispatched | Physical departure from the warehouse. |
| DELIVERED_COMPLETED | Delivered / Completed | Successful conclusion of delivery. |

### ShipmentStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| IN_PREPARATION | In Preparation | Order packing in progress. |
| DISPATCHED | Dispatched | Order in transit to the buyer. |
| DELIVERED | Delivered | Order delivered to the buyer. |

### ReturnStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| REQUESTED | Requested | Return registered by the buyer. |
| UNDER_REVIEW | Under Review | Return being evaluated by internal staff. |
| APPROVED | Approved | Return accepted, enables refund. |
| REJECTED | Rejected | Return not accepted. |

### RefundStatus

**Inherits From:** DomainCatalog

| Code | Name | Description |
|---|---|---|
| PENDING | Pending | Refund registered, not yet processed. |
| PROCESSED | Processed | Refund issued to the buyer. |
| REJECTED | Rejected | Refund denied. |

### Currency

**Inherits From:** DomainCatalog

**Additional Attributes**

| Attribute | Type | Description |
|---|---|---|
| isoCode | String | ISO 4217 currency code. |
| symbol | String | Currency symbol. |

| ISO Code | Name | Symbol |
|---|---|---|
| COP | Colombian Peso | $ |
| USD | United States Dollar | $ |

## Primitive Enumerations

The following concepts are simple enumerations because they represent fixed technical values without requiring business catalog metadata.

### ApprovalDecision

**Description:** Represents the outcome of a return's evaluation.

**Values:** APPROVED, REJECTED

### ProductVariant (Value Object)

**Description:** Represents a product variation (color, size, model).

**Attributes:** variantName (String), value (String)

### ReturnReason

**Description:** Represents the declared reason for a return.

**Values:** DAMAGED_PRODUCT, WRONG_PRODUCT, CHANGE_OF_MIND, OTHER

## Domain Design Rules

### Person and Its Specializations

- Buyer, Seller, and InternalStaff inherit from Person.
- role is defined in Person and inherited by all its specializations.
- A participant may only manage information corresponding to its own functions (RG-03).

### Warehouses

- MarketplaceWarehouse and SellerWarehouse are specializations of Warehouse.
- Every inventory record must be mandatorily linked to a warehouse and a product.

### Products and Inventory

- PhysicalProduct and DigitalProduct are specializations of Product.
- Only PhysicalProduct participates in Inventory and Shipment.
- Negative stock is never allowed under any circumstances.

### Orders

- Order status represents the current situation; InventoryMovement and Shipment represent the events that occurred.
- A completed order shall not be modified under any circumstances.

### Value Objects

- Value Objects are immutable.
- Equality is determined by their values, not by object identity.
- Business entities reference Value Objects instead of primitive strings for controlled business concepts.

## Domain Lifecycle Relationship

```
ShoppingCart
      │
      │ purchase confirmation
      ▼
   Order (CART → PENDING_PAYMENT)
      │
      │ payment confirmation
      ▼
   Order (PAID)
      │
      ├── Invoice
      │      totalAmount, issueDate
      │
      ├── InventoryMovement
      │      movementType = SALE_OUTFLOW
      │
      └── Shipment (physical products only)
             shipmentStatus = IN_PREPARATION → DISPATCHED → DELIVERED
                   │
                   ▼
             Order (DELIVERED_COMPLETED)
                   │
                   │ buyer request
                   ▼
                Return
                   │
                   │ approval
                   ▼
                Refund
```
