package application.adapters.in.rest.requests;

import java.math.BigDecimal;

/**
 * Request DTO: payload for opening a bank account.
 * DTOs never enter the domain layer and contain no business logic.
 */
public class OpenBankAccountRequest {

    private String holderId;
    private String currencyCode;
    private BigDecimal initialAmount;

    public OpenBankAccountRequest() {
    }

    public OpenBankAccountRequest(String holderId, String currencyCode,
                                  BigDecimal initialAmount) {
        this.holderId = holderId;
        this.currencyCode = currencyCode;
        this.initialAmount = initialAmount;
    }

    public String getHolderId() {
        return holderId;
    }

    public void setHolderId(String holderId) {
        this.holderId = holderId;
    }

    /** ISO code of the account currency (COP or USD). */
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }
}