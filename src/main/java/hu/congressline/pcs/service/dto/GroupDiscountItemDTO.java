package hu.congressline.pcs.service.dto;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GroupDiscountItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer chargeableItemId;
    private Integer regId;
    private String lastName;
    private String firstName;
    private ChargeableItemType chargeableItemType;
    private String payingGroupItemName;
    private BigDecimal amount;
    private LocalDate dateOfPayment;
    private String invoiceNumber;
    private PayingGroupDTO payingGroup;
    private String hotelName;
    private String roomType;
    private String roomMates;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupDiscountItemDTO groupDiscountItem = (GroupDiscountItemDTO) o;
        if (groupDiscountItem.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, groupDiscountItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GroupDiscountItem{id=" + id + "}";
    }
}
