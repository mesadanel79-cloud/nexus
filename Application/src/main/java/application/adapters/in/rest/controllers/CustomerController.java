package application.adapters.in.rest.controllers;

import java.util.List;

import application.adapters.in.rest.requests.ChangeStatusRequest;
import application.adapters.in.rest.requests.RegisterBuyerRequest;
import application.adapters.in.rest.requests.UpdateCustomerRequest;
import application.domain.models.Buyer;
import application.domain.models.Order;
import application.domain.models.Person;
import application.domain.ports.in.ChangeCustomerStatusUseCase;
import application.domain.ports.in.ConsultCustomerUseCase;
import application.domain.ports.in.RegisterBuyerUseCase;
import application.domain.ports.in.UpdateCustomerUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes customer management endpoints.
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final RegisterBuyerUseCase registerBuyerUseCase;
    private final ConsultCustomerUseCase consultCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final ChangeCustomerStatusUseCase changeCustomerStatusUseCase;

    public CustomerController(RegisterBuyerUseCase registerBuyerUseCase,
                              ConsultCustomerUseCase consultCustomerUseCase,
                              UpdateCustomerUseCase updateCustomerUseCase,
                              ChangeCustomerStatusUseCase changeCustomerStatusUseCase) {
        this.registerBuyerUseCase = registerBuyerUseCase;
        this.consultCustomerUseCase = consultCustomerUseCase;
        this.updateCustomerUseCase = updateCustomerUseCase;
        this.changeCustomerStatusUseCase = changeCustomerStatusUseCase;
    }

    @PostMapping("/buyers")
    public ResponseEntity<Buyer> registerBuyer(
            @RequestBody RegisterBuyerRequest request) {
        Buyer buyer = registerBuyerUseCase.registerBuyer(
                request.getIdentifier(), request.getFullName(),
                request.getEmail(), request.getMainAddress());
        return ResponseEntity.ok(buyer);
    }

    @GetMapping("/{personId}")
    public ResponseEntity<Person> consultCustomer(
            @PathVariable String personId) {
        return ResponseEntity.ok(
                consultCustomerUseCase.consultCustomer(personId));
    }

    @GetMapping("/{buyerId}/orders")
    public ResponseEntity<List<Order>> consultCustomerOrders(
            @PathVariable String buyerId) {
        return ResponseEntity.ok(
                consultCustomerUseCase.consultCustomerOrders(buyerId));
    }

    @PutMapping("/{personId}")
    public ResponseEntity<Person> updateCustomer(
            @PathVariable String personId,
            @RequestBody UpdateCustomerRequest request) {
        return ResponseEntity.ok(updateCustomerUseCase.updateCustomer(
                personId, request.getFullName(), request.getEmail()));
    }

    @PatchMapping("/buyers/{buyerId}/status")
    public ResponseEntity<Buyer> changeBuyerCommercialStatus(
            @PathVariable String buyerId,
            @RequestBody ChangeStatusRequest request) {
        return ResponseEntity.ok(changeCustomerStatusUseCase
                .changeBuyerCommercialStatus(buyerId,
                        request.getStatusCode()));
    }

    @PatchMapping("/sellers/{sellerId}/status")
    public ResponseEntity<application.domain.models.Seller> changeSellerStatus(
            @PathVariable String sellerId,
            @RequestBody ChangeStatusRequest request) {
        return ResponseEntity.ok(changeCustomerStatusUseCase
                .changeSellerStatus(sellerId, request.getStatusCode()));
    }
}