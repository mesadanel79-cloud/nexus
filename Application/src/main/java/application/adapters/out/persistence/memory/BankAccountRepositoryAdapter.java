package application.adapters.out.persistence.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import application.domain.models.BankAccount;
import application.domain.models.Transfer;
import application.domain.ports.out.BankAccountRepository;
import org.springframework.stereotype.Component;

/**
 * Output Adapter (in-memory): development implementation of the
 * BankAccountRepository port. Adapters implement ports but never define
 * business rules. Replaceable by a MySQL adapter without touching the
 * domain.
 */
@Component
public class BankAccountRepositoryAdapter implements BankAccountRepository {

    private final Map<String, BankAccount> accounts = new ConcurrentHashMap<>();
    private final Map<String, List<Transfer>> transfers = new ConcurrentHashMap<>();

    @Override
    public BankAccount save(BankAccount account) {
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    @Override
    public Transfer saveTransfer(Transfer transfer) {
        transfers.computeIfAbsent(transfer.getSource().getAccountNumber(),
                        key -> new java.util.ArrayList<>())
                .add(transfer);
        transfers.computeIfAbsent(transfer.getDestination().getAccountNumber(),
                        key -> new java.util.ArrayList<>())
                .add(transfer);
        return transfer;
    }

    @Override
    public Optional<BankAccount> findByAccountNumber(String accountNumber) {
        return Optional.ofNullable(accounts.get(accountNumber));
    }

    @Override
    public List<BankAccount> findByHolderId(String personId) {
        return accounts.values().stream()
                .filter(account -> account.getHolder().getIdentifier()
                        .equals(personId))
                .toList();
    }

    @Override
    public List<Transfer> findTransfersFor(String accountNumber) {
        return List.copyOf(
                transfers.getOrDefault(accountNumber, List.of()));
    }
}