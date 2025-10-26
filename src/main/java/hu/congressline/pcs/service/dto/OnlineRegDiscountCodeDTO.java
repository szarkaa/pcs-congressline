package hu.congressline.pcs.service.dto;

import hu.congressline.pcs.domain.OnlineRegDiscountCode;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegDiscountCodeDTO {
    private String code;
    private Integer discountPercentage;
    private ChargeableItemType discountType;

    public OnlineRegDiscountCodeDTO(OnlineRegDiscountCode discountCode) {
        this.code = discountCode.getCode();
        this.discountPercentage = discountCode.getDiscountPercentage();
        this.discountType = discountCode.getDiscountType();
    }
}
