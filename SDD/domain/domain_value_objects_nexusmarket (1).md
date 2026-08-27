# Domain Value Objects — NexusMarket

## Introducción

Los Value Objects representan conceptos inmutables dentro del dominio de NexusMarket.

A diferencia de las Entidades, los Value Objects no tienen identidad propia. Se definen completamente por sus valores y se utilizan para encapsular conceptos de negocio controlados, mejorar la expresividad del dominio y evitar el uso de valores primitivos o cadenas de texto dispersas a lo largo de la aplicación.

El dominio de NexusMarket utiliza Value Objects para catálogos de negocio como roles, estados, tipos de movimiento de inventario, estados de pedidos, envíos, devoluciones, reembolsos y monedas.

Todos los catálogos de negocio heredan de DomainCatalog.

## Jerarquía de Value Objects

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

## DomainCatalog (Abstract)

### Descripción

Representa un catálogo de negocio genérico utilizado en todo el dominio de NexusMarket.

DomainCatalog provee una estructura consistente para los valores de negocio controlados que requieren un código, un nombre legible y una descripción de negocio.

Esta clase no puede instanciarse directamente.

### Atributos

| Atributo | Tipo | Descripción |
|---|---|---|
| codigo | String | Identificador de negocio único del valor del catálogo. |
| nombre | String | Nombre legible mostrado dentro de la aplicación. |
| descripcion | String | Definición de negocio del valor del catálogo. |

### Características

- Inmutable.
- La igualdad se determina por el valor, no por identidad de objeto.
- Los valores del catálogo son controlados por el dominio.
- Los valores del catálogo no deben representarse mediante cadenas de texto arbitrarias en la aplicación.
- Cada valor del catálogo debe tener un código único.

## SystemRole

### Descripción

Representa las responsabilidades y permisos asignados a una persona dentro de NexusMarket.

El rol es una característica de Person porque representa lo que esa persona significa dentro del sistema y las responsabilidades asociadas a ella.

El atributo rol se define, por lo tanto, en Person y se hereda en sus especializaciones, incluyendo Buyer, Seller e InternalStaff.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| COMPRADOR | Comprador | Persona que adquiere productos publicados en el catálogo. |
| VENDEDOR | Vendedor | Responsable de registrar y administrar sus propios productos. |
| OPERADOR_LOGISTICO | Operador Logístico | Encargado de la operación física de bodegas y despachos. |
| ADMINISTRADOR | Administrador | Responsable de la administración de vendedores y bodegas del marketplace. |
| SUPERVISOR | Supervisor | Perfil de consulta y seguimiento operativo, sin permisos de administración. |

## UserStatus

### Descripción

Representa el estado actual de acceso de una persona al sistema NexusMarket.

UserStatus es independiente del CommercialStatus del comprador o del SellerStatus del vendedor. Una persona puede estar bloqueada en el sistema mientras su relación comercial permanece activa, o viceversa.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| ACTIVO | Activo | El usuario puede acceder al sistema con normalidad. |
| INACTIVO | Inactivo | El usuario existe pero no puede realizar operaciones en el sistema. |
| BLOQUEADO | Bloqueado | El acceso del usuario ha sido suspendido. |

## CommercialStatus

### Descripción

Representa el estado actual de la relación comercial de un comprador con el marketplace.

CommercialStatus es independiente de UserStatus. Representa el estado de la habilidad del comprador para realizar compras, no el estado de su acceso al sistema.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| HABILITADO | Habilitado | El comprador puede realizar compras con normalidad. |
| SUSPENDIDO | Suspendido | El comprador no puede realizar compras temporalmente. |

## SellerStatus

### Descripción

Representa el estado operativo de un vendedor dentro del marketplace.

El estado describe la condición actual de la relación comercial del vendedor y es independiente del estado de su acceso al sistema (UserStatus).

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| ACTIVO | Activo | Vendedor habilitado para publicar y vender productos. |
| SUSPENDIDO | Suspendido | Vendedor temporalmente inhabilitado para operar. |
| DADO_DE_BAJA | Dado de Baja | Vendedor retirado permanentemente del marketplace. |

### Ciclo de Vida

```
ACTIVO
   │
   ├──────────────> SUSPENDIDO
   │                     │
   │                     ├──────────────> ACTIVO
   │                     │
   │                     ▼
   │                DADO_DE_BAJA
   │
   ▼
DADO_DE_BAJA
```

## WarehouseStatus

### Descripción

