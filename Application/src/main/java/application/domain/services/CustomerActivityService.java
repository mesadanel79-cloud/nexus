package application.domain.services;

import java.util.List;

import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Product;
import application.domain.models.Shipment;
import application.domain.ports.in.ConsultCustomerProductsUseCase;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.OrderRepository;
import application.domain.ports.out.ProductRepository;
import application.domain.ports.out.SellerRepository;
import application.domain.ports.out.ShipmentRepository;

/**
 * Domain Service (Customer Management - SDD Consult Customer Products):
 * retrieves the marketplace products and activity associated with a
 * customer (listed catalog items, orders, shipments).
 *
 * Read-only service: it never mutates the domain, only resolves through
 * output ports owned by the domain.
 */
public class CustomerActivityService implements ConsultCustomerProductsUseCase {

    private final SellerRepository sellerRepository;
    private final BuyerRepository buyerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ShipmentRepository shipmentRepository;

    public CustomerActivityService(SellerRepository sellerRepository,
                                   BuyerRepository buyerRepository,
                                   ProductRepository productRepository,
                                   OrderRepository orderRepository,
                                   ShipmentRepository shipmentRepository) {
        if (sellerRepository == null || buyerRepository == null
                || productRepository == null || orderRepository == null
                || shipmentRepository == null) {
            throw new IllegalArgumentException("Repositories must not be null");
        }
        this.sellerRepository = sellerRepository;
        this.buyerRepository = buyerRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public List<Product> consultCustomerProducts(String sellerId) {
        sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Seller not found: " + sellerId));
        return productRepository.findAll().stream()
                .filter(product -> product.getSeller().getIdentifier().equals(sellerId))
                .toList();
    }

    @Override
    public List<Shipment> consultCustomerShipments(String buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Buyer not found: " + buyerId));
        List<Integer> orderIds = orderRepository.findByBuyerId(buyer.getIdentifier())
                .stream().map(Order::getOrderId).toList();
        return shipmentRepository.findAll().stream()
                .filter(shipment -> orderIds.contains(
                        shipment.getOrder().getOrderId()))
                .toList();
    }

    @Override
    public List<Order> consultCustomerActivity(String buyerId) {
        buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Buyer not found: " + buyerId));
        return orderRepository.findByBuyerId(buyerId);
    }
}
