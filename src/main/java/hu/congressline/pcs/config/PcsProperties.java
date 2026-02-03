package hu.congressline.pcs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@ConfigurationProperties(prefix = "pcs", ignoreUnknownFields = false)
public class PcsProperties {

    private final PcsProperties.Async async = new PcsProperties.Async();
    private final PcsProperties.Http http = new PcsProperties.Http();
    private final PcsProperties.Mail mail = new PcsProperties.Mail();
    private final PcsProperties.Security security = new PcsProperties.Security();
    private final PcsProperties.ApiDocs apiDocs = new PcsProperties.ApiDocs();
    private final CorsConfiguration cors = new CorsConfiguration();
    private final PcsProperties.Social social = new PcsProperties.Social();
    private final PcsProperties.Gateway gateway = new PcsProperties.Gateway();
    private final PcsProperties.Registry registry = new PcsProperties.Registry();
    private final PcsProperties.ClientApp clientApp = new PcsProperties.ClientApp();
    private final PcsProperties.AuditEvents auditEvents = new PcsProperties.AuditEvents();

    @Setter
    @Getter
    public static class Async {
        private int corePoolSize = PcsDefaults.Async.corePoolSize;
        private int maxPoolSize = PcsDefaults.Async.maxPoolSize;
        private int queueCapacity = PcsDefaults.Async.queueCapacity;
    }

    @Setter
    @Getter
    public static class Http {
        private final PcsProperties.Http.Cache cache = new PcsProperties.Http.Cache();

        @Setter
        @Getter
        public static class Cache {
            private int timeToLiveInDays = PcsDefaults.Http.Cache.timeToLiveInDays;
        }
    }

    @Setter
    @Getter
    public static class Mail {
        private boolean enabled = PcsDefaults.Mail.enabled;
        private String from = PcsDefaults.Mail.from;
        private String baseUrl = PcsDefaults.Mail.baseUrl;
    }

    @Setter
    @Getter
    public static class Security {
        private String contentSecurityPolicy = PcsDefaults.Security.contentSecurityPolicy;
        private final PcsProperties.Security.ClientAuthorization clientAuthorization = new PcsProperties.Security.ClientAuthorization();
        private final PcsProperties.Security.Authentication authentication = new PcsProperties.Security.Authentication();
        private final PcsProperties.Security.RememberMe rememberMe = new PcsProperties.Security.RememberMe();
        private final PcsProperties.Security.OAuth2 oauth2 = new PcsProperties.Security.OAuth2();

        @Setter
        @Getter
        public static class ClientAuthorization {
            private String accessTokenUri = PcsDefaults.Security.ClientAuthorization.accessTokenUri;
            private String tokenServiceId = PcsDefaults.Security.ClientAuthorization.tokenServiceId;
            private String clientId = PcsDefaults.Security.ClientAuthorization.clientId;
            private String clientSecret = PcsDefaults.Security.ClientAuthorization.clientSecret;
        }

        @Setter
        @Getter
        public static class Authentication {
            private final PcsProperties.Security.Authentication.Jwt jwt = new PcsProperties.Security.Authentication.Jwt();

            @Setter
            @Getter
            public static class Jwt {
                private String secret = PcsDefaults.Security.Authentication.Jwt.secret;
                private String base64Secret = PcsDefaults.Security.Authentication.Jwt.base64Secret;
                private long tokenValidityInSeconds = PcsDefaults.Security.Authentication.Jwt.tokenValidityInSeconds;
                private long tokenValidityInSecondsForRememberMe = PcsDefaults.Security.Authentication.Jwt.tokenValidityInSecondsForRememberMe;
            }
        }

        @Setter
        @Getter
        public static class RememberMe {
            @NotNull
            private String key = PcsDefaults.Security.RememberMe.key;
        }

        @Setter
        @Getter
        public static class OAuth2 {
            private List<String> audience = new ArrayList<>();
        }
    }

    @Setter
    @Getter
    public static class ApiDocs {
        private String title = PcsDefaults.ApiDocs.title;
        private String description = PcsDefaults.ApiDocs.description;
        private String version = PcsDefaults.ApiDocs.version;
        private String termsOfServiceUrl = PcsDefaults.ApiDocs.termsOfServiceUrl;
        private String contactName = PcsDefaults.ApiDocs.contactName;
        private String contactUrl = PcsDefaults.ApiDocs.contactUrl;
        private String contactEmail = PcsDefaults.ApiDocs.contactEmail;
        private String license = PcsDefaults.ApiDocs.license;
        private String licenseUrl = PcsDefaults.ApiDocs.licenseUrl;
        private String[] defaultIncludePattern = PcsDefaults.ApiDocs.defaultIncludePattern;
        private String[] managementIncludePattern = PcsDefaults.ApiDocs.managementIncludePattern;
        private PcsProperties.ApiDocs.Server[] servers = {};

        @Setter
        @Getter
        public static class Server {
            private String url;
            private String description;
        }
    }

    @Setter
    @Getter
    public static class Social {
        private String redirectAfterSignIn = PcsDefaults.Social.redirectAfterSignIn;
    }

    @Setter
    @Getter
    public static class Gateway {
        private final PcsProperties.Gateway.RateLimiting rateLimiting = new PcsProperties.Gateway.RateLimiting();
        private Map<String, List<String>> authorizedMicroservicesEndpoints = PcsDefaults.Gateway.authorizedMicroservicesEndpoints;

        @Setter
        @Getter
        public static class RateLimiting {
            private boolean enabled = PcsDefaults.Gateway.RateLimiting.enabled;
            private long limit = PcsDefaults.Gateway.RateLimiting.limit;
            private int durationInSeconds = PcsDefaults.Gateway.RateLimiting.durationInSeconds;
        }
    }

    @Setter
    @Getter
    public static class Registry {
        private String password = PcsDefaults.Registry.password;
    }

    @Setter
    @Getter
    public static class ClientApp {
        private String name = PcsDefaults.ClientApp.name;
    }

    @Setter
    @Getter
    public static class AuditEvents {
        private int retentionPeriod = PcsDefaults.AuditEvents.retentionPeriod;
    }
}
