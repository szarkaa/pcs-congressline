package hu.congressline.pcs.web.rest.vm;

import java.math.BigDecimal;

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegFeeVM {
    private Long registrationTypeId;
    private BigDecimal regFee;
    private String currency;

    public RegFeeVM(RegistrationRegistrationType rrt) {
        this.registrationTypeId = rrt.getRegistrationType().getId();
        this.regFee = rrt.getRegFee();
        this.currency = rrt.getRegistrationType().getCurrency().getCurrency();
    }
}
