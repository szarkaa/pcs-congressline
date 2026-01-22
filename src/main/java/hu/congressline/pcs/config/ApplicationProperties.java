package hu.congressline.pcs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * Properties specific to Pcs.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 */
@Getter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    private final System system = new System();
    private final Nav nav = new Nav();
    private final Payment payment = new Payment();

    @Getter
    @Setter
    public static class Liquibase {
        private Boolean asyncStart = true;
    }

    @Getter
    @Setter
    public static class System {
        private String baseUrl;
    }

    @Getter
    @Setter
    public static class Nav {
        private String url;
        private Long checkingFrequencyInMillis;
        private Header header;
        private User user;
        private Software software;
    }

    @Getter
    @Setter
    public static class Header {
        private String headerVersion;
        private String requestVersion;
        private String taxNumber;
    }

    @Getter
    @Setter
    public static class User {
        private String login;
        private String password;
        private String signKey;
        private String changeKey;
        private String taxNumber;
    }

    @Getter
    @Setter
    public static class Software {
        private String softwareId;
        private String softwareName;
        private String softwareOperation;
        private String softwareMainVersion;
        private String softwareDevName;
        private String softwareDevContact;
        private String softwareDevCountryCode;
        private String softwareDevTaxNumber;
    }

    @Getter
    @Setter
    public static class Payment {
        private Gateway gateway;
    }

    @Getter
    @Setter
    public static class Gateway {
        private String url;
        private String returnUrl;
        private String merchantIdForEUR;
        private String merchantIdForHUF;
        private String paymentKeyFilePath;
        private Boolean signRequired;
        private Long checkingFrequencyInMillis;
    }
}
