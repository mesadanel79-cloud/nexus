package application.domain.services;

import java.math.BigDecimal;

import application.domain.exceptions.BankAccountBlockedException;
import application.domain.exceptions.BankAccountNotFoundException;
import application.domain.exceptions.InvalidAmountException;
import application.domain.models.BankAccount;
import application.domain.ports.in.BankDepositWithdrawUseCase;
import application.domain.ports.out.BankAccountRepository;

/**
 * Domain Service (Bank subdomain - bank-services Deposit / Withdraw
 * Funds): settlement-relevant mutations.
 *
 * Business rules enforced:
 * - Amount &gt; 0 (compareTo, never equals for BigDecimal).
 * - Account must be ACTIVA; otherwise BankAccountBlockedException.
 * - Withdrawal never produces negative balance
 *   (InsufficientBalanceException from the model).
 */
public class BankDepositWithdrawService implements BankDepositWithdrawUseCase {

    private final BankAccountRepository bankAccountRepository;

    public BankDepositWithdrawService(BankAccountRepository bankAccountRepository) {
        if (bankAccountRepository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public BankAccount depositFunds(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be positive: " + amount);
        }
        BankAccount account = requireActive(accountNumber);
        account.deposit(amount);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount withdrawFunds(String accountNumber, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be positive: " + amount);
        }
        BankAccount account = requireActive(accountNumber);
        account.withdraw(amount);
        return bankAccountRepository.save(account);
    }

    private BankAccount requireActive(String accountNumber) {
        BankAccount account = bankAccountRepository
                .findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found: " + accountNumber));
        if (!account.isActive()) {
            throw new BankAccountBlockedException(
                    "Account is not active: " + accountNumber
                            + " (current: " + account.getStatus().getCode() + ")");
        }
        return account;
    }
}
