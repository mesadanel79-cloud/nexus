package application.adapters.in.rest.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO: standardized API representation of an order. Hides the
 * internal implementation of the domain.
 */
public class OrderResponse {

    private Integer orderId;
    private String buyerId;
    private String orderStatusCode;
    private LocalDateTime creationDate;
    private LocalDateTime paymentConfirmationDate;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String invoiceId;

    public OrderResponse() {
    }

    public OrderResponse(Integer orderId, String buyerId, String orderStatusCode,
                         LocalDateTime creationDate,
                         LocalDateTime paymentConfirmationDate,
                         String deliveryAddress, BigDecimal totalAmount,
                         String invoiceId) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.orderStatusCode = orderStatusCode;
        this.creationDate = creationDate;
        this.paymentConfirmationDate = paymentConfirmationDate;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.invoiceId = invoiceId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getOrderStatusCode() {
        return orderStatusCode;
    }

    public void setOrderStatusCode(String orderStatusCode) {
        this.orderStatusCode = orderStatusCode;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getPaymentConfirmationDate() {
        return paymentConfirmationDate;
    }

    public void setPaymentConfirmationDate(LocalDateTime paymentConfirmationDate) {
        this.paymentConfirmationDate = paymentConfirmationDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}