package hu.congressline.pcs.web.rest.vm;

import hu.congressline.pcs.domain.OptionalService;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegOptionalServiceVM {
    private OptionalService optionalService;
    private Integer participants;
}
