package hu.congressline.pcs.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationSummaryDTO {
    private Long registered;
    private Long onSpot;
    private Long accPeople;
}
