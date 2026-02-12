package hu.congressline.pcs.web.rest.vm;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegRegTypeVM {
    private Long registrationTypeId;
    private String registrationTypeName;
    private List<OnlineAccPeopleVM> accompanies;
}
