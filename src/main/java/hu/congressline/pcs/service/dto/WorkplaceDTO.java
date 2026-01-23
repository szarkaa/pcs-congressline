package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;

import hu.congressline.pcs.domain.Workplace;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class WorkplaceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String vatRegNumber;
    private String department;
    private String zipCode;
    private String city;
    private String street;
    private String phone;
    private String fax;
    private String email;
    private CountryDTO country;
    private Long congressId;

    public WorkplaceDTO(@NonNull Workplace workplace) {
        this.id = workplace.getId();
        this.name = workplace.getName();
        this.vatRegNumber = workplace.getVatRegNumber();
        this.department = workplace.getDepartment();
        this.zipCode = workplace.getZipCode();
        this.city = workplace.getCity();
        this.street = workplace.getStreet();
        this.phone = workplace.getPhone();
        this.fax = workplace.getFax();
        this.email = workplace.getEmail();
        this.country = nonNull(workplace.getCountry()) ? new CountryDTO(workplace.getCountry()) : null;
        this.congressId = nonNull(workplace.getCongress()) ? workplace.getCongress().getId() : null;
    }

}
