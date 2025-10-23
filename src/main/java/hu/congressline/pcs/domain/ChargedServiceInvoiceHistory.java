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
@Table(name = "charged_service_invoice_history")
public class ChargedServiceInvoiceHistory implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "charged_service")
    private ChargedService chargedService;

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
        ChargedServiceInvoiceHistory invoiceHistory = (ChargedServiceInvoiceHistory) o;
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
