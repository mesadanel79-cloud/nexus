package application.domain.services;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Administrator;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Person;
import application.domain.models.Seller;
import application.domain.ports.in.ChangeCustomerStatusUseCase;
import application.domain.ports.in.ConsultCustomerUseCase;
import application.domain.ports.in.RegisterBuyerUseCase;
import application.domain.ports.in.RegisterSellerUseCase;
import application.domain.ports.in.UpdateCustomerUseCase;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.OrderRepository;
import application.domain.ports.out.SellerRepository;
import application.domain.valueobjects.CommercialStatus;
import application.domain.valueobjects.SellerStatus;
import application.domain.valueobjects.UserStatus;

/**
 * Domain Service (Customer Management): coordinates the lifecycle of
 * marketplace customers (buyers and sellers).
 *
 * Business rules enforced:
 * - A buyer is registered with system access enabled (UserStatus.ACTIVO).
 * - Sellers cannot self-register: they are onboarded by an Administrator.
 * - Customer status transitions are separated between the commercial
 *   relationship (CommercialStatus / SellerStatus) and system access
 *   (UserStatus, managed by the User/Authentication subdomain).
 */
public class CustomerManagementService implements RegisterBuyerUseCase,
        RegisterSellerUseCase, ConsultCustomerUseCase, UpdateCustomerUseCase,
        ChangeCustomerStatusUseCase {

    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final OrderRepository orderRepository;
    private final Map<String, Administrator> administratorDirectory =
            new ConcurrentHashMap<>();

    public CustomerManagementService(BuyerRepository buyerRepository,
                                     SellerRepository sellerRepository,
                                     OrderRepository orderRepository) {
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
        this.orderRepository = orderRepository;
    }

    /** Registers a known administrator (onboarding authorizer). */
    public CustomerManagementService registerAdministrator(
            Administrator administrator) {
        administratorDirectory.put(administrator.getIdentifier(), administrator);
        return this;
    }

    @Override
    public Buyer registerBuyer(String identifier, String fullName,
                               String email, String mainAddress) {
        if (buyerRepository.findById(identifier).isPresent()) {
            throw new IllegalArgumentException(
                    "Buyer already registered: " + identifier);
        }
        if (buyerRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Email already registered to a buyer: " + email);
        }
        Buyer buyer = new Buyer(identifier, fullName, email,
                UserStatus.ACTIVO, mainAddress);
        return buyerRepository.save(buyer);
    }

    @Override
    public Seller registerSeller(String administratorId,
                                 String sellerIdentifier,
                                 String sellerFullName, String sellerEmail) {
        if (sellerRepository.findById(sellerIdentifier).isPresent()) {
            throw new IllegalArgumentException(
                    "Seller already registered: " + sellerIdentifier);
        }
        if (sellerRepository.findByEmail(sellerEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "Email already registered to a seller: " + sellerEmail);
        }
        Administrator administrator = requireAdministrator(administratorId);
        Seller seller = administrator.onboardSeller(sellerIdentifier,
                sellerFullName, sellerEmail);
        return sellerRepository.save(seller);
    }

    @Override
    public Person consultCustomer(String personId) {
        return buyerRepository.findById(personId)
                .<Person>map(customer -> customer)
                .or(() -> sellerRepository.findById(personId)
                        .<Person>map(customer -> customer))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found: " + personId));
    }

    @Override
    public List<Order> consultCustomerOrders(String buyerId) {
        requireBuyer(buyerId);
        return orderRepository.findByBuyerId(buyerId);
    }

    @Override
    public Person updateCustomer(String personId, String fullName,
                                 String email) {
        Person customer = consultCustomer(personId);
        ensureEmailIsAvailable(personId, email);
        customer.setFullName(fullName);
        customer.setEmail(email);
        if (customer instanceof Buyer buyer) {
            return buyerRepository.save(buyer);
        }
        return sellerRepository.save((Seller) customer);
    }

    @Override
    public Buyer updateBuyerAddress(String buyerId, String mainAddress) {
        Buyer buyer = requireBuyer(buyerId);
        buyer.setMainAddress(mainAddress);
        return buyerRepository.save(buyer);
    }

    @Override
    public Buyer changeBuyerCommercialStatus(String buyerId,
                                             String statusCode) {
        Buyer buyer = requireBuyer(buyerId);
        buyer.setCommercialStatus(CommercialStatus.fromCode(statusCode));
        return buyerRepository.save(buyer);
    }

    @Override
    public Seller changeSellerStatus(String sellerId, String statusCode) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Seller not found: " + sellerId));
        seller.setSellerStatus(SellerStatus.fromCode(statusCode));
        return sellerRepository.save(seller);
    }

    private Administrator requireAdministrator(String administratorId) {
        Administrator administrator = administratorDirectory.get(administratorId);
        if (administrator == null) {
            throw new IllegalArgumentException(
                    "Administrator not registered: " + administratorId);
        }
        return administrator;
    }

    /**
     * The e-mail address is unique across the platform: it cannot be adopted
     * by another buyer or seller (the customer's own address is accepted).
     */
    private void ensureEmailIsAvailable(String personId, String email) {
        boolean takenByBuyer = buyerRepository.findByEmail(email)
                .filter(existing -> !existing.getIdentifier().equals(personId))
                .isPresent();
        boolean takenBySeller = sellerRepository.findByEmail(email)
                .filter(existing -> !existing.getIdentifier().equals(personId))
                .isPresent();
        if (takenByBuyer || takenBySeller) {
            throw new IllegalArgumentException(
                    "Email already registered to another customer: " + email);
        }
    }

    private Buyer requireBuyer(String buyerId) {
        return buyerRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Buyer not found: " + buyerId));
    }
}