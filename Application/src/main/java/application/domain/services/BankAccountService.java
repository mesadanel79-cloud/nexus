package application.domain.services;

import java.math.BigDecimal;

import application.domain.exceptions.BankAccountNotFoundException;
import application.domain.models.BankAccount;
import application.domain.models.Person;
import application.domain.ports.in.BankAccountUseCase;
import application.domain.ports.out.BankAccountRepository;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.SellerRepository;
import application.domain.valueobjects.Currency;

/**
 * Domain Service (Bank subdomain): manages the lifecycle and balance of a
 * bank account.
 *
 * Business rules enforced:
 * - Only one active account per holder+currency is allowed.
 * - Deposits and withdrawals require an active account.
 * - Negative balance is never allowed under any circumstances.
 * - Only zero-balance accounts can be closed.
 */
public class BankAccountService implements BankAccountUseCase {

    private final BankAccountRepository bankAccountRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    public BankAccountService(BankAccountRepository bankAccountRepository,
                              BuyerRepository buyerRepository,
                              SellerRepository sellerRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public BankAccount openAccount(String holderId, Currency currency,
                                   BigDecimal initialAmount) {
        Person holder = resolveHolder(holderId);
        boolean alreadyOpen = bankAccountRepository.findByHolderId(holderId)
                .stream()
                .anyMatch(account -> account.getCurrency().equals(currency)
                        && account.isActive());
        if (alreadyOpen) {
            throw new IllegalStateException("Holder " + holderId
                    + " already has an active account in "
                    + currency.getCode());
        }
        BankAccount account = new BankAccount(holder, currency, initialAmount);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount consultAccount(String accountNumber) {
        return requireAccount(accountNumber);
    }

    @Override
    public BankAccount deposit(String accountNumber, BigDecimal amount) {
        BankAccount account = requireAccount(accountNumber);
        account.deposit(amount);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount withdraw(String accountNumber, BigDecimal amount) {
        BankAccount account = requireAccount(accountNumber);
        account.withdraw(amount);
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount blockAccount(String accountNumber) {
        BankAccount account = requireAccount(accountNumber);
        account.block();
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount unblockAccount(String accountNumber) {
        BankAccount account = requireAccount(accountNumber);
        account.unblock();
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount closeAccount(String accountNumber) {
        BankAccount account = requireAccount(accountNumber);
        account.close();
        return bankAccountRepository.save(account);
    }

    private BankAccount requireAccount(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found: " + accountNumber));
    }

    /** Resolves the holder across buyers and sellers (the Person subtypes). */
    private Person resolveHolder(String holderId) {
        return buyerRepository.findById(holderId)
                .<Person>map(holder -> holder)
                .or(() -> sellerRepository.findById(holderId)
                        .<Person>map(holder -> holder))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Holder not found: " + holderId));
    }
}