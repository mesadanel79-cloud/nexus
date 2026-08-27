package application.infrastructure.config;

import application.adapters.out.persistence.mongodb.adapters.InventoryMovementRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.BuyerRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.OrderRepositoryAdapter;
import application.adapters.out.persistence.mysql.adapters.ProductRepositoryAdapter;
import application.domain.models.Administrator;
import application.domain.models.LogisticsOperator;
import application.domain.ports.out.NotificationService;
import application.domain.services.InventoryReservationService;
import application.domain.services.OrderProcessingService;
import application.domain.services.RefundProcessingService;
import application.domain.services.ShipmentDispatchService;
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

    /** Seeds a demo administrator for development purposes. */
    @Bean
    public Administrator demoAdministrator() {
        return new Administrator("ADM-001", "Administrador Demo",
                "admin@nexusmarket.local", UserStatus.ACTIVO);
    }
}