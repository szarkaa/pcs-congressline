package hu.congressline.pcs.service.dto;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomDTO {
    private Long id;
    private String roomType;
    private Integer bed;
    private Integer quantity;
    private BigDecimal price;
    private CurrencyDTO currency;
    private VatInfoDTO vatInfo;
    private OnlineVisibility onlineVisibility;
    private String onlineLabel;
    private String onlineExternalLink;
    private String onlineExternalEmail;
    private String hotelName;
    private Long congressHotelId;
    private List<RoomReservationEntryDTO> reservations = Collections.emptyList();

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.congressHotelId = room.getCongressHotel().getId();
        this.roomType = room.getRoomType();
        this.bed = room.getBed();
        this.quantity = room.getQuantity();
        this.price = room.getPrice();
        this.currency = room.getCurrency() != null ? new CurrencyDTO(room.getCurrency()) : null;
        this.vatInfo = room.getVatInfo() != null ? new VatInfoDTO(room.getVatInfo()) : null;
        this.onlineVisibility = room.getOnlineVisibility();
        this.onlineLabel = room.getOnlineLabel();
        this.onlineExternalLink = room.getOnlineExternalLink();
        this.onlineExternalEmail = room.getOnlineExternalEmail();
        this.hotelName = room.getCongressHotel().getHotel().getName();
    }

}
