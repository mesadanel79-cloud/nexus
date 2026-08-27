package application.domain.ports.in;

import application.domain.models.Seller;

/**
 * Input Port: registers a new seller. Sellers cannot self-register; an
 * administrator performs the onboarding.
 */
public interface RegisterSellerUseCase {

    Seller registerSeller(String administratorId, String sellerIdentifier,
                          String sellerFullName, String sellerEmail);
}