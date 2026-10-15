package application.domain.ports.in;

import application.domain.models.BankAccount;
import application.domain.valueobjects.Currency;

import java.math.BigDecimal;

/**
 * Input Port (Bank subdomain - bank-services Open Bank Account):
 * creates a new bank account for a marketplace participant.
 */
public interface OpenBankAccountUseCase {

    BankAccount openBankAccount(String holderId, Currency currency,
                                BigDecimal initialAmount);
}
