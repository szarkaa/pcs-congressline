package hu.congressline.pcs.service.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import hu.congressline.pcs.domain.enumeration.Currency;

public class PcsNumberFormatter {

    private static final Map<Locale, String> PATTERNS = new HashMap<>();
    private static final String NO_PATTERN_START = "There is no pattern for locale '";
    private static final String NO_PATTERN_END = "'.";

    static {
        PATTERNS.put(new Locale("hu"), "[,][ .]");
        final String pattern = "[.][,]";
        PATTERNS.put(new Locale("en"), pattern);
        PATTERNS.put(new Locale("es"), pattern);
        PATTERNS.put(new Locale("pt"), pattern);
    }

    /** FormatSymbols thread local instance. */
    ThreadLocal<FormatSymbols> symbols = new ThreadLocal<FormatSymbols>();

    /**
     * Converts the given value to a string.
     *
     * @param object Object to format.
     * @param locale Locale to format the object in. If it is <code>null</code> then uses the default locale.
     * @return The formatted string value.
     * @throws IllegalArgumentException If the pattern or the object is invalid.
     */
    public String format(Object object, Locale locale) {
        if (object == null) {
            return "";
        }
        String pattern = PATTERNS.get(locale);
        if (pattern == null) {
            throw new IllegalStateException(NO_PATTERN_START + locale + NO_PATTERN_END);
        }
        FormatSymbols formatSymbols = getSymbols(pattern);
        String s;
        boolean isNegative = false;
        if (object instanceof BigDecimal) {
            BigDecimal number = (BigDecimal) object;
            if (number.compareTo(BigDecimal.ZERO) < 0) {
                isNegative = true;
                number = number.abs();
            }
            s = number.toString();
        } else {
            Number number = (Number) object;
            s = number.toString();
        }
        String[] parts = s.split("\\.");
        if (parts.length > 2) {
            throw new IllegalStateException("The string value of object to be formatted is invalid (object:" + object + " string value:'" + s + "').");
        }
        StringBuilder sb = new StringBuilder();
        String wholePart = parts[0];
        for (int i = wholePart.length() - 1; i >= 0; i--) {
            sb.append(wholePart.charAt(i));
            if (formatSymbols.canonicalGroupingSeparator != null && i > 0 && i < wholePart.length() - 1 && (wholePart.length() - i) % 3 == 0) {
                sb.append(formatSymbols.canonicalGroupingSeparator);
            }
        }
        if (isNegative) {
            sb.append("-");
        }
        sb = sb.reverse();
        if (parts.length > 1) {
            sb.append(formatSymbols.canonicalDecimalSeparator);
            sb.append(parts[1]);
        }
        return sb.toString();
    }

    /**
     * Parses the given string value to an object.
     *
     * @param value  String value to parse.
     * @param locale Locale to parse the object in. If it is <code>null</code> then uses the default locale.
     * @return The parsed object.
     * @throws IllegalArgumentException If the pattern or the string value is invalid.
     */
    public Object parse(String value, Locale locale) {
        String pattern = PATTERNS.get(locale);
        if (pattern == null) {
            throw new IllegalStateException(NO_PATTERN_START + locale + NO_PATTERN_END);
        }
        FormatSymbols formatSymbols = getSymbols(pattern);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            if (formatSymbols.supportedDecimalSeparators.contains(value.charAt(i))) {
                sb.append('.');
            } else if (!formatSymbols.supportedGroupingSeparators.contains(value.charAt(i))) {
                sb.append(value.charAt(i));
            }
        }
        try {
            return new BigDecimal(sb.toString());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid value string.", e);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal formatByCurrency(BigDecimal amount, String currency) {
        if (currency != null && amount != null) {
            return amount.setScale(Currency.HUF.toString().equalsIgnoreCase(currency) ? 0 : 2, RoundingMode.HALF_UP);
        }
        return amount;
    }

    /**
     * Gives back formatting symbols which is used in this thread.
     *
     * @param pattern pattern.
     * @return FormatSymbols instance.
     */
    private FormatSymbols getSymbols(String pattern) {
        if (symbols.get() == null) {
            symbols.set(new FormatSymbols(pattern));
        }
        return symbols.get();
    }

    private static class FormatSymbols {
        Set<Character> supportedDecimalSeparators;
        Character canonicalDecimalSeparator;
        Set<Character> supportedGroupingSeparators;
        Character canonicalGroupingSeparator;

        FormatSymbols(String pattern) {
            if (pattern == null) {
                throw new IllegalStateException("The 'pattern' parameter is required.");
            }
            String[] tokens = pattern.split("\\]\\[");
            if (tokens == null || tokens.length != 2 || tokens[0].length() < 2 || tokens[1].length() < 1) {
                throw new IllegalStateException("Invalid pattern '" + pattern + NO_PATTERN_END);
            }

            supportedDecimalSeparators = new HashSet<>();
            char[] chars = tokens[0].toCharArray();
            canonicalDecimalSeparator = chars[1];
            for (int i = 1; i < chars.length; i++) {
                supportedDecimalSeparators.add(chars[i]);
            }

            supportedGroupingSeparators = new HashSet<>();
            chars = tokens[1].toCharArray();
            if (chars.length > 1) {
                canonicalGroupingSeparator = chars[0];
                for (int i = 0; i < chars.length - 1; i++) {
                    supportedGroupingSeparators.add(chars[i]);
                }
            } else {
                canonicalGroupingSeparator = null;
            }
        }
    }
}
