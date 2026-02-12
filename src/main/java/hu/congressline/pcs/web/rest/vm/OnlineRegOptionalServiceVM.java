package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegOptionalServiceVM {
    private Long optionalServiceId;
    private String optionalServiceName;
    private Integer participants;
}
