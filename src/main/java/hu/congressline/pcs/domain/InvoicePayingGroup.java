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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "invoice_paying_group")
public class InvoicePayingGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    private Invoice invoice;

    @NotNull
    @ManyToOne
    private PayingGroup payingGroup;

    @Column(name = "date_of_group_payment")
    private LocalDate dateOfGroupPayment;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoicePayingGroup invoicePayingGroup = (InvoicePayingGroup) o;
        if (invoicePayingGroup.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoicePayingGroup.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "InvoicePayingGroup {" + "id=" + id + "}";
    }
}
