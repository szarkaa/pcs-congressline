package hu.congressline.pcs.service.dto;

import java.io.Serializable;

import hu.congressline.pcs.domain.AccPeople;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class AccPeopleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String lastName;
    private String firstName;
    private Long registrationRegistrationTypeId;

    public AccPeopleDTO(@NonNull AccPeople accPeople) {
        this.id = accPeople.getId();
        this.title = accPeople.getTitle();
        this.lastName = accPeople.getLastName();
        this.firstName = accPeople.getFirstName();
        this.registrationRegistrationTypeId = accPeople.getRegistrationRegistrationType() != null ? accPeople.getRegistrationRegistrationType().getId() : null;
    }

    @Override
    public String toString() {
        return "AccPeopleDTO{" + "id=" + id + '}';
    }
}
