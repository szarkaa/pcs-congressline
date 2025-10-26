package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentSummaryDTO implements Serializable {
    private String currency;
    private BigDecimal registrationFee = BigDecimal.ZERO;
    private BigDecimal reservationFee = BigDecimal.ZERO;
    private BigDecimal optionalFee = BigDecimal.ZERO;
    private BigDecimal miscFee = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentSummaryDTO)) {
            return false;
        }
        PaymentSummaryDTO that = (PaymentSummaryDTO) o;
        return Objects.equals(getCurrency(), that.getCurrency());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCurrency());
    }
}
