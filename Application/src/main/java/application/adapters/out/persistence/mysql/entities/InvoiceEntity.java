package application.adapters.out.persistence.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for the invoices table (MySQL).
 */
@Entity
@Table(name = "invoices")
public class InvoiceEntity {

    @Id
    @Column(name = "invoice_id", length = 20, nullable = false, unique = true)
    private String invoiceId;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "issue_date", nullable = false)
    private LocalDateTime issueDate;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    public InvoiceEntity() {
    }

    public InvoiceEntity(String invoiceId, Integer orderId,
                         LocalDateTime issueDate, BigDecimal totalAmount,
                         String currencyCode) {
        this.invoiceId = invoiceId;
        this.orderId = orderId;
        this.issueDate = issueDate;
        this.totalAmount = totalAmount;
        this.currencyCode = currencyCode;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}