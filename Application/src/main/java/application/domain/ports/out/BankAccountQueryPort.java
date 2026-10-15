package application.domain.ports.out;

import java.util.List;
import java.util.Optional;

import application.domain.models.BankAccount;

/**
 * Output Port (Bank subdomain - read complement of BankAccountRepository):
 * full listing required by Consult Bank Account services.
 * Kept separate so the existing BankAccountRepository is never modified.
 */
public interface BankAccountQueryPort {

    List<BankAccount> findAllAccounts();

    Optional<BankAccount> findMarketplaceAccount();
}
