package application.domain.ports.in;

import java.util.List;

import application.domain.models.BankAccount;

/**
 * Input Port (Bank subdomain - bank-services Consult Bank Account):
 * read-only consultation of accounts by number or holder.
 */
public interface ConsultBankAccountUseCase {

    BankAccount consultBankAccount(String accountNumber);

    List<BankAccount> consultAccountsByHolder(String holderId);

    List<BankAccount> consultBankAccounts();
}
