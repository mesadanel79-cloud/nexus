package application.domain.services;

import application.domain.exceptions.BankAccountNotFoundException;
import application.domain.models.BankAccount;
import application.domain.ports.in.BankAccountStatusUseCase;
import application.domain.ports.out.BankAccountRepository;

/**
 * Domain Service (Bank subdomain - bank-services Block / Unblock /
 * Close): lifecycle ACTIVA &lt;-&gt; BLOQUEADA -&gt; CERRADA.
 *
 * Business rules enforced:
 * - Block: only ACTIVA -&gt; BLOQUEADA.
 * - Unblock: only BLOQUEADA -&gt; ACTIVA.
 * - Close: only zero-balance accounts (model enforces signum == 0).
 */
public class BankAccountStatusService implements BankAccountStatusUseCase {

    private final BankAccountRepository bankAccountRepository;

    public BankAccountStatusService(BankAccountRepository bankAccountRepository) {
        if (bankAccountRepository == null) {
            throw new IllegalArgumentException("Repository must not be null");
        }
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public BankAccount blockBankAccount(String accountNumber) {
        BankAccount account = requireAccount(accountNumber);
        account.block();
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount unblockBankAccount(String accountNumber) {
        BankAccount account = requireAccount(accountNumber);
        account.unblock();
        return bankAccountRepository.save(account);
    }

    @Override
    public BankAccount closeBankAccount(String accountNumber) {
        BankAccount account = requireAccount(accountNumber);
        account.close();
        return bankAccountRepository.save(account);
    }

    private BankAccount requireAccount(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new BankAccountNotFoundException(
                        "Bank account not found: " + accountNumber));
    }
}
