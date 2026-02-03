package hu.congressline.pcs.web.rest.vm;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MiscInvoiceItemVM {
    @NotNull
    @Min(0)
    @Max(100000)
    private Integer itemQuantity;

    @NotNull
    private Long miscServiceId;

}
