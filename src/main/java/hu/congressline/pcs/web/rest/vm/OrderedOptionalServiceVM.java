package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OrderedOptionalServiceVM {
    private Long id;
    @NotNull
    private Long optionalServiceId;
    @NotNull
    private Integer participant;
    @NotNull
    private Long registrationId;
    private Long payingGroupItemId;
}
