package application.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import application.domain.valueobjects.Currency;
import application.domain.valueobjects.TransferStatus;

/**
 * Transfer - Domain Model (Bank subdomain).
 *
 * Relocates monetary value between two symmetric BankAccount Domain Models
 * (source and destination) as an atomic operation: both balances mutate in
 * the same consistent business transaction/process.
 *
 * Relationships:
 * - A Transfer has one source BankAccount.
 * - A Transfer has one destination BankAccount.
 */
public class Transfer {

    private static final AtomicLong SEQUENCE = new AtomicLong(90000);

    private final String transferId;
    private final BankAccount source;
    private final BankAccount destination;
    private final BigDecimal amount;
    private final Currency currency;
    private final LocalDateTime executionDate;
    private final TransferStatus status;

    /** Created by the domain when the transfer is executed atomically. */
    public Transfer(BankAccount source, BankAccount destination,
                    BigDecimal amount, Currency currency) {
        if (source == null) {
            throw new IllegalArgumentException("Transfer source must not be null");
        }
        if (destination == null) {
            throw new IllegalArgumentException("Transfer destination must not be null");
        }
        this.transferId = "TRF-" + SEQUENCE.incrementAndGet();
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.currency = currency;
        this.executionDate = LocalDateTime.now();
        this.status = TransferStatus.EJECUTADA;
    }

    /** Reconstitution constructor (persistence). */
    public Transfer(String transferId, BankAccount source, BankAccount destination,
                    BigDecimal amount, Currency currency,
                    LocalDateTime executionDate, TransferStatus status) {
        this.transferId = transferId;
        this.source = source;
        this.destination = destination;
        this.amount = amount;
        this.currency = currency;
        this.executionDate = executionDate;
        this.status = status;
    }

    /** Unique transfer identifier. */
    public String getTransferId() {
        return transferId;
    }

    /** Account debited by the transfer. */
    public BankAccount getSource() {
        return source;
    }

    /** Account credited by the transfer. */
    public BankAccount getDestination() {
        return destination;
    }

    /** Value moved between the two accounts. */
    public BigDecimal getAmount() {
        return amount;
    }

    /** Currency of the transferred value. */
    public Currency getCurrency() {
        return currency;
    }

    /** Date and time the transfer was executed. */
    public LocalDateTime getExecutionDate() {
        return executionDate;
    }

    /** Execution outcome of the transfer. */
    public TransferStatus getStatus() {
        return status;
    }
}