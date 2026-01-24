package hu.congressline.pcs.web.rest.vm;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomReservationVM {
    private Long id;
    @NotNull
    private Long roomId;
    private Boolean shared;
    @NotNull
    private LocalDate arrivalDate;
    @NotNull
    private LocalDate departureDate;
    private String comment;
    private Long payingGroupItemId;
    @NotNull
    private Long registrationId;
}
