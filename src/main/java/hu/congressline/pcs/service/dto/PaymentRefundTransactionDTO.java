package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import hu.congressline.pcs.domain.PaymentRefundTransaction;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentRefundTransactionDTO implements Serializable {

    private Long id;
    private String paymentTrxResultCode;
    private String paymentTrxResultMessage;
    private String paymentTrxStatus;
    private ZonedDateTime paymentTrxDate;
    private BigDecimal amount;
    private String currency;

    public PaymentRefundTransactionDTO(PaymentRefundTransaction transaction) {
        this.id = transaction.getId();
        this.paymentTrxResultCode = transaction.getPaymentTrxResultCode();
        this.paymentTrxResultMessage = transaction.getPaymentTrxResultMessage();
        this.paymentTrxStatus = transaction.getPaymentTrxStatus();
        this.paymentTrxDate = transaction.getPaymentTrxDate();
        this.amount = transaction.getAmount();
        this.currency = transaction.getCurrency();
    }
}
