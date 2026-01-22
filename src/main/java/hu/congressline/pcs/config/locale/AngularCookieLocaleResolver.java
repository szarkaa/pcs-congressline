package hu.congressline.pcs.config.locale;

import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * Angular cookie saved the locale with a double quote (%22en%22).
 * CookieLocaleResolver cannot parse this form.
 *
 * This implementation removes %22 when reading, and adds %22 around the locale when writing.
 */
@Slf4j
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    private static final String QUOTE = "%22";

    public AngularCookieLocaleResolver() {
        super(); // uses DEFAULT_COOKIE_NAME internally
    }

    public AngularCookieLocaleResolver(String cookieName) {
        super(cookieName);
    }

    /**
     * Wrap locale value with %22 for Angular compatibility.
     */
    @Override
    protected String toLocaleValue(Locale locale) {
        String value = super.toLocaleValue(locale);
        return value != null ? QUOTE + value + QUOTE : null;
    }

    /**
     * Remove %22 if present before parsing.
     */
    @Override
    protected Locale parseLocaleValue(String localeValue) {
        if (localeValue == null) {
            return super.parseLocaleValue(null);
        }
        return super.parseLocaleValue(localeValue.replace(QUOTE, ""));
    }
}
