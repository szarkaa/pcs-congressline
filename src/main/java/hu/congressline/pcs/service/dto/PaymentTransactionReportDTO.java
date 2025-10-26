package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

import hu.congressline.pcs.domain.PaymentTransaction;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentTransactionReportDTO implements Serializable {

    private Long id;
    private String congressName;
    private BigDecimal amount;
    private String currency;
    private ZonedDateTime paymentTrxDate;
    private String orderNumber;
    private String transactionId;
    private String transactionStatus;
    private String lastName;
    private String firstName;
    private boolean hasRefundTransaction;

    public PaymentTransactionReportDTO(PaymentTransaction paymentTransaction) {
        this.id = paymentTransaction.getId();
        this.lastName = paymentTransaction.getLastName();
        this.firstName = paymentTransaction.getFirstName();
        this.congressName = paymentTransaction.getCongress().getName();
        this.amount = paymentTransaction.getAmount();
        this.currency = paymentTransaction.getCurrency();
        this.paymentTrxDate = paymentTransaction.getPaymentTrxDate();
        this.transactionId = paymentTransaction.getTransactionId();
        this.transactionStatus = paymentTransaction.getPaymentTrxStatus();
        this.orderNumber = paymentTransaction.getPaymentOrderNumber();
    }
}
