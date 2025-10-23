package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.VatRateType;
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
@Table(name = "vat_info")
public class VatInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Size(min = 3, max = 128)
    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "vat", nullable = false)
    private Integer vat;

    @Size(max = 20)
    @Column(name = "szj", length = 20)
    private String szj;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chargeable_item_type", nullable = false)
    private ChargeableItemType chargeableItemType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vat_rate_type", nullable = false, length = 32)
    private VatRateType vatRateType;

    @Size(max = 200)
    @Column(name = "vat_exception_reason", length = 200)
    private String vatExceptionReason;

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
        VatInfo vatInfo = (VatInfo) o;
        if (vatInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, vatInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "VatInfo{" + "id=" + id + "}";
    }
}
