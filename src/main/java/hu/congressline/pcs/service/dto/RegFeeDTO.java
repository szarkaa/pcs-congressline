package hu.congressline.pcs.service.dto;

import java.math.BigDecimal;

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegFeeDTO {
    private Long registrationTypeId;
    private BigDecimal regFee;
    private String currency;

    public RegFeeDTO(RegistrationRegistrationType rrt) {
        this.registrationTypeId = rrt.getRegistrationType().getId();
        this.regFee = rrt.getRegFee();
        this.currency = rrt.getRegistrationType().getCurrency().getCurrency();
    }
}
