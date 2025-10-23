package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "paying_group_item")
public class PayingGroupItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Min(0)
    @Max(100)
    @Column(name = "amount_percentage")
    private Integer amountPercentage;

    @Min(0)
    @Max(100000000)
    @Column(name = "amount_value")
    private Integer amountValue;

    @Column(name = "hotel_date_from")
    private LocalDate hotelDateFrom;

    @Column(name = "hotel_date_to")
    private LocalDate hotelDateTo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chargeable_item_type", nullable = false)
    private ChargeableItemType chargeableItemType;

    @ManyToOne
    private PayingGroup payingGroup;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PayingGroupItem payingGroupItem = (PayingGroupItem) o;
        if (payingGroupItem.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, payingGroupItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PayingGroupItem{" + "id=" + id + "}";
    }
}
