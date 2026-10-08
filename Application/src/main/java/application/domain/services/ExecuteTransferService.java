package application.domain.services;

import java.math.BigDecimal;
import java.util.List;

import application.domain.exceptions.BankAccountNotFoundException;
import application.domain.exceptions.InvalidTransferException;
import application.domain.models.BankAccount;
import application.domain.models.Transfer;
import application.domain.ports.in.ExecuteTransferUseCase;
import application.domain.ports.out.BankAccountRepository;
import application.domain.valueobjects.Currency;

/**
 * Domain Service (Bank subdomain): executes transfers between two symmetric
 * BankAccount Domain Models.
 *
 * Business rules enforced:
 * - A transfer is atomic: partial execution must never persist.
 * - Source and destination must be different accounts.
 * - Both accounts must be active and denominated in the transfer currency.
 * - Negative balance on the source is never allowed.
 */
public class ExecuteTransferService implements ExecuteTransferUseCase {

    private final BankAccountRepository bankAccountRepository;

    public ExecuteTransferService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Transfer executeTransfer(String sourceAccountNumber,
                                    String destinationAccountNumber,
                                    BigDecimal amount, Currency currency) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Transfer currency must not be null");
        }
        if (sourceAccountNumber.equals(destinationAccountNumber)) {
            throw new InvalidTransferException(
                    "Source and destination accounts must be different");
        }
        BankAccount source = requireAccount(sourceAccountNumber);
        BankAccount destination = requireAccount(destinationAccountNumber);
        if (!source.isActive()) {
            throw new InvalidTransferException(
                    "Source account is not active: " + sourceAccountNumber);
        }
        if (!destination.isActive()) {
            throw new InvalidTransferException(
                    "Destination account is not active: " + destinationAccountNumber);
        }
        if (!source.getCurrency().equals(currency)) {
            throw new InvalidTransferException(
                    "Source currency " + source.getCurrency().getCode()
                            + " does not match transfer currency "
                            + currency.getCode());
        }
        if (!destination.getCurrency().equals(currency)) {
            throw new InvalidTransferException(
                    "Destination currency " + destination.getCurrency().getCode()
                            + " does not match transfer currency "
                            + currency.getCode());
        }
        // Mutations are validated up front: after the guard above, both
        // mutations succeed and no partial execution can persist.
        source.withdraw(amount);
        destination.deposit(amount);
        Transfer transfer = new Transfer(source, destination, amount, currency);
        bankAccountRepository.save(source);
        bankAccountRepository.save(destination);
        return bankAccountRepository.saveTransfer(transfer);
    }

    @Override
    public List<Transfer> consultTransfers(String accountNumber) {
        requireAccount(accountNumber);
        return bankAccountRepository.findTransfersFor(accountNumber);
    }

    private BankAccount requireAccount(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found: " + accountNumber));
    }
}