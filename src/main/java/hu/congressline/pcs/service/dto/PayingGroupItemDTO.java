package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import static java.util.Objects.nonNull;

@NoArgsConstructor
@Data
public class PayingGroupItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private Integer amountPercentage;
    private Integer amountValue;
    private LocalDate hotelDateFrom;
    private LocalDate hotelDateTo;
    private ChargeableItemType chargeableItemType;
    private Long payingGroupId;

    public PayingGroupItemDTO(@NonNull PayingGroupItem payingGroupItem) {
        this.id = payingGroupItem.getId();
        this.name = payingGroupItem.getName();
        this.amountPercentage = payingGroupItem.getAmountPercentage();
        this.amountValue = payingGroupItem.getAmountValue();
        this.hotelDateFrom = payingGroupItem.getHotelDateFrom();
        this.hotelDateTo = payingGroupItem.getHotelDateTo();
        this.chargeableItemType = payingGroupItem.getChargeableItemType();
        this.payingGroupId = nonNull(payingGroupItem.getPayingGroup()) ? payingGroupItem.getPayingGroup().getId() : null;
    }

    @Override
    public String toString() {
        return "PayingGroupItemDTO{" + "id=" + id + "}";
    }
}
