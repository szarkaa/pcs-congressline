package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.GroupDiscountPayment;
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
public class GroupDiscountPaymentDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ChargeableItemType paymentType;
    @NotNull
    private Long payingGroupId;
    @NotNull
    private String payingGroupName;
    @NotNull
    private LocalDate dateOfPayment;
    @NotNull
    @DecimalMin("0")
    @DecimalMax("100000000")
    private BigDecimal amount;
    private CurrencyDTO currency;
    private CongressDTO congress;

    public GroupDiscountPaymentDTO(GroupDiscountPayment groupDiscountPayment) {
        this.id = groupDiscountPayment.getId();
        this.paymentType = groupDiscountPayment.getPaymentType();
        this.payingGroupId = groupDiscountPayment.getPayingGroup().getId();
        this.payingGroupName = groupDiscountPayment.getPayingGroup().getName();
        this.dateOfPayment = groupDiscountPayment.getDateOfPayment();
        this.amount = groupDiscountPayment.getAmount();
        this.currency = new CurrencyDTO(groupDiscountPayment.getCurrency());
        this.congress = new CongressDTO(groupDiscountPayment.getCongress());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupDiscountPaymentDTO groupDiscountPayment = (GroupDiscountPaymentDTO) o;
        if (groupDiscountPayment.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, groupDiscountPayment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GroupDiscountPaymentDTO{" + "id=" + id + "}";
    }
}
