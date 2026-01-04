package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "invoice_item")
public class InvoiceItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "item_desc")
    private String itemDesc;

    @Size(max = 20)
    @Column(name = "szj", length = 20)
    private String szj;

    @NotNull
    @Size(min = 3, max = 3)
    @Column(name = "currency", length = 3, nullable = false)
    private String currency;

    @Column(name = "unit")
    private Integer unit;

    @Column(name = "unit_of_measure")
    private String unitOfMeasure;

    @NotNull
    //@DecimalMax("0")
    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @NotNull
    //@DecimalMax("0")
    @Column(name = "vat_base", precision = 10, scale = 2, nullable = false)
    private BigDecimal vatBase;

    @NotNull
    @Min(0)
    @Max(100)
    @Column(name = "vat", nullable = false)
    private Integer vat;

    @NotNull
    private String vatName;

    @NotNull
    //@DecimalMax("0")
    @Column(name = "vat_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal vatValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "vat_rate_type", nullable = false, length = 32)
    private VatRateType vatRateType;

    @Size(max = 200)
    @Column(name = "vat_exception_reason", length = 200)
    private String vatExceptionReason;

    @NotNull
    //@DecimalMax("0")
    @Column(name = "total", precision = 10, scale = 2, nullable = false)
    private BigDecimal total;

    @ManyToOne
    @JoinColumn(name = "invoice")
    private Invoice invoice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ChargeableItemType itemType;

    @SuppressWarnings("MissingJavadocMethod")
    public static InvoiceItem stornoItem(InvoiceItem item, Invoice invoice) {
        InvoiceItem copy = new InvoiceItem();
        copy.setItemType(item.getItemType());
        copy.setVatRateType(item.getVatRateType());
        copy.setVatExceptionReason(item.getVatExceptionReason());
        copy.setItemName(item.getItemName());
        copy.setItemDesc(item.getItemDesc());
        copy.setSzj(item.getSzj());
        copy.setCurrency(item.getCurrency());
        copy.setUnit(item.getUnit());
        copy.setUnitOfMeasure(item.getUnitOfMeasure());
        copy.setUnitPrice(item.getUnitPrice() != null ? item.getUnitPrice().negate() : item.getUnitPrice());
        copy.setVatBase(item.getVatBase() != null ? item.getVatBase().negate() : item.getVatBase());
        copy.setVat(item.getVat());
        copy.setVatName(item.getVatName());
        copy.setVatValue(item.getVatValue() != null ? item.getVatValue().negate() : item.getVatValue());
        copy.setTotal(item.getTotal() != null ? item.getTotal().negate() : item.getTotal());
        copy.setInvoice(invoice);
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
        InvoiceItem invoiceItem = (InvoiceItem) o;
        if (invoiceItem.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoiceItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
