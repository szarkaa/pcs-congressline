package hu.congressline.pcs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serial;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "room_reservation_registration")
@DiscriminatorValue("HOTEL")
public class RoomReservationRegistration extends ChargeableItem {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    private RoomReservation roomReservation;

    @ManyToOne
    private Registration registration;

    @ManyToOne
    private PayingGroupItem payingGroupItem;

    @Override
    @JsonIgnore
    public String getChargeableItemName() {
        return getRoomReservation().getRoom().getRoomType();
    }

    @Override
    @JsonIgnore
    public BigDecimal getChargeableItemPrice() {
        long days = ChronoUnit.DAYS.between(getRoomReservation().getArrivalDate(), getRoomReservation().getDepartureDate());
        return getSharedPricePerNight().multiply(new BigDecimal(days)).setScale(Currency.HUF.toString()
                .equalsIgnoreCase(getChargeableItemCurrency()) ? 0 : 2, RoundingMode.HALF_UP);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @JsonIgnore
    public BigDecimal getSharedPricePerNight() {
        int rrrSize = Optional.ofNullable(getRoomReservation().getRoomReservationRegistrations()).map(List::size).orElse(1);
        return getRoomReservation().getRoom().getPrice().divide(new BigDecimal(rrrSize), Currency.HUF.toString()
                .equalsIgnoreCase(getChargeableItemCurrency()) ? 0 : 2, RoundingMode.HALF_UP);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @JsonIgnore
    public String getRoomMatesWithoutRegistration(Registration reg, Locale locale) {
        if (reg == null) {
            throw new IllegalArgumentException("Registration can not be null!");
        }
        StringBuilder sb = new StringBuilder();
        if (getRoomReservation().getRoomReservationRegistrations() != null) {
            boolean first = true;
            for (RoomReservationRegistration rrr : getRoomReservation().getRoomReservationRegistrations()) {
                if (reg.equals(rrr.getRegistration())) {
                    continue;
                }
                if (!first) {
                    sb.append(", ");
                }
                if ("hu".equals(locale.getLanguage())) {
                    sb.append(rrr.getRegistration().getLastName() + " " + rrr.getRegistration().getFirstName());
                } else {
                    sb.append(rrr.getRegistration().getFirstName() + " " + rrr.getRegistration().getLastName());
                }
                first = false;
            }
        }
        return sb.toString();
    }

    @Override
    @JsonIgnore
    public Integer getChargeableItemVAT() {
        return getRoomReservation().getRoom().getVatInfo().getVat();
    }

    @Override
    @JsonIgnore
    public String getChargeableItemSZJ() {
        return getRoomReservation().getRoom().getVatInfo().getSzj();
    }

    @Override
    @JsonIgnore
    public String getChargeableItemCurrency() {
        return getRoomReservation().getRoom().getCurrency().getCurrency();
    }

    @Override
    @JsonIgnore
    public ChargeableItemType getChargeableItemType() {
        return ChargeableItemType.HOTEL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoomReservationRegistration roomReservationRegistration = (RoomReservationRegistration) o;
        if (roomReservationRegistration.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), roomReservationRegistration.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "RoomReservationRegistration{" + "id=" + getId() + "}";
    }
}
