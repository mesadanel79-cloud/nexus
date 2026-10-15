package application.domain.ports.in;

import application.domain.models.BankAccount;

/**
 * Input Port (Bank subdomain - bank-services Block / Unblock / Close):
 * lifecycle of the account status (ACTIVA &lt;-&gt; BLOQUEADA -&gt; CERRADA).
 */
public interface BankAccountStatusUseCase {

    BankAccount blockBankAccount(String accountNumber);

    BankAccount unblockBankAccount(String accountNumber);

    BankAccount closeBankAccount(String accountNumber);
}
