package hu.congressline.pcs.service.dto;

import java.util.Objects;

import hu.congressline.pcs.domain.Registration;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationBriefDTO {
    private Long id;
    private Integer regId;
    private String lastName;
    private String firstName;

    public RegistrationBriefDTO(Registration registration) {
        this.id = registration.getId();
        this.regId = registration.getRegId();
        this.lastName = registration.getLastName();
        this.firstName = registration.getFirstName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RegistrationBriefDTO that = (RegistrationBriefDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
