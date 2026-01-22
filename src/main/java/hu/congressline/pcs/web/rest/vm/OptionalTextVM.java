package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OptionalTextVM {
    private Long id;

    @NotNull
    @Size(max = 32)
    private String name;

    @NotNull
    @Size(max = 2048)
    private String optionalText;

    @NotNull
    private Long congressId;

}
