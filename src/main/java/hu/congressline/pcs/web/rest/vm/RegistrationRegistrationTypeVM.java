package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationRegistrationTypeVM {
    private Long id;
    @NotNull
    private Long registrationTypeId;
    @NotNull
    private Long registrationId;
    private Long payingGroupItemId;
    private Integer accPeople;
}
