package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class EmailVM {

    @NotNull
    private String congressId;
    @NotNull
    private String lastName;
    @NotNull
    private String firstName;
    @NotNull
    private String email;
    private String body;
}
