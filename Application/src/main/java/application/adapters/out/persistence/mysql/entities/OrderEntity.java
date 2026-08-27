package application.adapters.out.persistence.mysql.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * JPA Entity for the orders table (MySQL).
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @Column(name = "order_id", nullable = false, unique = true)
    private Integer orderId;

    @Column(name = "buyer_identifier", nullable = false)
    private String buyerIdentifier;

    @Column(name = "order_status_code", nullable = false)
    private String orderStatusCode;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "payment_confirmation_date")
    private LocalDateTime paymentConfirmationDate;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    public OrderEntity() {
    }

    public OrderEntity(Integer orderId, String buyerIdentifier,
                       String orderStatusCode, LocalDateTime creationDate,
                       LocalDateTime paymentConfirmationDate,
                       String deliveryAddress, BigDecimal totalAmount) {
        this.orderId = orderId;
        this.buyerIdentifier = buyerIdentifier;
        this.orderStatusCode = orderStatusCode;
        this.creationDate = creationDate;
        this.paymentConfirmationDate = paymentConfirmationDate;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getBuyerIdentifier() {
        return buyerIdentifier;
    }

    public void setBuyerIdentifier(String buyerIdentifier) {
        this.buyerIdentifier = buyerIdentifier;
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
}