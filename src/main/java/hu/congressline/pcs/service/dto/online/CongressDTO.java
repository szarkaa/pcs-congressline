package hu.congressline.pcs.service.dto.online;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Country;
import hu.congressline.pcs.domain.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CongressDTO {

    @NotNull
    private String uuid;
    @NotNull
    private String meetingCode;
    @NotNull
    private String name;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    private Country defaultCountry;
    private String website;
    @NotNull
    private OnlineRegConfigDTO onlineRegConfig;
    @NotNull
    private Set<String> currencies;
    @NotNull
    private Set<String> onlineRegCurrencies;

    public CongressDTO(Congress congress) {
        this.uuid = congress.getUuid();
        this.meetingCode = congress.getMeetingCode();
        this.name = congress.getName();
        this.startDate = congress.getStartDate();
        this.endDate = congress.getEndDate();
        this.defaultCountry = congress.getDefaultCountry();
        this.website = congress.getWebsite();
        this.currencies = congress.getCurrencies().stream().map(Currency::getCurrency).map(String::toLowerCase).collect(Collectors.toSet());
        this.onlineRegCurrencies = congress.getOnlineRegCurrencies().stream().map(Currency::getCurrency).map(String::toLowerCase).collect(Collectors.toSet());
    }

}
