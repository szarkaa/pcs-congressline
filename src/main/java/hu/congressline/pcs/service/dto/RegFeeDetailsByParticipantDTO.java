package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegFeeDetailsByParticipantDTO implements Serializable {
    private Integer regId;
    private String name;
    private String payingGroupName;
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
        if (!(o instanceof RegFeeDetailsByParticipantDTO)) {
            return false;
        }
        RegFeeDetailsByParticipantDTO that = (RegFeeDetailsByParticipantDTO) o;
        return Objects.equals(getRegId(), that.getRegId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRegId());
    }
}
