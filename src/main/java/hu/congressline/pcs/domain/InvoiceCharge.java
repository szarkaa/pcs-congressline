package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "invoice_charge")
public class InvoiceCharge implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_desc")
    private String itemDesc;

    @NotNull
    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @ManyToOne
    @JoinColumn(name = "invoice")
    private Invoice invoice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ChargeableItemType itemType;

    @SuppressWarnings("MissingJavadocMethod")
    public static InvoiceCharge stornoCharge(InvoiceCharge charge) {
        InvoiceCharge copy = new InvoiceCharge();
        copy.setItemType(charge.getItemType());
        copy.setItemName(charge.getItemName());
        copy.setItemDesc(charge.getItemDesc());
        copy.setAmount(charge.getAmount() != null ? charge.getAmount().negate() : charge.getAmount());
        copy.setCurrency(charge.getCurrency());
        copy.setInvoice(charge.getInvoice());
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
        InvoiceCharge invoiceCharge = (InvoiceCharge) o;
        if (invoiceCharge.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoiceCharge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
