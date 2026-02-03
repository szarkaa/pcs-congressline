package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.enumeration.OnlineType;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegistrationTypeVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 16)
    private String code;

    @NotNull
    @Size(min = 5, max = 100)
    private String name;

    @NotNull
    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    private BigDecimal firstRegFee;

    private LocalDate firstDeadline;

    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    private BigDecimal secondRegFee;

    private LocalDate secondDeadline;

    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    private BigDecimal thirdRegFee;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RegistrationTypeType registrationType;

    @NotNull
    private OnlineType onlineType;

    @NotNull
    private OnlineVisibility onlineVisibility;

    @Size(max = 200)
    private String onlineLabel;

    @Min(0)
    @Max(100)
    private Integer onlineOrder;

    @NotNull
    private Long vatInfoId;

    @NotNull
    private Long currencyId;

    @NotNull
    private Long congressId;

    @Override
    public String toString() {
        return "RegistrationTypeVM{" + "id=" + id + "}";
    }
}
