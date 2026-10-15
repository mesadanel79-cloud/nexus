package application.domain.services;

import java.math.BigDecimal;
import java.util.List;

import application.domain.exceptions.BankAccountCurrencyMismatchException;
import application.domain.exceptions.BankAccountNotFoundException;
import application.domain.exceptions.InvalidAmountException;
import application.domain.exceptions.InvalidTransferException;
import application.domain.models.BankAccount;
import application.domain.models.Transfer;
import application.domain.ports.in.BankSettlementUseCase;
import application.domain.ports.in.ConsultTransferHistoryUseCase;
import application.domain.ports.out.BankAccountRepository;
import application.domain.valueobjects.Currency;

/**
 * Domain Service (Bank subdomain - settlement layer):
 *
 * Order Payment (Invoicing) -&gt; Buyer pays into Marketplace account.
 * Seller Payout (Invoicing) -&gt; Marketplace pays into Seller account.
 * Refund (Returns/Refunds)  -&gt; Marketplace pays back into Buyer account.
 *
 * A transfer is atomic: both balances mutate in the same consistent
 * business transaction; partial execution must never persist.
 */
public class BankSettlementService implements BankSettlementUseCase,
        ConsultTransferHistoryUseCase {

    private final BankAccountRepository bankAccountRepository;

    public BankSettlementService(BankAccountRepository bankAccountRepository) {
        if (bankAccountRepository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public Transfer settleOrderPayment(String buyerAccountNumber,
                                       String marketplaceAccountNumber,
                                       BigDecimal amount, Currency currency) {
        return executeSettlement(buyerAccountNumber, marketplaceAccountNumber,
                amount, currency, "order payment");
    }

    @Override
    public Transfer settleSellerPayout(String marketplaceAccountNumber,
                                       String sellerAccountNumber,
                                       BigDecimal amount, Currency currency) {
        return executeSettlement(marketplaceAccountNumber, sellerAccountNumber,
                amount, currency, "seller payout");
    }

    @Override
    public Transfer settleRefundPayment(String marketplaceAccountNumber,
                                        String buyerAccountNumber,
                                        BigDecimal amount, Currency currency) {
        return executeSettlement(marketplaceAccountNumber, buyerAccountNumber,
                amount, currency, "refund payment");
    }

    @Override
    public List<Transfer> consultTransferHistory(String accountNumber) {
        requireAccount(accountNumber);
        return bankAccountRepository.findTransfersFor(accountNumber);
    }

    private Transfer executeSettlement(String sourceNumber, String destNumber,
                                       BigDecimal amount, Currency currency,
                                       String kind) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(
                    "Settlement amount must be positive for " + kind + ": " + amount);
        }
        if (currency == null) {
            throw new IllegalArgumentException(
                    "Settlement currency must not be null for " + kind);
        }
        if (sourceNumber.equals(destNumber)) {
            throw new InvalidTransferException(
                    "Source and destination must be different for " + kind);
        }
        BankAccount source = requireAccount(sourceNumber);
        BankAccount destination = requireAccount(destNumber);
        if (!source.isActive()) {
            throw new InvalidTransferException(
                    "Source account is not active for " + kind + ": " + sourceNumber);
        }
        if (!destination.isActive()) {
            throw new InvalidTransferException(
                    "Destination account is not active for " + kind + ": " + destNumber);
        }
        if (!source.getCurrency().equals(currency)
                || !destination.getCurrency().equals(currency)) {
            throw new BankAccountCurrencyMismatchException(
                    "Currency mismatch for " + kind + ": expected "
                            + currency.getCode());
        }
        source.withdraw(amount);
        destination.deposit(amount);
        Transfer transfer = new Transfer(source, destination, amount, currency);
        bankAccountRepository.save(source);
        bankAccountRepository.save(destination);
        return bankAccountRepository.saveTransfer(transfer);
    }

    private BankAccount requireAccount(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found: " + accountNumber));
    }
}
