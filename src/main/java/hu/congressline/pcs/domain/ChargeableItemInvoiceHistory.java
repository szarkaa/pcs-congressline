package hu.congressline.pcs.domain;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "chargeable_item_invoice_history")
public class ChargeableItemInvoiceHistory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chargeable_item")
    private ChargeableItem chargeableItem;

    @ManyToOne
    @JoinColumn(name = "invoice")
    private Invoice invoice;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChargeableItemInvoiceHistory invoiceHistory = (ChargeableItemInvoiceHistory) o;
        if (invoiceHistory.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoiceHistory.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
