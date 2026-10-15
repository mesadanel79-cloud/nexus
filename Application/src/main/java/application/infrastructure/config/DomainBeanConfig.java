package application.infrastructure.config;

import application.adapters.out.persistence.memory.RefundRepositoryAdapter;
import application.adapters.out.persistence.mongodb.adapters.InventoryMovementRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.BuyerRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.OrderRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.ProductRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.SellerRepositoryAdapter;
import application.domain.models.Administrator;
import application.domain.models.LogisticsOperator;
import application.domain.ports.out.NotificationService;
import application.domain.ports.out.PasswordHasher;
import application.domain.services.AuthorizationService;
import application.domain.services.BankAccountOpeningService;
import application.domain.services.BankAccountService;
import application.domain.services.BankAccountStatusService;
import application.domain.services.BankDepositWithdrawService;
import application.domain.services.BankSettlementService;
import application.domain.services.CatalogManagementService;
import application.domain.services.CustomerActivityService;
import application.domain.services.CustomerManagementService;
import application.domain.services.ExecuteTransferService;
import application.domain.services.InventoryReservationService;
import application.domain.services.InvoicingService;
import application.domain.services.OperationAuditService;
import application.domain.services.OrderLifecycleService;
import application.domain.services.OrderPaymentService;
import application.domain.services.OrderProcessingService;
import application.domain.services.RefundProcessingService;
import application.domain.services.RefundSettlementService;
import application.domain.services.ShipmentDispatchService;
import application.domain.services.UserAuthenticationService;
import application.domain.services.WarehouseInventoryService;
import application.domain.valueobjects.UserStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Infrastructure configuration: composes domain services with their output
 * adapters (dependency injection wiring). Dependencies always point toward
 * the domain.
 */
@Configuration
public class DomainBeanConfig {

    @Bean
    public OrderProcessingService orderProcessingService(
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            ProductRepositoryAdapter productRepositoryAdapter,
            OrderRepositoryAdapter orderRepositoryAdapter,
            NotificationService notificationService) {
        return new OrderProcessingService(
                buyerRepositoryAdapter,
                productRepositoryAdapter,
                orderRepositoryAdapter,
                notificationService);
    }

    @Bean
    public InventoryReservationService inventoryReservationService(
            InventoryMovementRepositoryAdapter movementRepositoryAdapter) {
        // Development in-memory inventory store (replaceable via the port).
        application.domain.ports.out.InventoryRepository inventoryRepository =
                new application.domain.ports.out.InventoryRepository() {
                    private final java.util.Map<String,
                            application.domain.models.Inventory> store =
                            new java.util.concurrent.ConcurrentHashMap<>();

                    @Override
                    public application.domain.models.Inventory save(
                            application.domain.models.Inventory inventory) {
                        store.put(inventory.getProduct().getIdentifier()
                                + ":" + inventory.getWarehouse().getIdentifier(),
                                inventory);
                        return inventory;
                    }

                    @Override
                    public java.util.Optional<application.domain.models.Inventory> findById(
                            Long id) {
                        return java.util.Optional.empty();
                    }

                    @Override
                    public java.util.Optional<application.domain.models.Inventory> findByProductAndWarehouse(
                            application.domain.models.PhysicalProduct product,
                            application.domain.models.Warehouse warehouse) {
                        return java.util.Optional.ofNullable(store.get(
                                product.getIdentifier() + ":"
                                        + warehouse.getIdentifier()));
                    }

                    @Override
                    public java.util.List<application.domain.models.Inventory> findByProduct(
                            application.domain.models.PhysicalProduct product) {
                        return store.entrySet().stream()
                                .filter(e -> e.getKey().startsWith(
                                        product.getIdentifier() + ":"))
                                .map(java.util.Map.Entry::getValue)
                                .toList();
                    }

                    @Override
                    public java.util.List<application.domain.models.Inventory> findAll() {
                        return java.util.List.copyOf(store.values());
                    }
                };
        return new InventoryReservationService(
                inventoryRepository,
                (type, quantity, productId, warehouseId) -> {
                    // Persist the append-only movement history in MongoDB.
                    movementRepositoryAdapter.save(
                            new application.domain.models.InventoryMovement(
                                    null, type, quantity,
                                    java.time.LocalDateTime.now(), null, null));
                });
    }

    @Bean
    public ShipmentDispatchService shipmentDispatchService(
            OrderRepositoryAdapter orderRepositoryAdapter,
            NotificationService notificationService,
            InventoryReservationService inventoryReservationService) {
        ShipmentDispatchService service = new ShipmentDispatchService(
                orderRepositoryAdapter, notificationService,
                inventoryReservationService);
        // Seed a demo logistics operator for development purposes.
        service.registerOperator(new LogisticsOperator("OP-001", "Operador Demo",
                "operador@nexusmarket.local", UserStatus.ACTIVO));
        return service;
    }

