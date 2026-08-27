package application.domain.ports.in;

import application.domain.models.ShoppingCart;

/**
 * Input Port: adds a product to the buyer's shopping cart.
 */
public interface AddToCartUseCase {

    ShoppingCart addToCart(String buyerId, String productId, int quantity);

    ShoppingCart removeFromCart(String buyerId, String productId);
}