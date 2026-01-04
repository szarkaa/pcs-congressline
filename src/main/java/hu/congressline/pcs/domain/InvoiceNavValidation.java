package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.InvoiceNavValidationSeverity;
import hu.congressline.pcs.domain.enumeration.InvoiceNavValidationType;
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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "invoice_nav_validation")
public class InvoiceNavValidation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "invoice", nullable = false)
    private Invoice invoice;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_type", nullable = false)
    private InvoiceNavValidationType validationType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "validation_severity", nullable = false)
    private InvoiceNavValidationSeverity validationSeverity;

    @Size(max = 255)
    @Column(name = "error_code", length = 255)
    private String errorCode;

    @Column(name = "message")
    private String message;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InvoiceNavValidation that = (InvoiceNavValidation) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "InvoiceNavValidation{" + "id=" + id + "}";
    }
}
