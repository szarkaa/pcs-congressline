package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import hu.congressline.pcs.domain.Registration;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class RegistrationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer regId;
    private String lastName;
    private String firstName;
    private String shortName;
    private String title;
    private String position;
    private String otherData;
    private String department;
    private String zipCode;
    private String city;
    private String street;
    private String phone;
    private String email;
    private String fax;
    private LocalDate dateOfApp;
    private String remark;
    private String invoiceName;
    private CountryDTO invoiceCountry;
    private String invoiceZipCode;
    private String invoiceCity;
    private String invoiceAddress;
    private String invoiceTaxNumber;
    private Boolean onSpot;
    private Boolean cancelled;
    private Boolean presenter;
    private Boolean closed;
    private Boolean etiquette;
    private WorkplaceDTO workplace;
    private CountryDTO country;
    private Long congressId;

    public RegistrationDTO(@NonNull Registration registration) {
        this.id = registration.getId();
        this.regId = registration.getRegId();
        this.lastName = registration.getLastName();
        this.firstName = registration.getFirstName();
        this.shortName = registration.getShortName();
        this.title = registration.getTitle();
        this.position = registration.getPosition();
        this.otherData = registration.getOtherData();
        this.department = registration.getDepartment();
        this.zipCode = registration.getZipCode();
        this.city = registration.getCity();
        this.street = registration.getStreet();
        this.phone = registration.getPhone();
        this.email = registration.getEmail();
        this.fax = registration.getFax();
        this.dateOfApp = registration.getDateOfApp();
        this.remark = registration.getRemark();
        this.invoiceName = registration.getInvoiceName();
        this.invoiceCountry = registration.getInvoiceCountry() != null ? new CountryDTO(registration.getInvoiceCountry()) : null;
        this.invoiceZipCode = registration.getInvoiceZipCode();
        this.invoiceCity = registration.getInvoiceCity();
        this.invoiceAddress = registration.getInvoiceAddress();
        this.invoiceTaxNumber = registration.getInvoiceTaxNumber();
        this.onSpot = registration.getOnSpot();
        this.cancelled = registration.getCancelled();
        this.presenter = registration.getPresenter();
        this.closed = registration.getClosed();
        this.etiquette = registration.getEtiquette();
        this.workplace = registration.getWorkplace() != null ? new WorkplaceDTO(registration.getWorkplace()) : null;
        this.country = registration.getCountry() != null ? new CountryDTO(registration.getCountry()) : null;
        this.congressId = registration.getCongress() != null ? registration.getCongress().getId() : null;
    }

    @Override
    public String toString() {
        return "RegistrationDTO{" + "id=" + id + ", regId='" + regId + "'" + ", lastName='" + lastName + "'" + ", firstName='" + firstName + "'" + "}";
    }
}
