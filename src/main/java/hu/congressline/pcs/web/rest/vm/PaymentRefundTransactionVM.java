package hu.congressline.pcs.web.rest.vm;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentRefundTransactionVM {
    private String txId;
    private BigDecimal amount;
    private String currency;

}
