package application.adapters.in.rest.controllers;

import application.adapters.in.rest.mappers.RestMapper;
import application.adapters.in.rest.requests.PublishProductRequest;
import application.adapters.in.rest.requests.RegisterSellerRequest;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.ports.in.PublishProductUseCase;
import application.domain.ports.in.RegisterSellerUseCase;
import java.math.BigDecimal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes seller and catalog endpoints.
 * Controllers never implement business rules; they delegate to the domain
 * through input ports.
 */
@RestController
@RequestMapping("/api/v1")
public class SellerController {

    private final RegisterSellerUseCase registerSellerUseCase;
    private final PublishProductUseCase publishProductUseCase;

    public SellerController(RegisterSellerUseCase registerSellerUseCase,
                            PublishProductUseCase publishProductUseCase) {
        this.registerSellerUseCase = registerSellerUseCase;
        this.publishProductUseCase = publishProductUseCase;
    }

    @PostMapping("/sellers")
    public ResponseEntity<Seller> registerSeller(
            @RequestBody RegisterSellerRequest request) {
        Seller seller = registerSellerUseCase.registerSeller(
                request.getAdministratorId(),
                request.getIdentifier(),
                request.getFullName(),
                request.getEmail());
        return ResponseEntity.ok(seller);
    }

    @PostMapping("/products")
    public ResponseEntity<Product> publishProduct(
            @RequestBody PublishProductRequest request) {
        BigDecimal price = request.getPrice();
        Product product;
        if ("DIGITAL".equalsIgnoreCase(request.getProductType())) {
            product = publishProductUseCase.publishDigitalProduct(
                    request.getSellerId(), request.getProductId(),
                    request.getName(), request.getDescription(), price);
        } else {
            product = publishProductUseCase.publishPhysicalProduct(
                    request.getSellerId(), request.getProductId(),
                    request.getName(), request.getDescription(), price);
        }
        return ResponseEntity.ok(product);
    }
}