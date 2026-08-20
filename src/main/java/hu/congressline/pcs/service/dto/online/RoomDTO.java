package hu.congressline.pcs.service.dto.online;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.Room;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoomDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String currency;
    private String onlineExternalLink;
    private String onlineExternalEmail;
    private Map<LocalDate, Long> reservations = Collections.emptyMap();

    public RoomDTO(Room room) {
        this.id = room.getId();
        this.name = room.getOnlineLabel() != null ? room.getOnlineLabel() : "";
        this.price = room.getPrice();
        this.quantity = room.getQuantity();
        this.currency = room.getCurrency().getCurrency();
        this.onlineExternalLink = room.getOnlineExternalLink();
        this.onlineExternalEmail = room.getOnlineExternalEmail();
    }
}
