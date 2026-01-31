package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hu.congressline.pcs.domain.RegistrationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GeneralRegistrationReportDTO implements Serializable {
    private Long id;
    private Integer regId;
    private String lastName;
    private String firstName;
    private String shortName;
    private String invoiceName;
    private String title;
    private String position;
    private String otherData;
    private String department;
    private String zipCode;
    private String city;
    private String country;
    private String countryCode;
    private String street;
    private String phone;
    private String email;
    private String workplaceName;
    private String workplaceDepartment;
    private String workplaceZipCode;
    private String workplaceCity;
    private String workplaceStreet;
    private String workplacePhone;
    private String workplaceEmail;
    private String workplaceCountry;
    private String remark;
    private String accompanyingNames;
    private Integer accompanyingNum;
    private String hotelNames;
    private LocalDate dateOfApp;
    private List<RegistrationTypeDTO> registrationTypes = Collections.emptyList();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneralRegistrationReportDTO that = (GeneralRegistrationReportDTO) o;
        return Objects.equals(regId, that.regId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regId);
    }

    @Override
    public String toString() {
        return "GeneralRegistrationReportDTO{regId=" + regId + "}";
    }
}
