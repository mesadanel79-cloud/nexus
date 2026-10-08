package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.Invoice;
import application.domain.ports.out.InvoiceRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * InvoiceRepository port. Adapters implement ports but never define
 * business rules.
 */
@Component
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final Map<String, Invoice> invoices = new ConcurrentHashMap<>();

    @Override
    public Invoice save(Invoice invoice) {
        invoices.put(invoice.getInvoiceId(), invoice);
        return invoice;
    }

    @Override
    public Optional<Invoice> findById(String invoiceId) {
        return Optional.ofNullable(invoices.get(invoiceId));
    }

    @Override
    public List<Invoice> findByBuyerId(String buyerId) {
        return invoices.values().stream()
                .filter(invoice -> invoice.getOrder().getBuyer()
                        .getIdentifier().equals(buyerId))
                .toList();
    }

    @Override
    public List<Invoice> findAll() {
        return List.copyOf(invoices.values());
    }
}