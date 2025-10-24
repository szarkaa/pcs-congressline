package hu.congressline.pcs.web.rest.vm;

import java.util.Objects;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SharedRoomReservationVM {
    private Long rrId;
    private Long registrationId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SharedRoomReservationVM that = (SharedRoomReservationVM) o;
        return Objects.equals(rrId, that.rrId) && Objects.equals(registrationId, that.registrationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rrId, registrationId);
    }
}
