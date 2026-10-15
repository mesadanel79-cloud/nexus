package application.domain.ports.in;

import java.math.BigDecimal;

import application.domain.models.Transfer;
import application.domain.valueobjects.Currency;

/**
 * Input Port (Bank subdomain - settlement layer used by
 * Order Payment / Seller Payout / Refund):
 *
 * Order Payment (Invoicing) -&gt; Buyer pays into Marketplace account.
 * Seller Payout (Invoicing) -&gt; Marketplace pays into Seller account.
 * Refund (Returns/Refunds)  -&gt; Marketplace pays back into Buyer account.
 */
public interface BankSettlementUseCase {

    Transfer settleOrderPayment(String buyerAccountNumber,
                                String marketplaceAccountNumber,
                                BigDecimal amount, Currency currency);

    Transfer settleSellerPayout(String marketplaceAccountNumber,
                                String sellerAccountNumber,
                                BigDecimal amount, Currency currency);

    Transfer settleRefundPayment(String marketplaceAccountNumber,
                                 String buyerAccountNumber,
                                 BigDecimal amount, Currency currency);
}
