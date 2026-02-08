package hu.congressline.pcs.service.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.BankAccount;
import hu.congressline.pcs.domain.Congress;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class CongressDTO {

    private Long id;
    private String uuid;
    private String meetingCode;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private String contactPerson;
    private String contactEmail;
    private String programNumber;
    private String website;
    private String migratedFromCongressCode;
    private String additionalBillingTextHu;
    private String additionalBillingTextEn;
    private Boolean archive;
    private CountryDTO defaultCountry;
    private Set<CurrencyDTO> currencies = new HashSet<>();
    private Set<CurrencyDTO> onlineRegCurrencies = new HashSet<>();
    private Set<BankAccount> bankAccounts = new HashSet<>();

    public CongressDTO(@NonNull Congress congress) {
        this.id = congress.getId();
        this.uuid = congress.getUuid();
        this.meetingCode = congress.getMeetingCode();
        this.name = congress.getName();
        this.startDate = congress.getStartDate();
        this.endDate = congress.getEndDate();
        this.contactPerson = congress.getContactPerson();
        this.contactEmail = congress.getContactEmail();
        this.programNumber = congress.getProgramNumber();
        this.website = congress.getWebsite();
        this.migratedFromCongressCode = congress.getMigratedFromCongressCode();
        this.additionalBillingTextHu = congress.getAdditionalBillingTextHu();
        this.additionalBillingTextEn = congress.getAdditionalBillingTextEn();
        this.archive = congress.getArchive();
        this.defaultCountry = congress.getDefaultCountry() != null ? new CountryDTO(congress.getDefaultCountry()) : null;
        this.currencies = congress.getCurrencies().stream().map(CurrencyDTO::new).collect(Collectors.toSet());
        this.onlineRegCurrencies = congress.getOnlineRegCurrencies().stream().map(CurrencyDTO::new).collect(Collectors.toSet());
        this.bankAccounts = congress.getBankAccounts();
    }

    @Override
    public String toString() {
        return "CongressDTO{" + "id='" + id + "}";
    }
}
