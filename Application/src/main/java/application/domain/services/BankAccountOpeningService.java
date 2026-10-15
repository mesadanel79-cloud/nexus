package application.domain.services;

import java.math.BigDecimal;
import java.util.List;

import application.domain.exceptions.BankAccountNotFoundException;
import application.domain.exceptions.DuplicateBankAccountException;
import application.domain.exceptions.InvalidAmountException;
import application.domain.models.BankAccount;
import application.domain.models.Person;
import application.domain.ports.in.ConsultBankAccountUseCase;
import application.domain.ports.in.OpenBankAccountUseCase;
import application.domain.ports.out.BankAccountQueryPort;
import application.domain.ports.out.BankAccountRepository;
import application.domain.ports.out.BuyerRepository;
import application.domain.ports.out.SellerRepository;
import application.domain.valueobjects.Currency;

/**
 * Domain Service (Bank subdomain - bank-services Open / Consult Bank
 * Account): creates accounts with initial credit and consults them by
 * number or holder.
 *
 * Business rules enforced:
 * - Only one active account per holder+currency is allowed.
 * - Initial amount &gt;= 0; currency must be supported (COP, USD).
 * - Created with status ACTIVA and currentBalance = initialAmount.
 */
public class BankAccountOpeningService implements OpenBankAccountUseCase,
        ConsultBankAccountUseCase {

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountQueryPort bankAccountQueryPort;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;

    public BankAccountOpeningService(BankAccountRepository bankAccountRepository,
                                     BankAccountQueryPort bankAccountQueryPort,
                                     BuyerRepository buyerRepository,
                                     SellerRepository sellerRepository) {
        if (bankAccountRepository == null || buyerRepository == null
                || sellerRepository == null) {
            throw new IllegalArgumentException("Repositories must not be null");
        }
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountQueryPort = bankAccountQueryPort;
        this.buyerRepository = buyerRepository;
        this.sellerRepository = sellerRepository;
    }

    @Override
    public BankAccount openBankAccount(String holderId, Currency currency,
                                       BigDecimal initialAmount) {
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        if (initialAmount == null || initialAmount.signum() < 0) {
            throw new InvalidAmountException(
                    "Initial amount must not be null or negative: " + initialAmount);
        }
        Person holder = resolveHolder(holderId);
        boolean alreadyOpen = bankAccountRepository.findByHolderId(holderId)
                .stream()
                .anyMatch(account -> account.getCurrency().equals(currency)
                        && account.isActive());
        if (alreadyOpen) {
            throw new DuplicateBankAccountException("Holder " + holderId
                    + " already has an active account in " + currency.getCode());
        }
        return bankAccountRepository.save(
                new BankAccount(holder, currency, initialAmount));
    }

    @Override
    public BankAccount consultBankAccount(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found: " + accountNumber));
    }

    @Override
    public List<BankAccount> consultAccountsByHolder(String holderId) {
        resolveHolder(holderId);
        return bankAccountRepository.findByHolderId(holderId);
    }

    @Override
    public List<BankAccount> consultBankAccounts() {
        if (bankAccountQueryPort != null) {
            return bankAccountQueryPort.findAllAccounts();
        }
        return List.of();
    }

    private Person resolveHolder(String holderId) {
        return buyerRepository.findById(holderId)
                .<Person>map(holder -> holder)
                .or(() -> sellerRepository.findById(holderId)
                        .<Person>map(holder -> holder))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Holder not found: " + holderId));
    }
}
