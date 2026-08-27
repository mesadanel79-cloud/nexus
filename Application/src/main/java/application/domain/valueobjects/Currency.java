package application.domain.valueobjects;

/**
 * Currency Value Object.
 *
 * Represents a currency supported by NexusMarket for order invoicing.
 * A business Value Object because its meaning is determined by its
 * controlled values, not by an independent identity.
 *
 * Additional attributes: ISO 4217 code and display symbol.
 *
 * Permitted values: COP, USD.
 */
public final class Currency extends DomainCatalog {

    public static final Currency COP =
            new Currency("COP", "Peso Colombiano",
                    "Moneda legal de Colombia utilizada para la facturacion.", "COP");
    public static final Currency USD =
            new Currency("USD", "Dolar Estadounidense",
                    "Moneda de curso legal de Estados Unidos utilizada para la facturacion.", "USD");

    private static final Currency[] VALUES = {COP, USD};

    private final String isoCode;
    private final String symbol;

    private Currency(String code, String name, String description,
                     String isoCode) {
        super(code, name, description);
        this.isoCode = isoCode;
        this.symbol = "$";
    }

    /** ISO 4217 currency code. */
    public String getIsoCode() {
        return isoCode;
    }

    /** Currency display symbol. */
    public String getSymbol() {
        return symbol;
    }

    /** All controlled values of this catalog. */
    public static Currency[] values() {
        return VALUES.clone();
    }

    /** Resolves the controlled instance from its business code. */
    public static Currency fromCode(String code) {
        for (Currency currency : VALUES) {
            if (currency.getCode().equals(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unknown Currency code: " + code);
    }
}