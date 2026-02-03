package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;

import hu.congressline.pcs.domain.PayingGroup;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class PayingGroupDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String zipCode;
    private String city;
    private String street;
    private String contactName;
    private String email;
    private String phone;
    private String fax;
    private String taxNumber;
    private CountryDTO country;
    private CurrencyDTO currency;
    private Long congressId;

    public PayingGroupDTO(@NonNull PayingGroup payingGroup) {
        this.id = payingGroup.getId();
        this.name = payingGroup.getName();
        this.zipCode = payingGroup.getZipCode();
        this.city = payingGroup.getCity();
        this.street = payingGroup.getStreet();
        this.contactName = payingGroup.getContactName();
        this.email = payingGroup.getEmail();
        this.phone = payingGroup.getPhone();
        this.fax = payingGroup.getFax();
        this.taxNumber = payingGroup.getTaxNumber();
        this.currency = nonNull(payingGroup.getCurrency()) ? new CurrencyDTO(payingGroup.getCurrency()) : null;
        this.country = nonNull(payingGroup.getCountry()) ? new CountryDTO(payingGroup.getCountry()) : null;
        this.congressId = nonNull(payingGroup.getCongress()) ? payingGroup.getCongress().getId() : null;
    }

    @Override
    public String toString() {
        return "MiscServiceDTO{" + "id=" + id + "}";
    }
}
