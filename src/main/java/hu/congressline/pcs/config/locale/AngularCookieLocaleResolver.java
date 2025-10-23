package hu.congressline.pcs.config.locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.util.WebUtils;

import java.util.Locale;
import java.util.TimeZone;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Angular cookie saved the locale with a double quote (%22en%22).
 * The default CookieLocaleResolver cannot parse this form.
 *
 * This implementation removes %22 when reading, and adds %22 around the locale when writing.
 *
 * Note: CookieLocaleResolver no longer exposes a public getCookieName() in Spring 6,
 * so we store the cookie name in this subclass to read the cookie reliably.
 */
@Slf4j
public class AngularCookieLocaleResolver extends CookieLocaleResolver {

    private static final String QUOTE = "%22";
    /**
     * Store the cookie name here so we can read it back (CookieLocaleResolver
     * no longer exposes a public getter for it).
     */
    private final String cookieName;

    public AngularCookieLocaleResolver() {
        super(); // uses DEFAULT_COOKIE_NAME internally
        this.cookieName = DEFAULT_COOKIE_NAME;
    }

    public AngularCookieLocaleResolver(String cookieName) {
        super(cookieName);
        this.cookieName = cookieName != null ? cookieName : DEFAULT_COOKIE_NAME;
    }

    /**
     * Override cookie writing: wrap locale value with %22 for Angular compatibility.
     */
    @Override
    protected String toLocaleValue(Locale locale) {
        String value = super.toLocaleValue(locale);
        return value == null ? null : QUOTE + value + QUOTE;
    }

    /**
     * Override cookie reading: remove %22 if present before parsing.
     */
    @Override
    protected Locale parseLocaleValue(String localeValue) {
        if (localeValue == null) {
            return super.parseLocaleValue(null);
        }
        String cleaned = localeValue.replace(QUOTE, "");
        return super.parseLocaleValue(cleaned);
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
    }

    @Override
    public LocaleContext resolveLocaleContext(final HttpServletRequest request) {
        parseLocaleCookieIfNecessary(request);
        return new TimeZoneAwareLocaleContext() {
            @Override
            @Nullable
            public Locale getLocale() {
                return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
            }

            @Override
            @Nullable
            public TimeZone getTimeZone() {
                return (TimeZone) request.getAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME);
            }
        };
    }

    /**
     * Use our stored cookieName instead of a non-existent getCookieName() method.
     */
    private void parseLocaleCookieIfNecessary(HttpServletRequest request) {
        if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) == null) {
            Cookie cookie = WebUtils.getCookie(request, this.cookieName);
            Locale locale = null;
            TimeZone timeZone = null;

            if (cookie != null) {
                String value = cookie.getValue();
                // Remove encoded double quotes that Angular added
                value = value != null ? value.replace(QUOTE, "") : "";

                String localePart = value;
                String timeZonePart = null;
                int spaceIndex = localePart != null ? localePart.indexOf(' ') : -1;
                if (spaceIndex != -1) {
                    localePart = value.substring(0, spaceIndex);
                    timeZonePart = value.substring(spaceIndex + 1);
                }

                locale = localePart != null && !"-".equals(localePart) ? Locale.forLanguageTag(localePart.replace('_', '-')) : null;

                if (timeZonePart != null && !timeZonePart.isEmpty()) {
                    timeZone = TimeZone.getTimeZone(timeZonePart);
                }

                if (log.isTraceEnabled()) {
                    log.trace("Parsed cookie value [" + cookie.getValue() + "] into locale '" + locale
                            + "'" + (timeZone != null ? " and time zone '" + timeZone.getID() + "'" : ""));
                }
            }

            request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale != null ? locale : determineDefaultLocale(request));

            request.setAttribute(TIME_ZONE_REQUEST_ATTRIBUTE_NAME, timeZone != null ? timeZone : determineDefaultTimeZone(request));
        }
    }
}
