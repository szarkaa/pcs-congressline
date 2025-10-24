package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationSummaryVM {
    private Long registered;
    private Long onSpot;
    private Long accPeople;
}
