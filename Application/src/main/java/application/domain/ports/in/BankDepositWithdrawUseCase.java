package application.domain.ports.in;

import java.math.BigDecimal;

import application.domain.models.BankAccount;

/**
 * Input Port (Bank subdomain - bank-services Deposit / Withdraw Funds):
 * settlement-relevant mutations, each generating Operation + Audit.
 */
public interface BankDepositWithdrawUseCase {

    BankAccount depositFunds(String accountNumber, BigDecimal amount);

    BankAccount withdrawFunds(String accountNumber, BigDecimal amount);
}
