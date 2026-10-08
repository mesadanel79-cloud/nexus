package application.adapters.in.rest.requests;

import java.math.BigDecimal;

/**
 * Request DTO: payload for a balance mutation on a bank account
 * (deposit or withdrawal).
 */
public class BankAccountAmountRequest {

    private String accountNumber;
    private BigDecimal amount;

    public BankAccountAmountRequest() {
    }

    public BankAccountAmountRequest(String accountNumber, BigDecimal amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}