package application.domain.ports.in;

import application.domain.models.Invoice;

/**
 * Input Port (Invoicing Management): creates an invoice for a confirmed
 * (paid) order and establishes its initial state.
 */
public interface GenerateInvoiceUseCase {

    Invoice generateInvoice(Integer orderId);
}