package hu.congressline.pcs.service.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.RoomReservation;
import lombok.Data;
import lombok.NoArgsConstructor;

import static hu.congressline.pcs.domain.enumeration.Currency.HUF;

@NoArgsConstructor
@Data
public class SharedRoomReservationDTO {
    private Long id;
    private Long roomId;
    private String roomType;
    private String hotelName;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private BigDecimal price;
    private String currency;
    private String roomMates;

    public SharedRoomReservationDTO(RoomReservation rr) {
        this.id = rr.getId();
        this.roomId = rr.getRoom().getId();
        this.roomType = rr.getRoom().getRoomType();
        this.hotelName = rr.getRoom().getCongressHotel().getHotel().getName();
        this.arrivalDate = rr.getArrivalDate();
        this.departureDate = rr.getDepartureDate();
        long days = ChronoUnit.DAYS.between(rr.getArrivalDate(), rr.getDepartureDate());
        int rrrSize = Optional.ofNullable(rr.getRoomReservationRegistrations()).map(List::size).orElse(1);
        int scale = HUF.toString().equalsIgnoreCase(rr.getRoom().getCurrency().getCurrency()) ? 0 : 2;
        BigDecimal priceForOneNight = rr.getRoom().getPrice().divide(new BigDecimal(++rrrSize), scale, RoundingMode.HALF_UP);
        this.price = priceForOneNight.multiply(new BigDecimal(days)).setScale(scale, RoundingMode.HALF_UP);
        this.currency = rr.getRoom().getCurrency().getCurrency();
        this.roomMates = "";
        if (rr.getRoomReservationRegistrations() != null) {
            this.roomMates = rr.getRoomReservationRegistrations().stream()
                    .map(o -> o.getRegistration().getFirstName() + ", " + o.getRegistration().getLastName()).collect(Collectors.joining("; "));
        }
    }

}
