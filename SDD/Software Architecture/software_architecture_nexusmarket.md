# Software Architecture — NexusMarket

## Visión General

El sistema **NexusMarket** sigue una **Arquitectura Hexagonal (Puertos y Adaptadores)** combinada con principios de **Domain-Driven Design (DDD)**.

El objetivo principal de esta arquitectura es aislar el dominio del negocio de las tecnologías externas, garantizando que las reglas de negocio del marketplace permanezcan independientes de frameworks, bases de datos, protocolos de comunicación y aspectos de infraestructura.

Este enfoque promueve mantenibilidad, escalabilidad, testabilidad e independencia tecnológica.

---

# Principios Arquitectónicos

La arquitectura se basa en los siguientes principios:

- Diseño centrado en el dominio (domain-first).
- Separación de responsabilidades.
- Inversión de dependencias.
- Independencia tecnológica.
- Alta cohesión.
- Bajo acoplamiento.
- Fronteras explícitas entre capas.

El dominio contiene todas las reglas de negocio del marketplace y nunca depende de tecnologías externas.

---

# Capas de la Arquitectura

La aplicación se organiza en cuatro componentes principales:

```
Application
│
├── Adapters
│
├── Domain
│
└── Infrastructure
```

Cada componente tiene una responsabilidad claramente definida.

---

# Estructura de Paquetes

```text
src/
└── main/
    └── java/
        └── application/
            │
            ├── App.java
            │
            ├── adapters/
            │   │
            │   ├── in/
            │   │   └── rest/
            │   │       ├── controllers/
            │   │       ├── requests/
            │   │       ├── responses/
            │   │       └── mappers/
            │   │
            │   └── out/
            │       └── persistence/
            │           ├── mysql/
            │           │   ├── adapters/
            │           │   ├── entities/
            │           │   ├── repositories/
            │           │   └── mappers/
            │           │
            │           └── mongodb/
            │               ├── adapters/
            │               ├── documents/
            │               ├── repositories/
            │               └── mappers/
            │
            ├── domain/
            │   ├── models/
            │   ├── valueobjects/
            │   ├── enums/
            │   ├── services/
            │   ├── exceptions/
            │   └── ports/
            │       ├── in/
            │       └── out/
            │
            └── infrastructure/
                ├── config/
                ├── database/
                └── security/
```

---

# Responsabilidades por Capa

## Application

El paquete `application` representa la raíz del proyecto.

Contiene el punto de entrada de la aplicación y todos los componentes arquitectónicos.

### Responsabilidades

- Arranque de la aplicación.
- Organización de componentes.
- Composición de dependencias.

---

## App.java

### Descripción

`App.java` es el punto de entrada de la aplicación.

### Responsabilidades

- Inicializar la aplicación.
- Cargar la infraestructura.
- Configurar la inyección de dependencias.
- Iniciar el servidor REST.

---

# Adapters

Los adaptadores conectan las tecnologías externas con el dominio del negocio.

Los adaptadores traducen solicitudes externas en operaciones de dominio y transforman objetos de dominio en representaciones específicas de cada tecnología.

El dominio nunca se comunica directamente con sistemas externos.

---

## Adaptadores de Entrada

Los adaptadores de entrada exponen la aplicación a clientes externos (compradores, vendedores, personal interno).

Implementación actual:

```
adapters/in/rest
```

### Responsabilidades

- Recibir solicitudes HTTP.
- Validar los datos entrantes.
- Convertir Request DTOs en Domain Models.
- Ejecutar los casos de uso de la aplicación.
- Convertir los resultados del dominio en Response DTOs.

---

### Controllers

Los controllers exponen los endpoints REST del marketplace.

Responsabilidades:

- Recibir solicitudes HTTP (crear pedido, publicar producto, registrar bodega, solicitar devolución, etc.).
- Delegar la ejecución al dominio.
- Retornar respuestas HTTP.

Los controllers nunca deben implementar reglas de negocio.

---

### Requests

Los Request DTOs representan los payloads HTTP entrantes.

Responsabilidades:

- Recibir los datos del cliente (por ejemplo, `CreateOrderRequest`, `RegisterSellerRequest`, `RequestReturnRequest`).
- Validar el formato de entrada.
- Transportar datos hacia la aplicación.

Estos objetos no deben contener lógica de negocio.

---

### Responses

Los Response DTOs representan las respuestas HTTP salientes.

Responsabilidades:

- Retornar la información procesada (por ejemplo, `OrderResponse`, `InventoryResponse`, `ShipmentResponse`).
- Ocultar la implementación interna del dominio.
- Estandarizar las respuestas de la API.

---

### Mappers

Responsables de convertir entre:

- Request DTO ↔ Domain Model
- Domain Model ↔ Response DTO

Esto evita que el dominio dependa de objetos de transporte.

---

# Output Adapters

Los adaptadores de salida conectan el dominio con recursos externos.

Ejemplos:

- Bases de datos
- Servicios de notificación (correo/SMS a compradores y vendedores)
- Pasarelas de pago
- Sistemas de mensajería

Implementación actual:

```
Persistence
├── MySQL
└── MongoDB
```

---

## Adaptador MySQL

Responsable de la persistencia relacional del catálogo, inventario y transacciones.

### Componentes

#### Entities

Representan las tablas de la base de datos relacional (por ejemplo, `BuyerEntity`, `SellerEntity`, `ProductEntity`, `OrderEntity`, `InvoiceEntity`, `ShipmentEntity`).

#### Repositories

Implementan las operaciones de persistencia.

#### Mappers

Convierten Domain Models en entidades de base de datos.

