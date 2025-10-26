package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class HotelSummaryCellDTO implements Serializable {
    private LocalDate reservationDate;
    private Long roomId;
    private String roomType;
    private Integer nights;

    public HotelSummaryCellDTO(LocalDate reservationDate, Long roomId, String roomType) {
        this.reservationDate = reservationDate;
        this.roomId = roomId;
        this.roomType = roomType;
        this.nights = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HotelSummaryCellDTO that = (HotelSummaryCellDTO) o;
        return Objects.equals(reservationDate, that.reservationDate) && Objects.equals(roomId, that.roomId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationDate, roomId);
    }
}
