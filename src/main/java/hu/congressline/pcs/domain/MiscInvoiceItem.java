package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "misc_invoice_item")
public class MiscInvoiceItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(0)
    @Max(100000)
    @Column(name = "item_quantity", nullable = false)
    private Integer itemQuantity;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private MiscService miscService;

    @Column(name = "date_of_payment")
    private LocalDate dateOfPayment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MiscInvoiceItem miscInvoiceItem = (MiscInvoiceItem) o;
        if (miscInvoiceItem.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, miscInvoiceItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "MiscInvoiceItem{" + "id=" + id + "}";
    }
}
