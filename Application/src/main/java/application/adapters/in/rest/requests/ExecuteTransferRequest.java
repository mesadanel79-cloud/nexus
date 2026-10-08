package application.adapters.in.rest.requests;

import java.math.BigDecimal;

/**
 * Request DTO: payload for executing a transfer between two bank accounts.
 */
public class ExecuteTransferRequest {

    private String sourceAccountNumber;
    private String destinationAccountNumber;
    private BigDecimal amount;
    private String currencyCode;

    public ExecuteTransferRequest() {
    }

    public ExecuteTransferRequest(String sourceAccountNumber,
                                  String destinationAccountNumber,
                                  BigDecimal amount, String currencyCode) {
        this.sourceAccountNumber = sourceAccountNumber;
        this.destinationAccountNumber = destinationAccountNumber;
        this.amount = amount;
        this.currencyCode = currencyCode;
    }

    public String getSourceAccountNumber() {
        return sourceAccountNumber;
    }

    public void setSourceAccountNumber(String sourceAccountNumber) {
        this.sourceAccountNumber = sourceAccountNumber;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** ISO code of the transfer currency (COP or USD). */
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}