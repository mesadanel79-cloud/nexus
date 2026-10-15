package application.domain.exceptions;

/**
 * Domain Exception (Bank subdomain): raised when opening a second active
 * account for the same holder and currency (bank-services: only one
 * active account per holder+currency is allowed).
 */
public class DuplicateBankAccountException extends DomainException {

    public DuplicateBankAccountException(String message) {
        super(message);
    }
}
