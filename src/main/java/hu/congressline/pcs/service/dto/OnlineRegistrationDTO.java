package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

import hu.congressline.pcs.domain.OnlineRegistration;
import lombok.Data;

@Data
public class OnlineRegistrationDTO implements Serializable {

    private Long id;
    private String lastName;
    private String firstName;
    private String zipCode;
    private String city;
    private String street;
    private CountryDTO country;
    private String phone;
    private String email;
    private LocalDate dateOfApp;
    private boolean hasAttachment;
    private CongressDTO congress;

    public OnlineRegistrationDTO(OnlineRegistration onlineRegistration) {
        this.id = onlineRegistration.getId();
        this.lastName = onlineRegistration.getLastName();
        this.firstName = onlineRegistration.getFirstName();
        this.zipCode = onlineRegistration.getZipCode();
        this.city = onlineRegistration.getCity();
        this.street = onlineRegistration.getStreet();
        this.country = onlineRegistration.getCountry() != null ? new CountryDTO(onlineRegistration.getCountry()) : null;
        this.phone = onlineRegistration.getPhone();
        this.email = onlineRegistration.getEmail();
        this.congress = new CongressDTO(onlineRegistration.getCongress());
    }

    @Override
    public String toString() {
        return "OnlineRegistrationDTO{" + "id=" + id + "}";
    }
}