    @Bean
    public RefundProcessingService refundProcessingService(
            OrderRepositoryAdapter orderRepositoryAdapter,
            NotificationService notificationService) {
        return new RefundProcessingService(orderRepositoryAdapter,
                notificationService);
    }

    @Bean
    public BankAccountService bankAccountService(
            application.adapters.out.persistence.memory.BankAccountRepositoryAdapter bankAccountRepositoryAdapter,
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            SellerRepositoryAdapter sellerRepositoryAdapter) {
        return new BankAccountService(
                bankAccountRepositoryAdapter,
                buyerRepositoryAdapter,
                sellerRepositoryAdapter);
    }

    @Bean
    public ExecuteTransferService executeTransferService(
            application.adapters.out.persistence.memory.BankAccountRepositoryAdapter bankAccountRepositoryAdapter) {
        return new ExecuteTransferService(bankAccountRepositoryAdapter);
    }

    @Bean
    public CustomerManagementService customerManagementService(
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            SellerRepositoryAdapter sellerRepositoryAdapter,
            OrderRepositoryAdapter orderRepositoryAdapter,
            Administrator demoAdministrator) {
        return new CustomerManagementService(buyerRepositoryAdapter,
                sellerRepositoryAdapter, orderRepositoryAdapter)
                .registerAdministrator(demoAdministrator);
    }

    @Bean
    public CatalogManagementService catalogManagementService(
            ProductRepositoryAdapter productRepositoryAdapter,
            SellerRepositoryAdapter sellerRepositoryAdapter) {
        return new CatalogManagementService(productRepositoryAdapter,
                sellerRepositoryAdapter);
    }

    @Bean
    public UserAuthenticationService userAuthenticationService(
            application.adapters.out.persistence.memory.UserRepositoryAdapter userRepositoryAdapter,
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            SellerRepositoryAdapter sellerRepositoryAdapter,
            PasswordHasher passwordHasher,
            Administrator demoAdministrator) {
        return new UserAuthenticationService(userRepositoryAdapter,
                buyerRepositoryAdapter, sellerRepositoryAdapter,
                passwordHasher).registerAdministrator(demoAdministrator);
    }

    @Bean
    public InvoicingService invoicingService(
            OrderRepositoryAdapter orderRepositoryAdapter,
            application.adapters.out.persistence.memory.InvoiceRepositoryAdapter invoiceRepositoryAdapter,
            BuyerRepositoryAdapter buyerRepositoryAdapter) {
        return new InvoicingService(orderRepositoryAdapter,
                invoiceRepositoryAdapter, buyerRepositoryAdapter);
    }

    /**
     * Authorization service (Authorization subdomain). Authorization is
     * evaluated directly from the User and marketplace asset Domain Models,
     * so it requires no output ports.
     */
    @Bean
    public AuthorizationService authorizationService() {
        return new AuthorizationService();
    }

    /**
     * Operation and Audit service. Traceability records are persisted
     * through the Operation and Audit output ports (in-memory adapters for
     * development, replaceable by MySQL/MongoDB adapters).
     */
    @Bean
    public OperationAuditService operationAuditService(
            application.adapters.out.persistence.memory.OperationRepositoryAdapter operationRepositoryAdapter,
            application.adapters.out.persistence.memory.AuditRepositoryAdapter auditRepositoryAdapter) {
        return new OperationAuditService(operationRepositoryAdapter,
                auditRepositoryAdapter);
    }

    @Bean
    public OrderLifecycleService orderLifecycleService(
            OrderRepositoryAdapter orderRepositoryAdapter,
            application.adapters.out.persistence.memory.WarehouseRepositoryAdapter warehouseRepositoryAdapter,
            InventoryReservationService inventoryReservationService,
            NotificationService notificationService) {
        return new OrderLifecycleService(orderRepositoryAdapter,
                warehouseRepositoryAdapter, inventoryReservationService,
                notificationService);
    }

    @Bean
    public CustomerActivityService customerActivityService(
            SellerRepositoryAdapter sellerRepositoryAdapter,
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            ProductRepositoryAdapter productRepositoryAdapter,
            OrderRepositoryAdapter orderRepositoryAdapter,
            application.adapters.out.persistence.memory.ShipmentRepositoryAdapter shipmentRepositoryAdapter) {
        return new CustomerActivityService(sellerRepositoryAdapter,
                buyerRepositoryAdapter, productRepositoryAdapter,
                orderRepositoryAdapter, shipmentRepositoryAdapter);
    }

    @Bean
    public RefundSettlementService refundSettlementService(
            RefundRepositoryAdapter refundRepositoryAdapter,
            NotificationService notificationService) {
        return new RefundSettlementService(refundRepositoryAdapter,
                notificationService);
    }

