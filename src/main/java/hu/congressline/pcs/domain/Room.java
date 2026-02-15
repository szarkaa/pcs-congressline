package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.OnlineVisibility;
import hu.congressline.pcs.web.rest.vm.RoomVM;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NonNull;

@Data
@Entity
@Table(name = "room")
public class Room implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 32)
    @Column(name = "room_type", length = 32, nullable = false)
    private String roomType;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "bed", nullable = false)
    private Integer bed;

    @NotNull
    @Min(0)
    @Max(1000)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @ManyToOne
    private Currency currency;

    @ManyToOne
    private VatInfo vatInfo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "online_visibility", nullable = false)
    private OnlineVisibility onlineVisibility;

    @Size(max = 200)
    @Column(name = "online_label", length = 200)
    private String onlineLabel;

    @Column(name = "online_external_link")
    private String onlineExternalLink;

    @Size(max = 64)
    @Column(name = "online_external_email", length = 64)
    private String onlineExternalEmail;

    @ManyToOne
    private CongressHotel congressHotel;

    @SuppressWarnings("MissingJavadocMethod")
    public void update(@NonNull RoomVM viewModel) {
        this.roomType = viewModel.getRoomType();
        this.bed = viewModel.getBed();
        this.quantity = viewModel.getQuantity();
        this.price = viewModel.getPrice();
        this.onlineExternalEmail = viewModel.getOnlineExternalEmail();
        this.onlineLabel = viewModel.getOnlineLabel();
        this.onlineVisibility = viewModel.getOnlineVisibility();
        this.onlineExternalLink = viewModel.getOnlineExternalLink();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Room copy(Room room) {
        Room copy = new Room();
        copy.setRoomType(room.getRoomType());
        copy.setBed(room.getBed());
        copy.setQuantity(room.getQuantity());
        copy.setPrice(room.getPrice());
        copy.setCurrency(room.getCurrency());
        copy.setVatInfo(room.getVatInfo());
        copy.setOnlineVisibility(room.getOnlineVisibility());
        copy.setOnlineLabel(room.getOnlineLabel());
        copy.setOnlineExternalLink(room.getOnlineExternalLink());
        copy.setOnlineExternalEmail(room.getOnlineExternalEmail());
        copy.setCongressHotel(room.getCongressHotel());
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Room room = (Room) o;
        if (room.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, room.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Room{" + "id=" + id + "}";
    }

}
