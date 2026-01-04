package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
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
@Table(name = "online_reg_discount_code")
public class OnlineRegDiscountCode implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 5, max = 32)
    @Column(name = "code", length = 32, nullable = false)
    private String code;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private ChargeableItemType discountType;

    @ManyToOne
    private Congress congress;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OnlineRegDiscountCode onlineRegDiscountCode = (OnlineRegDiscountCode) o;
        if (onlineRegDiscountCode.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, onlineRegDiscountCode.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "OnlineRegDiscountCode{" + "id=" + id + "}";
    }
}
