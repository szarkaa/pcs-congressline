package hu.congressline.pcs.service.dto;

import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.RoomReservationEntry;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomReservationEntryDTO {
    private Long id;
    private Long roomId;
    private Integer reserved = 0;
    private LocalDate reservationDate;

    public RoomReservationEntryDTO(RoomReservationEntry rre) {
        this.id = rre.getId();
        this.roomId = rre.getRoom().getId();
        this.reserved = rre.getReserved();
        this.reservationDate = rre.getReservationDate();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RoomReservationEntryDTO that = (RoomReservationEntryDTO) o;

        return Objects.equals(id, that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
