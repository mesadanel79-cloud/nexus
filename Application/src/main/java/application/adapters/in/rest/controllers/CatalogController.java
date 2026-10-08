package application.adapters.in.rest.controllers;

import java.util.List;

import application.adapters.in.rest.requests.ChangeProductStatusRequest;
import application.adapters.in.rest.requests.UpdateProductRequest;
import application.domain.models.Product;
import application.domain.ports.in.ChangeProductStatusUseCase;
import application.domain.ports.in.ConsultCatalogUseCase;
import application.domain.ports.in.UpdateProductUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes catalog consultation, product update and
 * product status endpoints.
 */
@RestController
@RequestMapping("/api/v1/products")
public class CatalogController {

    private final UpdateProductUseCase updateProductUseCase;
    private final ChangeProductStatusUseCase changeProductStatusUseCase;
    private final ConsultCatalogUseCase consultCatalogUseCase;

    public CatalogController(UpdateProductUseCase updateProductUseCase,
                             ChangeProductStatusUseCase changeProductStatusUseCase,
                             ConsultCatalogUseCase consultCatalogUseCase) {
        this.updateProductUseCase = updateProductUseCase;
        this.changeProductStatusUseCase = changeProductStatusUseCase;
        this.consultCatalogUseCase = consultCatalogUseCase;
    }

    @GetMapping
    public ResponseEntity<List<Product>> consultCatalog() {
        return ResponseEntity.ok(consultCatalogUseCase.consultPublishedProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> consultProduct(
            @PathVariable String productId) {
        return ResponseEntity.ok(consultCatalogUseCase.consultProduct(productId));
    }

    @GetMapping("/sellers/{sellerId}")
    public ResponseEntity<List<Product>> consultProductsBySeller(
            @PathVariable String sellerId) {
        return ResponseEntity.ok(
                consultCatalogUseCase.consultProductsBySeller(sellerId));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String productId,
            @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(updateProductUseCase.updateProduct(
                productId, request.getSellerId(), request.getName(),
                request.getDescription(), request.getPrice()));
    }

    @PatchMapping("/{productId}/status")
    public ResponseEntity<Product> changeProductStatus(
            @PathVariable String productId,
            @RequestBody ChangeProductStatusRequest request) {
        return ResponseEntity.ok(changeProductStatusUseCase
                .changeProductStatus(productId, request.getSellerId(),
                        request.getStatusCode()));
    }
}