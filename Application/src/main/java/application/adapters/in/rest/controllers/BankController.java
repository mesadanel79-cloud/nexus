package application.adapters.in.rest.controllers;

import java.math.BigDecimal;
import java.util.List;

import application.adapters.in.rest.requests.BankAccountAmountRequest;
import application.adapters.in.rest.requests.ExecuteTransferRequest;
import application.adapters.in.rest.requests.OpenBankAccountRequest;
import application.domain.models.BankAccount;
import application.domain.models.Transfer;
import application.domain.ports.in.BankAccountUseCase;
import application.domain.ports.in.ExecuteTransferUseCase;
import application.domain.valueobjects.Currency;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Input Adapter (REST): exposes bank account and transfer endpoints.
 * Controllers never implement business rules; they delegate to the domain
 * through input ports.
 */
@RestController
@RequestMapping("/api/v1/bank")
public class BankController {

    private final BankAccountUseCase bankAccountUseCase;
    private final ExecuteTransferUseCase executeTransferUseCase;

    public BankController(BankAccountUseCase bankAccountUseCase,
                          ExecuteTransferUseCase executeTransferUseCase) {
        this.bankAccountUseCase = bankAccountUseCase;
        this.executeTransferUseCase = executeTransferUseCase;
    }

    @PostMapping("/accounts")
    public ResponseEntity<BankAccount> openAccount(
            @RequestBody OpenBankAccountRequest request) {
        BankAccount account = bankAccountUseCase.openAccount(
                request.getHolderId(),
                Currency.fromCode(request.getCurrencyCode()),
                request.getInitialAmount());
        return ResponseEntity.ok(account);
    }

    @GetMapping("/accounts/{accountNumber}")
    public ResponseEntity<BankAccount> consultAccount(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                bankAccountUseCase.consultAccount(accountNumber));
    }

    @PostMapping("/accounts/{accountNumber}/deposits")
    public ResponseEntity<BankAccount> deposit(
            @PathVariable String accountNumber,
            @RequestBody BankAccountAmountRequest request) {
        BigDecimal amount = request.getAmount() != null
                ? request.getAmount() : BigDecimal.ZERO;
        return ResponseEntity.ok(
                bankAccountUseCase.deposit(accountNumber, amount));
    }

    @PostMapping("/accounts/{accountNumber}/withdrawals")
    public ResponseEntity<BankAccount> withdraw(
            @PathVariable String accountNumber,
            @RequestBody BankAccountAmountRequest request) {
        BigDecimal amount = request.getAmount() != null
                ? request.getAmount() : BigDecimal.ZERO;
        return ResponseEntity.ok(
                bankAccountUseCase.withdraw(accountNumber, amount));
    }

    @PostMapping("/accounts/{accountNumber}/blocking")
    public ResponseEntity<BankAccount> block(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                bankAccountUseCase.blockAccount(accountNumber));
    }

    @PostMapping("/accounts/{accountNumber}/unblocking")
    public ResponseEntity<BankAccount> unblock(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                bankAccountUseCase.unblockAccount(accountNumber));
    }

    @PostMapping("/accounts/{accountNumber}/closing")
    public ResponseEntity<BankAccount> close(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                bankAccountUseCase.closeAccount(accountNumber));
    }

    @PostMapping("/transfers")
    public ResponseEntity<Transfer> executeTransfer(
            @RequestBody ExecuteTransferRequest request) {
        Transfer transfer = executeTransferUseCase.executeTransfer(
                request.getSourceAccountNumber(),
                request.getDestinationAccountNumber(),
                request.getAmount(),
                Currency.fromCode(request.getCurrencyCode()));
        return ResponseEntity.ok(transfer);
    }

    @GetMapping("/accounts/{accountNumber}/transfers")
    public ResponseEntity<List<Transfer>> consultTransfers(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(
                executeTransferUseCase.consultTransfers(accountNumber));
    }
}