#### Adapters

Implementan los Output Ports del dominio.

---

## Adaptador MongoDB

Responsable de almacenar el historial de movimientos de inventario.

Los movimientos de inventario (`InventoryMovement`) se benefician de un almacenamiento flexible y de solo anexado, ya que registran eventos históricos (ingresos, reservas, salidas por venta, ajustes, devoluciones) que no deben modificarse una vez persistidos.

### Componentes

#### Documents

Representan las colecciones de MongoDB (por ejemplo, `InventoryMovementDocument`).

#### Repositories

Proveen la persistencia de documentos.

#### Mappers

Convierten objetos de dominio en documentos de MongoDB.

#### Adapters

Implementan los puertos de persistencia del historial de movimientos.

---

# Domain

La capa de Domain es el núcleo de la aplicación.

Contiene todas las reglas de negocio del marketplace y debe permanecer independiente de cualquier tecnología externa.

Ninguna clase dentro del dominio puede depender de:

- Spring
- JPA
- MongoDB
- HTTP
- REST
- JSON
- SQL

---

## Models

Contienen las entidades de negocio.

Ejemplos:

- Person
- Buyer
- Seller
- InternalStaff
- Warehouse
- Product
- Inventory
- InventoryMovement
- ShoppingCart
- Order
- Invoice
- Shipment
- Return
- Refund

Estos objetos representan el negocio del marketplace.

---

## Value Objects

Representan conceptos de negocio inmutables.

Ejemplos:

- SystemRole
- UserStatus
- CommercialStatus
- SellerStatus
- ProductStatus
- OrderStatus
- ShipmentStatus
- ReturnStatus
- RefundStatus
- Currency

Los Value Objects se comparan por valor y no por identidad.

---

## Enums

Contienen enumeraciones técnicas que no requieren comportamiento de negocio.

Ejemplos:

- ApprovalDecision
- ReturnReason

---

## Services

Contienen lógica de negocio que no pertenece naturalmente a una sola entidad.

Ejemplos:

- OrderProcessingService
- InventoryReservationService
- ShipmentDispatchService
- RefundProcessingService

Los servicios coordinan operaciones de negocio preservando la integridad del dominio.

---

## Ports

Los puertos definen los contratos de comunicación entre el dominio y las tecnologías externas.

El dominio es dueño de todas las interfaces.

---

### Input Ports

Representan los casos de uso de la aplicación.

Ejemplos:

- RegisterSellerUseCase
- PublishProductUseCase
- AddToCartUseCase
- PlaceOrderUseCase
- ConfirmPaymentUseCase
- DispatchShipmentUseCase
- RequestReturnUseCase
- ApproveReturnUseCase

Los Input Ports definen qué puede hacer el sistema.

---

### Output Ports

Representan las dependencias que requiere el dominio.

Ejemplos:

- BuyerRepository
- SellerRepository
- ProductRepository
- InventoryRepository
- InventoryMovementRepository
- OrderRepository
- NotificationService

Los Output Ports definen qué necesita el dominio de los sistemas externos.

---

## Exceptions

Contiene las excepciones de negocio.

Ejemplos:

- InsufficientStockException
- OrderAlreadyCompletedException
- InvalidReturnRequestException
- SellerNotAuthorizedException

Las excepciones de negocio pertenecen exclusivamente al dominio.

---

# Infrastructure

Infrastructure contiene la configuración técnica requerida por la aplicación.

No contiene lógica de negocio.

---

## Config

Responsable de la configuración de la aplicación.

Ejemplos:

- Configuración REST
- Serialización
- Configuración de entorno

---

## Database

Contiene la inicialización de bases de datos y la configuración de conexión.

Ejemplos:

- Configuración de MySQL
- Configuración de MongoDB
- Pools de conexión

---

## Security

Contiene la configuración de autenticación y autorización.

Ejemplos:

- Configuración JWT
- Codificador de contraseñas
- Filtros de autenticación (diferenciando permisos por rol: Comprador, Vendedor, Operador Logístico, Administrador, Supervisor)

---

# Flujo de Dependencias

Las dependencias siempre apuntan hacia el dominio.

```
REST Controller
        │
        ▼
Input Port
        │
        ▼
Domain Service
        │
        ▼
Output Port
        │
        ▼
Persistence Adapter
        │
        ▼
Database
```

El dominio nunca depende de los adapters ni de la infraestructura.

---

# Beneficios

Esta arquitectura provee:

- Independencia tecnológica.
- Alta mantenibilidad.
- Separación clara de responsabilidades.
- Testabilidad mejorada.
- Escalabilidad más sencilla.
- Mejor soporte para Domain-Driven Design.
- Fácil reemplazo de frameworks o bases de datos.
- Lógica de negocio reutilizable.
- Mantenibilidad a largo plazo.

---

# Restricciones Arquitectónicas

Las siguientes reglas deben respetarse siempre:

1. La lógica de negocio pertenece exclusivamente a la capa de Domain.
2. Los controllers no deben contener reglas de negocio.
3. Los DTOs nunca deben entrar a la capa de Domain.
4. Las entidades de persistencia nunca deben exponerse a través de la API.
5. La comunicación entre las tecnologías y el Domain debe ocurrir únicamente a través de Ports.
6. Los adapters implementan Ports pero nunca definen reglas de negocio.
7. Infrastructure depende del Domain, nunca al revés.
8. Toda dependencia debe apuntar hacia el Domain.
9. Las entidades de negocio deben permanecer independientes del framework.
10. El Domain debe ser completamente testeable sin requerir componentes de infraestructura.
