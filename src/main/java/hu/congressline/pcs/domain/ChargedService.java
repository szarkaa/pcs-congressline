package hu.congressline.pcs.domain;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.ChargedServicePaymentMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "charged_service")
public class ChargedService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_mode", nullable = false)
    private ChargedServicePaymentMode paymentMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private ChargeableItemType paymentType;

    @NotNull
    @Column(name = "date_of_payment", nullable = false)
    private LocalDate dateOfPayment;

    @NotNull
    @DecimalMax("-1000000")
    @DecimalMax("1000000")
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Size(max = 8)
    @Column(name = "card_type", length = 8)
    private String cardType;

    @Size(max = 50)
    @Column(name = "card_number", length = 50)
    private String cardNumber;

    @Size(max = 8)
    @Column(name = "card_expiration_date", length = 8)
    private String cardExpirationDate;

    @Size(max = 50)
    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @Column(name = "comment")
    private String comment;

    @ManyToOne
    private Registration registration;

    @ManyToOne
    private ChargeableItem chargeableItem;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChargedService chargedService = (ChargedService) o;
        if (chargedService.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, chargedService.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ChargedService{" + "id=" + id + "}";
    }
}
