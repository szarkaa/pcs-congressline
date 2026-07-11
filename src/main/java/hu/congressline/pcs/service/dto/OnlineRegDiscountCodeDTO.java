package hu.congressline.pcs.service.dto;

import hu.congressline.pcs.domain.OnlineRegDiscountCode;
import hu.congressline.pcs.domain.enumeration.OnlineDiscountCodeType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class OnlineRegDiscountCodeDTO {
    private Long id;
    private String code;
    private Integer discountPercentage;
    private Integer discountValue;
    private OnlineDiscountCodeType discountType;
    private Long congressId;

    public OnlineRegDiscountCodeDTO(@NonNull OnlineRegDiscountCode discountCode) {
        this.id = discountCode.getId();
        this.code = discountCode.getCode();
        this.discountPercentage = discountCode.getDiscountPercentage();
        this.discountValue = discountCode.getDiscountValue();
        this.discountType = discountCode.getDiscountType();
        this.congressId = discountCode.getCongress() != null ? discountCode.getCongress().getId() : null;
    }
}
