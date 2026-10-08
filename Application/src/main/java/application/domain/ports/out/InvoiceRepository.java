package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.Invoice;

/**
 * Output Port (Invoicing subdomain): persistence contract for invoices.
 */
public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(String invoiceId);

    /** Invoices associated with a buyer. */
    List<Invoice> findByBuyerId(String buyerId);

    List<Invoice> findAll();
}