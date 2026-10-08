package application.domain.ports.in;

import java.math.BigDecimal;
import java.util.List;

import application.domain.models.Transfer;
import application.domain.valueobjects.Currency;

/**
 * Input Port (Bank subdomain): executes atomic transfers between two bank
 * accounts and consults the transfer history of an account.
 */
public interface ExecuteTransferUseCase {

    /**
     * Moves monetary value between source and destination atomically.
     * Partial execution must never persist.
     */
    Transfer executeTransfer(String sourceAccountNumber,
                             String destinationAccountNumber,
                             BigDecimal amount, Currency currency);

    /** Transfers in which the account participated. */
    List<Transfer> consultTransfers(String accountNumber);
}