Representa el estado operativo de una bodega, ya sea del marketplace o de un vendedor.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| ACTIVA | Activa | La bodega se encuentra operativa y puede recibir movimientos de inventario. |
| INACTIVA | Inactiva | La bodega se encuentra temporalmente fuera de operación. |

## ProductStatus

### Descripción

Representa el estado del ciclo de vida de un producto dentro del catálogo.

El estado describe la condición actual de visibilidad del producto y es independiente del estado del vendedor que lo publica.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| PUBLICADO | Publicado | El producto es visible en el catálogo público. |
| SUSPENDIDO | Suspendido | El producto se encuentra oculto temporalmente del catálogo. |
| DESCONTINUADO | Descontinuado | El producto ha sido retirado permanentemente del catálogo. |

### Ciclo de Vida

```
PUBLICADO
   │
   ├──────────────> SUSPENDIDO
   │                     │
   │                     ▼
   │                PUBLICADO
   │
   ▼
DESCONTINUADO
```

## InventoryMovementType

### Descripción

Representa el tipo de movimiento significativo ejecutado sobre un registro de Inventory.

Los movimientos de inventario representan eventos de negocio ocurridos sobre las existencias, y son independientes del estado actual del inventario:

- Una cantidad disponible representa el estado actual de las existencias.
- Un movimiento representa la acción o evento que ocurrió.

Por ejemplo:

`Inventory.cantidadDisponible` disminuye

representa el estado actual del inventario, mientras que:

`InventoryMovement.movimientoType = SALIDA_POR_VENTA`

representa el evento que causó dicha disminución.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| INGRESO | Ingreso | Entrada de nuevas existencias a la bodega. |
| RESERVA | Reserva | Apartado de existencias para un pedido en curso. |
| SALIDA_POR_VENTA | Salida por Venta | Disminución de existencias por el despacho de un pedido. |
| AJUSTE | Ajuste | Corrección manual de existencias. |
| DEVOLUCION | Devolución | Reingreso de existencias por una devolución aprobada. |

## OrderStatus

### Descripción

Representa el estado de ejecución de un pedido.

El estado describe la situación actual del pedido, mientras que las operaciones asociadas (movimientos de inventario, facturación, envío) proveen el registro histórico de las acciones ejecutadas a lo largo de su ciclo de vida.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| CARRITO | Carrito | Selección provisional de productos, aún no confirmada como pedido. |
| PENDIENTE_DE_PAGO | Pendiente de Pago | Pedido creado, en espera de confirmación financiera. |
| PAGADO | Pagado | Pago confirmado; inicia el proceso de alistamiento. |
| DESPACHADO | Despachado | Pedido despachado físicamente desde la bodega. |
| ENTREGADO_FINALIZADO | Entregado / Finalizado | Entrega concluida satisfactoriamente. |

### Ciclo de Vida

```
CARRITO
   │
   ▼
PENDIENTE_DE_PAGO
   │
   ▼
PAGADO
   │
   ▼
DESPACHADO
   │
   ▼
ENTREGADO_FINALIZADO
```

## ShipmentStatus

### Descripción

Representa el estado de ejecución del proceso logístico de un envío.

El estado describe la situación actual del envío mientras es gestionado por un LogisticsOperator.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| EN_PREPARACION | En Preparación | Empaque del pedido en curso dentro de la bodega. |
| DESPACHADO | Despachado | Pedido despachado y en tránsito hacia el comprador. |
| EN_TRANSITO | En Tránsito | Pedido en proceso de transporte hacia el destino final. |
| ENTREGADO | Entregado | Pedido entregado al comprador. |

### Ciclo de Vida

```
EN_PREPARACION
   │
   ▼
DESPACHADO
   │
   ▼
EN_TRANSITO
   │
   ▼
ENTREGADO
```

## ReturnStatus

### Descripción

Representa el estado de una solicitud de devolución.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| SOLICITADA | Solicitada | Devolución registrada por el comprador. |
| EN_REVISION | En Revisión | Devolución en evaluación por el personal interno. |
| APROBADA | Aprobada | Devolución aceptada; habilita la generación de un Refund. |
| RECHAZADA | Rechazada | Devolución no aceptada. |

### Ciclo de Vida

```
SOLICITADA
   │
   ▼
EN_REVISION
   │
   ├──────────────> RECHAZADA
   │
   ▼
APROBADA
```

## RefundStatus

### Descripción

Representa el estado de un reembolso originado por una devolución aprobada.

### Hereda de

DomainCatalog

### Valores Permitidos

| Código | Nombre | Descripción |
|---|---|---|
| PENDIENTE | Pendiente | Reembolso registrado, aún no procesado. |
| PROCESADO | Procesado | Reembolso efectuado al comprador. |
| RECHAZADO | Rechazado | Reembolso denegado. |

