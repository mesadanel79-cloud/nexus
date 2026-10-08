package application.domain.ports.in;

import java.math.BigDecimal;

import application.domain.models.BankAccount;
import application.domain.valueobjects.Currency;

/**
 * Input Port (Bank subdomain): manages the lifecycle and balance of a
 * bank account (opening, consultation, deposits, withdrawals and status
 * changes).
 */
public interface BankAccountUseCase {

    BankAccount openAccount(String holderId, Currency currency,
                            BigDecimal initialAmount);

    BankAccount consultAccount(String accountNumber);

    BankAccount deposit(String accountNumber, BigDecimal amount);

    BankAccount withdraw(String accountNumber, BigDecimal amount);

    BankAccount blockAccount(String accountNumber);

    BankAccount unblockAccount(String accountNumber);

    BankAccount closeAccount(String accountNumber);
}