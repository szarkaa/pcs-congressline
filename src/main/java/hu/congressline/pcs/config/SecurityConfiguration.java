package hu.congressline.pcs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

import hu.congressline.pcs.security.AuthoritiesConstants;
import hu.congressline.pcs.web.filter.SpaWebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import static org.springframework.security.config.Customizer.withDefaults;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    private final PcsProperties pcsProperties;
    private final RememberMeServices rememberMeServices;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        final String apiPattern = "/api/**";

        PathPatternRequestMatcher.Builder pp = PathPatternRequestMatcher.withDefaults();

        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            /*
            .csrf(csrf ->
                csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            */
            //.addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(
                        pcsProperties.getSecurity().getContentSecurityPolicy()
                    ))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(
                        ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN
                    ))
                    .permissionsPolicyHeader(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), "
                                + "microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz -> authz
                // static assets
                .requestMatchers(
                    pp.matcher("/index.html"),
                    pp.matcher("/*.js"),
                    pp.matcher("/*.txt"),
                    pp.matcher("/*.json"),
                    pp.matcher("/*.map"),
                    pp.matcher("/*.css")
                ).permitAll()
                .requestMatchers(
                    pp.matcher("/*.ico"),
                    pp.matcher("/*.png"),
                    pp.matcher("/*.svg"),
                    pp.matcher("/*.webapp")
                ).permitAll()

                // app folders
                .requestMatchers(pp.matcher("/app/**")).permitAll()
                .requestMatchers(pp.matcher("/bower_components/**")).permitAll()
                .requestMatchers(pp.matcher("/i18n/**")).permitAll()
                .requestMatchers(pp.matcher("/content/**")).permitAll()
                .requestMatchers(pp.matcher("/swagger-ui/**")).permitAll()

                // public API endpoints
                .requestMatchers(pp.matcher("/api/authenticate")).permitAll()
                .requestMatchers(pp.matcher("/api/register")).permitAll()
                .requestMatchers(pp.matcher("/api/activate")).permitAll()
                .requestMatchers(pp.matcher("/api/account/reset-password/init")).permitAll()
                .requestMatchers(pp.matcher("/api/account/reset-password/finish")).permitAll()

                // protected API endpoints
                .requestMatchers(pp.matcher("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers(pp.matcher(apiPattern)).authenticated()

                // openapi / management
                .requestMatchers(pp.matcher("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                .requestMatchers(pp.matcher("/management/health")).permitAll()
                .requestMatchers(pp.matcher("/management/health/**")).permitAll()
                .requestMatchers(pp.matcher("/management/info")).permitAll()
                .requestMatchers(pp.matcher("/management/prometheus")).permitAll()
                .requestMatchers(pp.matcher("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .rememberMe(rememberMe ->
                rememberMe
                    .rememberMeServices(rememberMeServices)
                    .rememberMeParameter("remember-me")
                    .key(pcsProperties.getSecurity().getRememberMe().getKey())
            )
            .exceptionHandling(exceptions ->
                exceptions.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new OrRequestMatcher(pp.matcher(apiPattern))
                )
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/")
                    .loginProcessingUrl("/api/authentication")
                    .successHandler((request, response, authentication) ->
                        response.setStatus(HttpStatus.OK.value())
                    )
                    .failureHandler((request, response, exception) ->
                        response.setStatus(HttpStatus.UNAUTHORIZED.value())
                    )
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .logoutUrl("/api/logout")
                    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                    .permitAll()
            );

        return http.build();
    }

    /*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        final String apiPattern = "/api/**";
        http
            .cors(withDefaults())
            .csrf(AbstractHttpConfigurer::disable)
            .addFilterAfter(new SpaWebFilter(), BasicAuthenticationFilter.class)
            .headers(headers ->
                headers
                    .contentSecurityPolicy(csp -> csp.policyDirectives(hipsterProperties.getSecurity().getContentSecurityPolicy()))
                    .frameOptions(FrameOptionsConfig::sameOrigin)
                    .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .permissionsPolicyHeader(permissions ->
                        permissions.policy(
                            "camera=(), fullscreen=(self), geolocation=(), gyroscope=(), magnetometer=(), microphone=(), midi=(), payment=(), sync-xhr=()"
                        )
                    )
            )
            .authorizeHttpRequests(authz ->
                // prettier-ignore
                authz
                    .requestMatchers(mvc.pattern("/index.html"), mvc.pattern("/*.js"), mvc.pattern("/*.txt"), mvc.pattern("/*.json"), mvc.pattern("/*.map"),
                        mvc.pattern("/*.css")).permitAll()
                    .requestMatchers(mvc.pattern("/*.ico"), mvc.pattern("/*.png"), mvc.pattern("/*.svg"), mvc.pattern("/*.webapp")).permitAll()
                    .requestMatchers(mvc.pattern("/app/**")).permitAll()
                    .requestMatchers(mvc.pattern("/bower_components/**")).permitAll()
                    .requestMatchers(mvc.pattern("/i18n/**")).permitAll()
                    .requestMatchers(mvc.pattern("/content/**")).permitAll()
                    .requestMatchers(mvc.pattern("/swagger-ui/**")).permitAll()
                    .requestMatchers(mvc.pattern("/api/authenticate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/register")).permitAll()
                    .requestMatchers(mvc.pattern("/api/activate")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/init")).permitAll()
                    .requestMatchers(mvc.pattern("/api/account/reset-password/finish")).permitAll()
                    .requestMatchers(mvc.pattern("/api/admin/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern(apiPattern)).authenticated()
                    .requestMatchers(mvc.pattern("/v3/api-docs/**")).hasAuthority(AuthoritiesConstants.ADMIN)
                    .requestMatchers(mvc.pattern("/management/health")).permitAll()
                    .requestMatchers(mvc.pattern("/management/health/**")).permitAll()
                    .requestMatchers(mvc.pattern("/management/info")).permitAll()
                    .requestMatchers(mvc.pattern("/management/prometheus")).permitAll()
                    .requestMatchers(mvc.pattern("/management/**")).hasAuthority(AuthoritiesConstants.ADMIN)
            )
            .rememberMe(rememberMe ->
                rememberMe
                    .rememberMeServices(rememberMeServices)
                    .rememberMeParameter("remember-me")
                    .key(hipsterProperties.getSecurity().getRememberMe().getKey())
            )
            .exceptionHandling(exceptionHanding ->
                exceptionHanding.defaultAuthenticationEntryPointFor(
                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                    new OrRequestMatcher(antMatcher(apiPattern))
                )
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/")
                    .loginProcessingUrl("/api/authentication")
                    .successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))
                    .failureHandler((request, response, exception) -> response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .permitAll()
            )
            .logout(logout ->
                logout.logoutUrl("/api/logout").logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler()).permitAll()
            );
        return http.build();
    }
    */

    /**
     * Custom CSRF handler to provide BREACH protection for Single-Page Applications (SPA).
     *
     * @see <a href="https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-integration-javascript-spa">Spring Security Documentation - Integrating with CSRF Protection</a>
     * @see <a href="https://github.com/jhipster/generator-jhipster/pull/25907">JHipster - use customized SpaCsrfTokenRequestHandler to handle CSRF token</a>
     * @see <a href="https://stackoverflow.com/q/74447118/65681">CSRF protection not working with Spring Security 6</a>
     */
    static final class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

        private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
        private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
            /*
             * Always use XorCsrfTokenRequestAttributeHandler to provide BREACH protection of
             * the CsrfToken when it is rendered in the response body.
             */
            this.xor.handle(request, response, csrfToken);

            // Render the token value to a cookie by causing the deferred token to be loaded.
            csrfToken.get();
        }

        @Override
        public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
            /*
             * If the request contains a request header, use CsrfTokenRequestAttributeHandler
             * to resolve the CsrfToken. This applies when a single-page application includes
             * the header value automatically, which was obtained via a cookie containing the
             * raw CsrfToken.
             */
            if (StringUtils.hasText(request.getHeader(csrfToken.getHeaderName()))) {
                return this.plain.resolveCsrfTokenValue(request, csrfToken);
            }
            /*
             * In all other cases (e.g. if the request contains a request parameter), use
             * XorCsrfTokenRequestAttributeHandler to resolve the CsrfToken. This applies
             * when a server-side rendered form includes the _csrf request parameter as a
             * hidden input.
             */
            return this.xor.resolveCsrfTokenValue(request, csrfToken);
        }
    }
}
