package application.domain.ports.in;

import java.util.List;

import application.domain.models.Invoice;

/**
 * Input Port (Invoicing Management): retrieves the invoices associated with
 * a customer according to the requesting user's permissions.
 */
public interface ConsultInvoiceHistoryUseCase {

    List<Invoice> consultInvoiceHistory(String buyerId);
}