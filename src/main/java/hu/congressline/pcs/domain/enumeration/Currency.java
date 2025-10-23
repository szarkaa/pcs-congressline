package hu.congressline.pcs.domain.enumeration;

public enum Currency {
    HUF, EUR, USD;

    @SuppressWarnings("MissingJavadocMethod")
    public static Currency parse(String currency) {
        if (!HUF.toString().equalsIgnoreCase(currency) && !EUR.toString().equalsIgnoreCase(currency.toUpperCase())) {
            throw new IllegalArgumentException("Payment currency has to be HUF, USD or EUR!");
        } else {
            return Currency.valueOf(currency.toUpperCase());
        }
    }
}
