package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegFeeDetailsDTO implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String currency;
    private int firstCount;
    private int secondCount;
    private int thirdCount;
    private BigDecimal firstFee = BigDecimal.ZERO;
    private BigDecimal secondFee = BigDecimal.ZERO;
    private BigDecimal thirdFee = BigDecimal.ZERO;
    private BigDecimal paidByPerson = BigDecimal.ZERO;
    private BigDecimal paidByGroup = BigDecimal.ZERO;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RegFeeDetailsDTO)) {
            return false;
        }
        RegFeeDetailsDTO that = (RegFeeDetailsDTO) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public BigDecimal getTotal() {
        return firstFee.add(secondFee).add(thirdFee).setScale(2, RoundingMode.HALF_UP);
    }
}
