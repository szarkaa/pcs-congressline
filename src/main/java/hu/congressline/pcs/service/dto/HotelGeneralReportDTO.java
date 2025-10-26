package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HotelGeneralReportDTO implements Serializable {
    private Long id;
    private Integer regId;
    private String name;
    private String regTypes;
    private String payingGroupName;
    private String country;
    private String city;
    private String phone;
    private String email;
    private String hotelName;
    private String roomType;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private String roomMates;
}
