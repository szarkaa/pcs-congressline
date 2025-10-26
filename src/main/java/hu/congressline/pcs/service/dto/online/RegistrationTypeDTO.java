package hu.congressline.pcs.service.dto.online;

import java.math.BigDecimal;

import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.domain.enumeration.OnlineType;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationTypeDTO {
    private Long id;
    private String name;
    private BigDecimal currentRegFee;
    private BigDecimal firstRegFee;
    private BigDecimal secondRegFee;
    private BigDecimal thirdRegFee;
    private RegistrationTypeType registrationType;
    private String currency;
    private OnlineType onlineType;

    public RegistrationTypeDTO(RegistrationType regType) {
        this.id = regType.getId();
        this.name = regType.getName() + (regType.getOnlineLabel() != null ? " " + regType.getOnlineLabel() : "");
        this.firstRegFee = regType.getFirstRegFee();
        this.secondRegFee = regType.getSecondRegFee();
        this.thirdRegFee = regType.getThirdRegFee();
        this.registrationType = regType.getRegistrationType();
        this.currency = regType.getCurrency().getCurrency();
        this.onlineType = regType.getOnlineType();
    }
}
