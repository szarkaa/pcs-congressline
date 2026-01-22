package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PayingGroupItemVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 128)
    private String name;

    @Min(0)
    @Max(100)
    private Integer amountPercentage;

    @Min(0)
    @Max(100000000)
    private Integer amountValue;

    private LocalDate hotelDateFrom;

    private LocalDate hotelDateTo;

    @NotNull
    private ChargeableItemType chargeableItemType;

    @NotNull
    private Long payingGroupId;
}
