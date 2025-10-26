package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomReservationByRoomsDTO implements Serializable {
    private Long id;
    private String regId;
    private String name;
    private String country;
    private String roomType;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private Integer nights;
    private BigDecimal personCost;
    private BigDecimal personPaid;
    private BigDecimal personToPay;
    private BigDecimal groupCost;
    private BigDecimal groupPaid;
    private BigDecimal groupToPay;
    private BigDecimal totalCost;
    private BigDecimal totalPaid;
    private BigDecimal totalToPay;
    private String payingGroupName;
    private String currency;
    private String comment;
}
