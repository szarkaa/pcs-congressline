package hu.congressline.pcs.web.rest.vm;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.ChargedServicePaymentMode;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChargedServiceVM implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChargedServicePaymentMode paymentMode;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ChargeableItemType paymentType;

    @NotNull
    private LocalDate dateOfPayment;

    @NotNull
    @DecimalMin("-1000000")
    @DecimalMax("1000000")
    private BigDecimal amount;

    @Size(max = 8)
    private String cardType;

    @Size(max = 50)
    private String cardNumber;

    @Size(max = 8)
    private String cardExpirationDate;

    @Size(max = 50)
    private String transactionId;

    private String comment;

    @NotNull
    private Long registrationId;

    private Long chargeableItemId;

    @Override
    public String toString() {
        return "ChargedServiceDTO{" + "id=" + id + "}";
    }
}
