package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class StripePaymentResultVM {
    String paymentId;
    String trxId;
    String errorMessage;
}
