package application.domain.ports.in;

import java.util.List;

import application.domain.models.Transfer;

/**
 * Input Port (Bank subdomain - bank-services Consult Transfer History):
 * read-only retrieval of transfers for an account.
 */
public interface ConsultTransferHistoryUseCase {

    List<Transfer> consultTransferHistory(String accountNumber);
}
