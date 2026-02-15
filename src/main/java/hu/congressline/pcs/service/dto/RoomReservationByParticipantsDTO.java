package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomReservationByParticipantsDTO implements Serializable {
    private Long id;
    private Integer regId;
    private String lastName;
    private String firstName;
    private String email;
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
