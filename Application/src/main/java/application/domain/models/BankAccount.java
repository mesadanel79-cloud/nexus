package application.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import application.domain.exceptions.InsufficientBalanceException;
import application.domain.valueobjects.BankAccountStatus;
import application.domain.valueobjects.Currency;

/**
 * BankAccount - Domain Model (Bank subdomain).
 *
 * Holds the authoritative monetary balance of a marketplace participant.
 * All balance mutations must go through the domain so that invariants are
 * enforced atomically.
 *
 * Business rules (Bank subdomain):
 * - Negative balance is never allowed under any circumstances.
 * - Deposits and withdrawals require an ACTIVA account.
 * - Only a zero-balance account can be closed.
 *
 * Relationships:
 * - A BankAccount belongs to one holder (a Person Domain Model).
 * - A BankAccount may participate in zero or more Transfer instances.
 */
public class BankAccount {

    private static final AtomicLong SEQUENCE = new AtomicLong(20000);

    private final String accountNumber;
    private final Person holder;
    private final Currency currency;
    private BigDecimal currentBalance;
    private BankAccountStatus status;
    private final LocalDateTime openingDate;

    /** Created by the domain when the account is opened. */
    public BankAccount(Person holder, Currency currency, BigDecimal initialAmount) {
        if (holder == null) {
            throw new IllegalArgumentException("BankAccount holder must not be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("BankAccount currency must not be null");
        }
        if (initialAmount == null || initialAmount.signum() < 0) {
            throw new IllegalArgumentException("BankAccount initial amount must not be null or negative");
        }
        this.accountNumber = "ACC-" + SEQUENCE.incrementAndGet();
        this.holder = holder;
        this.currency = currency;
        this.currentBalance = initialAmount;
        this.status = BankAccountStatus.ACTIVA;
        this.openingDate = LocalDateTime.now();
    }

    /** Reconstitution constructor (persistence). */
    public BankAccount(String accountNumber, Person holder, Currency currency,
                       BigDecimal currentBalance, BankAccountStatus status,
                       LocalDateTime openingDate) {
        this.accountNumber = accountNumber;
        this.holder = holder;
        this.currency = currency;
        this.currentBalance = currentBalance;
        this.status = status;
        this.openingDate = openingDate;
    }

    /** Unique account number. */
    public String getAccountNumber() {
        return accountNumber;
    }

    /** Person who owns the account. */
    public Person getHolder() {
        return holder;
    }

    /** Currency in which the account holds its balance. */
    public Currency getCurrency() {
        return currency;
    }

    /** Authoritative current balance. */
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    /** Operational status of the account. */
    public BankAccountStatus getStatus() {
        return status;
    }

    /** Date and time the account was opened. */
    public LocalDateTime getOpeningDate() {
        return openingDate;
    }

    /** True when the account performs operations normally. */
    public boolean isActive() {
        return BankAccountStatus.ACTIVA.equals(status);
    }

    /**
     * Adds funds to the account. Only active accounts accept deposits.
     */
    public void deposit(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        ensureStatus(BankAccountStatus.ACTIVA,
                "Only active accounts accept deposits");
        this.currentBalance = currentBalance.add(amount);
    }

    /**
     * Removes funds from the account. Negative balance is never allowed.
     */
    public void withdraw(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        ensureStatus(BankAccountStatus.ACTIVA,
                "Only active accounts allow withdrawals");
        if (currentBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient balance in account " + accountNumber
                            + ": requested=" + amount
                            + ", available=" + currentBalance);
        }
        this.currentBalance = currentBalance.subtract(amount);
    }

    /** ACTIVA -> BLOQUEADA (suspends operations). */
    public void block() {
        ensureStatus(BankAccountStatus.ACTIVA,
                "Only active accounts can be blocked");
        this.status = BankAccountStatus.BLOQUEADA;
    }

    /** BLOQUEADA -> ACTIVA (restores operations). */
    public void unblock() {
        ensureStatus(BankAccountStatus.BLOQUEADA,
                "Only blocked accounts can be unblocked");
        this.status = BankAccountStatus.ACTIVA;
    }

    /**
     * ACTIVA/BLOQUEADA -> CERRADA. Accounts with residual balance cannot be
     * closed: funds must be withdrawn (or transferred) first.
     */
    public void close() {
        if (currentBalance.signum() != 0) {
            throw new IllegalStateException("Account " + accountNumber
                    + " cannot be closed with a non-zero balance: "
                    + currentBalance);
        }
        ensureStatus(BankAccountStatus.ACTIVA, BankAccountStatus.BLOQUEADA,
                "Only active or blocked accounts can be closed");
        this.status = BankAccountStatus.CERRADA;
    }

    private void ensureStatus(BankAccountStatus expected, String message) {
        if (!expected.equals(status)) {
            throw new IllegalStateException(message + " (current status: "
                    + status.getCode() + ")");
        }
    }

    private void ensureStatus(BankAccountStatus expected1,
                              BankAccountStatus expected2, String message) {
        if (!expected1.equals(status) && !expected2.equals(status)) {
            throw new IllegalStateException(message + " (current status: "
                    + status.getCode() + ")");
        }
    }
}