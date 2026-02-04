package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.NavVatCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "invoice")
public class Invoice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 64)
    @Column(name = "invoice_number", length = 64)
    private String invoiceNumber;

    @Size(max = 64)
    @Column(name = "storno_invoice_number", length = 64)
    private String stornoInvoiceNumber;

    @Size(max = 64)
    @Column(name = "name1", length = 64)
    private String name1;

    @Size(max = 64)
    @Column(name = "name2", length = 64)
    private String name2;

    @Size(max = 64)
    @Column(name = "name3", length = 64)
    private String name3;

    @Size(max = 64)
    @Column(name = "optional_name", length = 64)
    private String optionalName;

    @Size(max = 64)
    @Column(name = "vat_reg_number", length = 64)
    private String vatRegNumber;

    @Size(max = 64)
    @Column(name = "city", length = 64)
    private String city;

    @Size(max = 64)
    @Column(name = "zip_code", length = 64)
    private String zipCode;

    @Size(max = 128)
    @Column(name = "street", length = 128)
    private String street;

    @Size(max = 64)
    @Column(name = "country", length = 64)
    private String country;

    @Size(max = 3)
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Size(max = 2048)
    @Column(name = "optional_text", length = 2048)
    private String optionalText;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "date_of_fulfilment")
    private LocalDate dateOfFulfilment;

    @Column(name = "payment_deadline")
    private LocalDate paymentDeadline;

    @Size(max = 12)
    @Column(name = "billing_method", length = 12)
    private String billingMethod;

    @NotNull
    @Size(min = 3, max = 32)
    @Column(name = "bank_name", length = 32, nullable = false)
    private String bankName;

    @NotNull
    @Size(min = 8, max = 32)
    @Column(name = "bank_account", length = 32, nullable = false)
    private String bankAccount;

    @Size(min = 8, max = 250)
    @Column(name = "bank_address", length = 250)
    private String bankAddress;

    @NotNull
    @Size(max = 32)
    @Column(name = "swift_code", length = 32)
    private String swiftCode;

    @Size(max = 8)
    @Column(name = "print_locale", length = 8)
    private String printLocale;

    @Column(name = "storno")
    private Boolean storno = Boolean.FALSE;

    @Column(name = "stornired")
    private Boolean stornired = Boolean.FALSE;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @DecimalMin("0")
    @Column(name = "exchange_rate", precision = 10, scale = 2)
    private BigDecimal exchangeRate;

    @Size(max = 30)
    @Column(name = "nav_trx_id", length = 30)
    private String navTrxId;

    @Enumerated(EnumType.STRING)
    @Column(name = "nav_status")
    private InvoiceNavStatus navStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "nav_vat_category", nullable = false)
    private NavVatCategory navVatCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "invoice_type", nullable = false)
    private InvoiceType invoiceType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Invoice invoice = (Invoice) o;
        if (invoice.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Invoice{" + "id=" + id + "}";
    }
}
