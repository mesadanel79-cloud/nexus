package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.BankAccount;
import application.domain.models.Transfer;

/**
 * Output Port (Bank subdomain): persistence contract for bank accounts and
 * their transfer history.
 * The domain owns this interface; adapters implement it.
 */
public interface BankAccountRepository {

    BankAccount save(BankAccount account);

    Transfer saveTransfer(Transfer transfer);

    Optional<BankAccount> findByAccountNumber(String accountNumber);

    /** Accounts owned by a given person identifier. */
    List<BankAccount> findByHolderId(String personId);

    /** Transfers in which the account participated (as source or destination). */
    List<Transfer> findTransfersFor(String accountNumber);
}