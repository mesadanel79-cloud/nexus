package application.domain.ports.in;

import application.domain.models.Buyer;

/**
 * Input Port (Customer Management): registers a new marketplace customer
 * representing a natural person who purchases products (buyer).
 */
public interface RegisterBuyerUseCase {

    Buyer registerBuyer(String identifier, String fullName, String email,
                        String mainAddress);
}