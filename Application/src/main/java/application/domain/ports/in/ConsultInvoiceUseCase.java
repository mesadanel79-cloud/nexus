package application.domain.ports.in;

import application.domain.models.Invoice;

/**
 * Input Port (Invoicing Management): retrieves information about an invoice
 * according to the requesting user's permissions.
 */
public interface ConsultInvoiceUseCase {

    Invoice consultInvoice(String invoiceId);
}