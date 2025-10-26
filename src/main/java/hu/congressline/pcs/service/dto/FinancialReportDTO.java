package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class FinancialReportDTO implements Serializable {
    private Long id;
    private Integer regId;
    private String name;
    private String regTypes;
    private String payingGroupName;
    private String countryCode;
    private String country;
    private String city;
    private String workplace;
    private String phone;
    private String email;
    private BigDecimal personCost;
    private BigDecimal personPaid;
    private BigDecimal personToPay;
    private BigDecimal groupCost;
    private BigDecimal groupPaid;
    private BigDecimal groupToPay;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private BigDecimal totalToPay;
    private String currency;
    private String remark;
}
