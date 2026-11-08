package hu.congressline.pcs.web.rest.vm;

import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GroupDiscountPaymentVM {
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChargeableItemType paymentType;

    @NotNull
    private Long payingGroupId;

    @NotNull
    private LocalDate dateOfPayment;

    @NotNull
    @DecimalMin("0")
    @DecimalMax("100000000")
    private BigDecimal amount;
    @NotNull
    private Long currencyId;

    @NotNull
    private Long congressId;

    public String toString() {
        return "GroupDiscountPaymentVM{" + "id=" + id + "}";
    }
}
