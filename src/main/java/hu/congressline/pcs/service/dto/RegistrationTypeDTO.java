package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.domain.enumeration.OnlineType;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class RegistrationTypeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private BigDecimal firstRegFee;
    private LocalDate firstDeadline;
    private BigDecimal secondRegFee;
    private LocalDate secondDeadline;
    private BigDecimal thirdRegFee;
    private RegistrationTypeType registrationType;
    private OnlineType onlineType;
    private OnlineVisibility onlineVisibility;
    private String onlineLabel;
    private Integer onlineOrder;
    private VatInfoDTO vatInfo;
    private CurrencyDTO currency;
    private Long congressId;

    public RegistrationTypeDTO(@NonNull RegistrationType registrationType) {
        this.id = registrationType.getId();
        this.code = registrationType.getCode();
        this.name = registrationType.getName();
        this.firstRegFee = registrationType.getFirstRegFee();
        this.firstDeadline = registrationType.getFirstDeadline();
        this.secondRegFee = registrationType.getSecondRegFee();
        this.secondDeadline = registrationType.getSecondDeadline();
        this.thirdRegFee = registrationType.getThirdRegFee();
        this.registrationType = registrationType.getRegistrationType();
        this.onlineType = registrationType.getOnlineType();
        this.onlineVisibility = registrationType.getOnlineVisibility();
        this.onlineLabel = registrationType.getOnlineLabel();
        this.onlineOrder = registrationType.getOnlineOrder();
        this.currency = nonNull(registrationType.getCurrency()) ? new CurrencyDTO(registrationType.getCurrency()) : null;
        this.vatInfo = nonNull(registrationType.getVatInfo()) ? new VatInfoDTO(registrationType.getVatInfo()) : null;
        this.congressId = nonNull(registrationType.getCongress()) ? registrationType.getCongress().getId() : null;
    }

    @Override
    public String toString() {
        return "RegistrationTypeDTO{" + "id=" + id + "}";
    }
}
