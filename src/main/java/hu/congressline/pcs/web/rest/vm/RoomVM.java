package hu.congressline.pcs.web.rest.vm;

import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.domain.VatInfo;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class RoomVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(max = 32)
    private String roomType;

    @NotNull
    @Min(0)
    @Max(100)
    private Integer bed;

    @NotNull
    @Min(0)
    @Max(1000)
    private Integer quantity;

    @NotNull
    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    private BigDecimal price;

    private Currency currency;

    @NotNull
    private OnlineVisibility onlineVisibility;

    @Size(max = 200)
    private String onlineLabel;

    private String onlineExternalLink;

    @Size(max = 64)
    private String onlineExternalEmail;

    @NotNull
    private Long vatInfoId;

    @NotNull
    private Long currencyId;

    @NotNull
    private Long congressHotelId;

    @Override
    public String toString() {
        return "RoomVM{" + "id=" + id + "}";
    }
}
