package application.adapters.in.rest.controllers;

import java.util.List;

import application.adapters.in.rest.requests.GenerateInvoiceRequest;
import application.domain.models.Invoice;
import application.domain.ports.in.ConsultInvoiceHistoryUseCase;
import application.domain.ports.in.ConsultInvoiceUseCase;
import application.domain.ports.in.GenerateInvoiceUseCase;
import application.domain.ports.in.VoidInvoiceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes invoicing endpoints.
 */
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final GenerateInvoiceUseCase generateInvoiceUseCase;
    private final ConsultInvoiceUseCase consultInvoiceUseCase;
    private final VoidInvoiceUseCase voidInvoiceUseCase;
    private final ConsultInvoiceHistoryUseCase consultInvoiceHistoryUseCase;

    public InvoiceController(GenerateInvoiceUseCase generateInvoiceUseCase,
                             ConsultInvoiceUseCase consultInvoiceUseCase,
                             VoidInvoiceUseCase voidInvoiceUseCase,
                             ConsultInvoiceHistoryUseCase consultInvoiceHistoryUseCase) {
        this.generateInvoiceUseCase = generateInvoiceUseCase;
        this.consultInvoiceUseCase = consultInvoiceUseCase;
        this.voidInvoiceUseCase = voidInvoiceUseCase;
        this.consultInvoiceHistoryUseCase = consultInvoiceHistoryUseCase;
    }

    @PostMapping
    public ResponseEntity<Invoice> generateInvoice(
            @RequestBody GenerateInvoiceRequest request) {
        return ResponseEntity.ok(
                generateInvoiceUseCase.generateInvoice(request.getOrderId()));
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<Invoice> consultInvoice(
            @PathVariable String invoiceId) {
        return ResponseEntity.ok(consultInvoiceUseCase.consultInvoice(invoiceId));
    }

    @PostMapping("/{invoiceId}/voiding")
    public ResponseEntity<Invoice> voidInvoice(
            @PathVariable String invoiceId) {
        return ResponseEntity.ok(voidInvoiceUseCase.voidInvoice(invoiceId));
    }

    @GetMapping("/buyers/{buyerId}")
    public ResponseEntity<List<Invoice>> consultInvoiceHistory(
            @PathVariable String buyerId) {
        return ResponseEntity.ok(
                consultInvoiceHistoryUseCase.consultInvoiceHistory(buyerId));
    }
}