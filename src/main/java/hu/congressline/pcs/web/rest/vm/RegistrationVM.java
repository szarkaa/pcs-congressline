package hu.congressline.pcs.web.rest.vm;

import java.util.Objects;

import hu.congressline.pcs.domain.Registration;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationVM {
    private Long id;
    private Integer regId;
    private String lastName;
    private String firstName;

    public RegistrationVM(Registration registration) {
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

        RegistrationVM that = (RegistrationVM) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
