package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AccPeopleVM {

    private Long id;

    @Size(max = 8)
    private String title;

    @NotNull
    @Size(max = 64)
    private String lastName;

    @NotNull
    @Size(max = 64)
    private String firstName;

    @NotNull
    private Long registrationRegistrationTypeId;
}
