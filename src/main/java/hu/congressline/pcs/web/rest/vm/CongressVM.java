package hu.congressline.pcs.web.rest.vm;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CongressVM {

    private Long id;

    @NotNull
    @Size(min = 3, max = 15)
    private String meetingCode;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @Size(max = 128)
    private String contactPerson;

    @Size(max = 64)
    private String contactEmail;

    @Size(max = 64)
    private String programNumber;

    @Size(max = 1000)
    private String website;

    @Size(max = 1000)
    private String additionalBillingTextHu;

    @Size(max = 1000)
    private String additionalBillingTextEn;

    private Long defaultCountryId;

    private Long migrateCongressId;

    private Boolean archive = Boolean.FALSE;

    private Set<Long> currencyIds = new HashSet<>();

    private Set<Long> onlineRegCurrencyIds = new HashSet<>();

    private Set<Long> bankAccountIds = new HashSet<>();

    @Override
    public String toString() {
        return "CongressVM{" + "id='" + id + "}";
    }
}
