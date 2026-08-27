package application.adapters.in.rest.controllers;

import application.adapters.in.rest.mappers.RestMapper;
import application.adapters.in.rest.requests.AddToCartRequest;
import application.adapters.in.rest.requests.CreateOrderRequest;
import application.adapters.in.rest.responses.OrderResponse;
import application.domain.models.Order;
import application.domain.models.ShoppingCart;
import application.domain.ports.in.AddToCartUseCase;
import application.domain.ports.in.ConfirmPaymentUseCase;
import application.domain.ports.in.PlaceOrderUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes cart and order lifecycle endpoints.
 */
@RestController
@RequestMapping("/api/v1")
public class OrderController {

    private final AddToCartUseCase addToCartUseCase;
    private final PlaceOrderUseCase placeOrderUseCase;
    private final ConfirmPaymentUseCase confirmPaymentUseCase;

    public OrderController(AddToCartUseCase addToCartUseCase,
                           PlaceOrderUseCase placeOrderUseCase,
                           ConfirmPaymentUseCase confirmPaymentUseCase) {
        this.addToCartUseCase = addToCartUseCase;
        this.placeOrderUseCase = placeOrderUseCase;
        this.confirmPaymentUseCase = confirmPaymentUseCase;
    }

    @PostMapping("/cart/items")
    public ResponseEntity<ShoppingCart> addToCart(
            @RequestBody AddToCartRequest request) {
        ShoppingCart cart = addToCartUseCase.addToCart(
                request.getBuyerId(), request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> placeOrder(
            @RequestBody CreateOrderRequest request) {
        Order order = placeOrderUseCase.placeOrder(
                request.getBuyerId(), request.getDeliveryAddress());
        return ResponseEntity.ok(RestMapper.toOrderResponse(order));
    }

    @PostMapping("/orders/{orderId}/payment")
    public ResponseEntity<OrderResponse> confirmPayment(
            @org.springframework.web.bind.annotation.PathVariable Integer orderId) {
        Order order = confirmPaymentUseCase.confirmPayment(orderId);
        return ResponseEntity.ok(RestMapper.toOrderResponse(order));
    }
}