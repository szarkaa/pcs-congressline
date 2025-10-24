package hu.congressline.pcs.web.rest.vm;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.service.util.DateInterval;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomReservationVM {
    private Long id;
    private Long roomId;
    private Long registrationId;
    private PayingGroupItem payingGroupItem;
    private String roomType;
    private String hotelName;
    private Boolean shared;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private BigDecimal price;
    private String currency;
    private Integer chargeableItemVAT;
    private String chargeableItemSZJ;
    private String payingGroupName;
    private BigDecimal priceWithDiscount;
    private String roomMates;
    private String comment;

    public RoomReservationVM(RoomReservationRegistration rrr) {
        this.id = rrr.getId();
        this.roomId = rrr.getRoomReservation().getRoom().getId();
        this.registrationId = rrr.getRegistration().getId();
        this.payingGroupItem = rrr.getPayingGroupItem();
        this.roomType = rrr.getRoomReservation().getRoom().getRoomType();
        this.hotelName = rrr.getRoomReservation().getRoom().getCongressHotel().getHotel().getName();
        this.shared = rrr.getRoomReservation().getShared();
        this.arrivalDate = rrr.getRoomReservation().getArrivalDate();
        this.departureDate = rrr.getRoomReservation().getDepartureDate();

        this.price = rrr.getSharedPricePerNight();
        this.currency = rrr.getRoomReservation().getRoom().getCurrency().getCurrency();
        this.chargeableItemVAT = rrr.getRoomReservation().getRoom().getVatInfo().getVat();
        this.chargeableItemSZJ = rrr.getRoomReservation().getRoom().getVatInfo().getSzj();
        this.payingGroupName = rrr.getPayingGroupItem() != null ? rrr.getPayingGroupItem().getPayingGroup().getName() + "/" + rrr.getPayingGroupItem().getName() : null;
        this.roomMates = "";
        if (rrr.getRoomReservation().getRoomReservationRegistrations() != null) {
            this.roomMates = rrr.getRoomReservation().getRoomReservationRegistrations().stream().filter(o -> !o.getRegistration().equals(rrr.getRegistration()))
                .map(o -> o.getRegistration().getFirstName() + ", " + o.getRegistration().getLastName()).collect(Collectors.joining("; "));
        }
        this.comment = rrr.getComment();
    }

    public String getChargeableItemName() {
        return roomType;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public BigDecimal getChargeableItemPrice() {
        DateInterval roomReservationInterval = new DateInterval(arrivalDate, departureDate);
        if (roomReservationInterval.length() > 0) {
            return price.multiply(new BigDecimal(roomReservationInterval.length())).setScale(2);
        } else {
            return price;
        }
    }

    public Integer getChargeableItemVAT() {
        return chargeableItemVAT;
    }

    public String getChargeableItemSZJ() {
        return chargeableItemSZJ;
    }

    public String getChargeableItemCurrency() {
        return currency;
    }

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

        RoomReservationVM that = (RoomReservationVM) o;
        return Objects.equals(id, that.id);

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
