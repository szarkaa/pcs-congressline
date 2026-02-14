package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;

import hu.congressline.pcs.domain.enumeration.OnlineDiscountCodeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OnlineRegDiscountCodeVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(min = 5, max = 32)
    private String code;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer discountPercentage;

    private OnlineDiscountCodeType discountType;

    private Long congressId;

    @Override
    public String toString() {
        return "OnlineRegDiscountCodeVM{" + "id=" + id + "}";
    }
}
