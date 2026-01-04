package hu.congressline.pcs.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "payment_refund_transaction")
public class PaymentRefundTransaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", length = 15)
    private String transactionId;

    @Column(name = "payment_trx_result_code", length = 10)
    private String paymentTrxResultCode;

    @Column(name = "payment_trx_status", length = 32)
    private String paymentTrxStatus;

    @Column(name = "payment_trx_result_message", length = 100)
    private String paymentTrxResultMessage;

    @Column(name = "payment_trx_auth_code", length = 32)
    private String paymentTrxAuthCode;

    @Column(name = "bank_auth_number", length = 32)
    private String bankAuthNumber;

    @Column(name = "payment_trx_date")
    private ZonedDateTime paymentTrxDate;

    @NotNull
    @DecimalMax("-1000000")
    @DecimalMax("1000000")
    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PaymentRefundTransaction that = (PaymentRefundTransaction) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
