package application.domain.ports.in;

import application.domain.models.Invoice;

/**
 * Input Port (Invoicing Management): cancels a previously generated invoice
 * and records the corresponding business operation.
 */
public interface VoidInvoiceUseCase {

    Invoice voidInvoice(String invoiceId);
}