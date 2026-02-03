package hu.congressline.pcs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import hu.congressline.pcs.config.locale.AngularCookieLocaleResolver;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class LocaleConfiguration implements WebMvcConfigurer {

    private final Environment environment;

    @Bean(name = "localeResolver")
    public LocaleResolver localeResolver() {
        // Cookie name must be provided via constructor in Spring 6+/7
        return new AngularCookieLocaleResolver("NG_TRANSLATE_LANG_KEY");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language");
        registry.addInterceptor(localeChangeInterceptor);
    }
}