    @Bean
    public BankAccountOpeningService bankAccountOpeningService(
            application.adapters.out.persistence.memory.BankAccountRepositoryAdapter bankAccountRepositoryAdapter,
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            SellerRepositoryAdapter sellerRepositoryAdapter) {
        return new BankAccountOpeningService(bankAccountRepositoryAdapter,
                bankAccountRepositoryAdapter, buyerRepositoryAdapter,
                sellerRepositoryAdapter);
    }

    @Bean
    public BankDepositWithdrawService bankDepositWithdrawService(
            application.adapters.out.persistence.memory.BankAccountRepositoryAdapter bankAccountRepositoryAdapter) {
        return new BankDepositWithdrawService(bankAccountRepositoryAdapter);
    }

    @Bean
    public BankAccountStatusService bankAccountStatusService(
            application.adapters.out.persistence.memory.BankAccountRepositoryAdapter bankAccountRepositoryAdapter) {
        return new BankAccountStatusService(bankAccountRepositoryAdapter);
    }

    @Bean
    public BankSettlementService bankSettlementService(
            application.adapters.out.persistence.memory.BankAccountRepositoryAdapter bankAccountRepositoryAdapter) {
        return new BankSettlementService(bankAccountRepositoryAdapter);
    }

    @Bean
    public OrderPaymentService orderPaymentService(
            BuyerRepositoryAdapter buyerRepositoryAdapter,
            OrderRepositoryAdapter orderRepositoryAdapter,
            NotificationService notificationService) {
        return new OrderPaymentService(buyerRepositoryAdapter,
                orderRepositoryAdapter, notificationService);
    }

    /** Seeds a demo administrator for development purposes. */
    @Bean
    public Administrator demoAdministrator() {
        return new Administrator("ADM-001", "Administrador Demo",
                "admin@nexusmarket.local", UserStatus.ACTIVO);
    }

    /**
     * Inventory and Warehouse Management service. Uses the existing
     * MySQL adapters for warehouses/products/sellers and the movement
     * adapter for the append-only history; inventory itself keeps the
     * development in-memory store already composed above.
     */
    @Bean
    public WarehouseInventoryService warehouseInventoryService(
            InventoryMovementRepositoryAdapter movementRepositoryAdapter,
            ProductRepositoryAdapter productRepositoryAdapter,
            SellerRepositoryAdapter sellerRepositoryAdapter) {
        application.domain.ports.out.WarehouseRepository warehouseAdapter =
                new application.domain.ports.out.WarehouseRepository() {
                    private final java.util.Map<String,
                            application.domain.models.Warehouse> store =
                            new java.util.concurrent.ConcurrentHashMap<>();

                    @Override
                    public application.domain.models.Warehouse save(
                            application.domain.models.Warehouse w) {
                        store.put(w.getIdentifier(), w);
                        return w;
                    }

                    @Override
                    public java.util.Optional<application.domain.models.Warehouse> findById(String id) {
                        return java.util.Optional.ofNullable(store.get(id));
                    }

                    @Override
                    public java.util.List<application.domain.models.Warehouse> findAll() {
                        return java.util.List.copyOf(store.values());
                    }
                };
        return new WarehouseInventoryService(warehouseAdapter,
                new application.domain.ports.out.InventoryRepository() {
                    private final java.util.Map<String,
                            application.domain.models.Inventory> store =
                            new java.util.concurrent.ConcurrentHashMap<>();

                    @Override
                    public application.domain.models.Inventory save(
                            application.domain.models.Inventory inventory) {
                        store.put(inventory.getProduct().getIdentifier()
                                + ":" + inventory.getWarehouse().getIdentifier(),
                                inventory);
                        return inventory;
                    }

                    @Override
                    public java.util.Optional<application.domain.models.Inventory> findById(Long id) {
                        return java.util.Optional.empty();
                    }

                    @Override
                    public java.util.Optional<application.domain.models.Inventory> findByProductAndWarehouse(
                            application.domain.models.PhysicalProduct product,
                            application.domain.models.Warehouse warehouse) {
                        return java.util.Optional.ofNullable(store.get(
                                product.getIdentifier() + ":" + warehouse.getIdentifier()));
                    }

                    @Override
                    public java.util.List<application.domain.models.Inventory> findByProduct(
                            application.domain.models.PhysicalProduct product) {
                        return store.entrySet().stream()
                                .filter(e -> e.getKey().startsWith(product.getIdentifier() + ":"))
                                .map(java.util.Map.Entry::getValue).toList();
                    }

                    @Override
                    public java.util.List<application.domain.models.Inventory> findAll() {
                        return java.util.List.copyOf(store.values());
                    }
                },
                movementRepositoryAdapter, productRepositoryAdapter, sellerRepositoryAdapter);
    }
}