### Ciclo de Vida

```
PENDIENTE
   │
   ├──────────────> RECHAZADO
   │
   ▼
PROCESADO
```

## Currency

### Descripción

Representa una moneda soportada por NexusMarket para la facturación de pedidos.

Currency es un Value Object de negocio porque su significado está determinado por sus valores controlados y no por una identidad independiente.

### Hereda de

DomainCatalog

### Atributos Adicionales

| Atributo | Tipo | Descripción |
|---|---|---|
| codigoISO | String | Código de moneda ISO 4217. |
| simbolo | String | Símbolo de la moneda utilizado para su visualización. |

### Valores Permitidos

| Código ISO | Nombre | Símbolo |
|---|---|---|
| COP | Peso Colombiano | $ |
| USD | Dólar Estadounidense | $ |

## Otros Value Objects

### ProductVariant

**Descripción**

Representa una variación específica de un producto, como color, talla o modelo.

A diferencia de los catálogos anteriores, ProductVariant no hereda de DomainCatalog: no representa un valor controlado por el dominio, sino una combinación libre definida por el vendedor al momento de publicar el producto.

**Atributos**

| Atributo | Tipo | Descripción |
|---|---|---|
| nombreVariante | String | Nombre del atributo de variación (por ejemplo, "Color", "Talla"). |
| valor | String | Valor específico de la variación (por ejemplo, "Rojo", "M"). |

**Características**

- Inmutable.
- La igualdad se determina por la combinación de nombreVariante y valor.

## Enumeraciones Primitivas

Los siguientes conceptos se representan como enumeraciones primitivas porque contienen valores técnicos fijos y no requieren metadatos de catálogo de negocio como código, nombre o descripción.

### ApprovalDecision

**Descripción**

Representa el resultado de un proceso de evaluación, utilizado al decidir sobre una solicitud de devolución.

**Valores**

- APROBADO
- RECHAZADO

### ReturnReason

**Descripción**

Representa el motivo declarado por el comprador al solicitar una devolución.

**Valores**

- PRODUCTO_DAÑADO
- PRODUCTO_INCORRECTO
- CAMBIO_DE_OPINION
- OTRO

## Reglas de Diseño de los Value Objects

### Inmutabilidad

Todos los Value Objects deben ser inmutables después de su creación.

Sus valores no pueden modificarse una vez que el objeto ha sido instanciado.

### Igualdad

Los Value Objects se comparan según sus valores y no según la identidad del objeto.

Dos instancias que contienen los mismos valores de negocio representan el mismo Value Object.

### Valores Controlados

Los catálogos de negocio deben utilizar valores controlados definidos por el dominio.

La aplicación debe evitar reemplazar estos conceptos por cadenas de texto arbitrarias como:

- `"ACTIVO"`
- `"BLOQUEADO"`
- `"APROBADO"`

a lo largo del código base.

En su lugar, debe utilizarse el Value Object correspondiente:

- CommercialStatus
- SellerStatus
- UserStatus
- ProductStatus
- OrderStatus
- ShipmentStatus
- ReturnStatus
- RefundStatus

### Enumeraciones de Negocio Versus Técnicas

Un concepto de negocio debe modelarse como un Value Object DomainCatalog cuando requiere:

- un código de negocio;
- un nombre para mostrar;
- una descripción de negocio;
- una evolución controlada por el dominio.

Debe utilizarse una enumeración simple cuando el concepto representa un valor técnico fijo sin metadatos de negocio adicionales.

### Relación con las Entidades

Las entidades referencian Value Objects en lugar de cadenas de texto primitivas siempre que el valor referenciado represente un concepto de negocio controlado.

Ejemplos:

- `Person.rol : SystemRole`
- `Person.estado : UserStatus`
- `Buyer.estadoComercial : CommercialStatus`
- `Seller.estadoVendedor : SellerStatus`
- `Warehouse.estado : WarehouseStatus`
- `Product.estado : ProductStatus`
- `InventoryMovement.tipoMovimiento : InventoryMovementType`
- `Order.estadoPedido : OrderStatus`
- `Shipment.estadoEnvio : ShipmentStatus`
- `Return.estadoDevolucion : ReturnStatus`
- `Refund.estadoReembolso : RefundStatus`
- `Invoice.moneda : Currency`

Este enfoque mejora la seguridad de tipos, la expresividad del dominio, la mantenibilidad y la consistencia con los principios de Domain-Driven Design.
