package hu.congressline.pcs.web.rest.vm;

import hu.congressline.pcs.domain.RegistrationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegFeeDetailsByParticipantsVM {

    private Long congressId;
    private RegistrationType registrationType;

}
