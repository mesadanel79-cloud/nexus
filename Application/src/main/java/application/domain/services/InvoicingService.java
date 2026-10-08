package application.domain.services;

import java.util.List;

import application.domain.models.Invoice;
import application.domain.models.Order;
import application.domain.ports.in.ConsultInvoiceHistoryUseCase;
import application.domain.ports.in.ConsultInvoiceUseCase;
import application.domain.ports.in.GenerateInvoiceUseCase;
import application.domain.ports.in.VoidInvoiceUseCase;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.InvoiceRepository;
import application.domain.ports.out.OrderRepository;
import application.domain.valueobjects.OrderStatus;

/**
 * Domain Service (Invoicing Management): generates, consults and voids the
 * invoices associated with confirmed orders.
 *
 * Business rules enforced:
 * - An invoice is generated only for a PAID order.
 * - Only issued (EMITIDA) invoices can be voided.
 * - Invoice history is consulted per customer.
 */
public class InvoicingService implements GenerateInvoiceUseCase,
        ConsultInvoiceUseCase, VoidInvoiceUseCase,
        ConsultInvoiceHistoryUseCase {

    private final OrderRepository orderRepository;
    private final InvoiceRepository invoiceRepository;
    private final BuyerRepository buyerRepository;

    public InvoicingService(OrderRepository orderRepository,
                            InvoiceRepository invoiceRepository,
                            BuyerRepository buyerRepository) {
        this.orderRepository = orderRepository;
        this.invoiceRepository = invoiceRepository;
        this.buyerRepository = buyerRepository;
    }

    @Override
    public Invoice generateInvoice(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Order not found: " + orderId));
        if (!OrderStatus.PAGADO.equals(order.getOrderStatus())) {
            throw new IllegalStateException(
                    "Invoices are generated only for paid orders (current: "
                            + order.getOrderStatus().getCode() + ")");
        }
        Invoice invoice = order.getInvoice() != null
                ? order.getInvoice()
                : new Invoice(order);
        orderRepository.save(order);
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice consultInvoice(String invoiceId) {
        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invoice not found: " + invoiceId));
    }

    @Override
    public Invoice voidInvoice(String invoiceId) {
        Invoice invoice = consultInvoice(invoiceId);
        invoice.voidInvoice();
        return invoiceRepository.save(invoice);
    }

    @Override
    public List<Invoice> consultInvoiceHistory(String buyerId) {
        if (buyerRepository.findById(buyerId).isEmpty()) {
            throw new IllegalArgumentException("Buyer not found: " + buyerId);
        }
        return invoiceRepository.findByBuyerId(buyerId);
    }
}