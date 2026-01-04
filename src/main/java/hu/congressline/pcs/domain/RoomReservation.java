package hu.congressline.pcs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "room_reservation")
public class RoomReservation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shared")
    private Boolean shared;

    @NotNull
    @Column(name = "arrival_date", nullable = false)
    private LocalDate arrivalDate;

    @NotNull
    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    @ManyToOne
    private Room room;

    @JsonIgnore
    @OneToMany(mappedBy = "roomReservation", fetch = FetchType.EAGER, orphanRemoval = true)
    private List<RoomReservationRegistration> roomReservationRegistrations;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoomReservation that = (RoomReservation) o;
        if (that.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RoomReservation{" + "id=" + getId() + "}";
    }
}
