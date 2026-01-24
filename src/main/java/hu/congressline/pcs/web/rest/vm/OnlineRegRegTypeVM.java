package hu.congressline.pcs.web.rest.vm;

import java.util.List;

import hu.congressline.pcs.domain.RegistrationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegRegTypeVM {
    private RegistrationType registrationType;
    private List<OnlineAccPeopleVM> accompanies;
}
