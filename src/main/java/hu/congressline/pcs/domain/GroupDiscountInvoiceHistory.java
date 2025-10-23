package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "group_discount_invoice_history")
public class GroupDiscountInvoiceHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ChargeableItem chargeableItem;

    @ManyToOne
    private Invoice invoice;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupDiscountInvoiceHistory invoiceHistory = (GroupDiscountInvoiceHistory) o;
        if (invoiceHistory.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoiceHistory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GroupDiscountInvoiceHistory{" + "id=" + id + "}";
    }
}
