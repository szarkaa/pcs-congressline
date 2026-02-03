package hu.congressline.pcs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import hu.congressline.pcs.web.rest.vm.CongressVM;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "congress")
public class Congress implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", length = 20, nullable = false, unique = true)
    private String uuid;

    @NotNull
    @Size(min = 3, max = 15)
    @Column(name = "meeting_code", length = 15, nullable = false, unique = true)
    private String meetingCode;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Size(max = 128)
    @Column(name = "contact_person", length = 128)
    private String contactPerson;

    @Size(max = 64)
    @Column(name = "contact_email", length = 64)
    private String contactEmail;

    @Size(max = 64)
    @Column(name = "program_number", length = 64)
    private String programNumber;

    @Size(max = 1000)
    @Column(name = "website", length = 1000)
    private String website;

    @Size(min = 3, max = 15)
    @Column(name = "migrated_from_congress_code", length = 15)
    private String migratedFromCongressCode;

    @Size(max = 1000)
    @Column(name = "additional_billing_text_hu", length = 1000)
    private String additionalBillingTextHu;

    @Size(max = 1000)
    @Column(name = "additional_billing_text_en", length = 1000)
    private String additionalBillingTextEn;

    @ManyToOne
    private Country defaultCountry;

    @Column(name = "archive")
    private Boolean archive = Boolean.FALSE;

    @ManyToMany
    @JoinTable(name = "congress_currencies",
               joinColumns = @JoinColumn(name = "congresses_id", referencedColumnName = "ID"),
               inverseJoinColumns = @JoinColumn(name = "currencies_id", referencedColumnName = "ID"))
    private Set<Currency> currencies = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "congress_online_reg_currencies",
            joinColumns = @JoinColumn(name = "congresses_id", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "currencies_id", referencedColumnName = "ID"))
    private Set<Currency> onlineRegCurrencies = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "congress_bank_account",
               joinColumns = @JoinColumn(name = "congresses_id", referencedColumnName = "ID"),
               inverseJoinColumns = @JoinColumn(name = "bank_accounts_id", referencedColumnName = "ID"))
    private Set<BankAccount> bankAccounts = new HashSet<>();

    @ManyToMany(mappedBy = "congresses")
    @JsonIgnore
    private Set<User> users = new HashSet<>();

    @SuppressWarnings("MissingJavadocMethod")
    public void update(@NonNull CongressVM viewModel) {
        this.meetingCode = viewModel.getMeetingCode();
        this.name = viewModel.getName();
        this.startDate = viewModel.getStartDate();
        this.endDate = viewModel.getEndDate();
        this.contactPerson = viewModel.getContactPerson();
        this.contactEmail = viewModel.getContactEmail();
        this.programNumber = viewModel.getProgramNumber();
        this.website = viewModel.getWebsite();
        this.additionalBillingTextHu = viewModel.getAdditionalBillingTextHu();
        this.additionalBillingTextEn = viewModel.getAdditionalBillingTextEn();
        this.archive = viewModel.getArchive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Congress congress = (Congress) o;
        if (congress.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, congress.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Congress{" + "id=" + id + "}";
    }
}
