package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class OptionalServiceApplicantsDTO implements Serializable {
    private Long id;
    private Integer regId;
    private String firstName;
    private String lastName;
    private Integer numOfApplicants;
    private String country;
    private String currency;
    private BigDecimal orderedPrice;
    private BigDecimal paid;
    private String hotel;
    private String payingGroupName;
    private BigDecimal groupCost;
    private BigDecimal paidByGroup;
}
