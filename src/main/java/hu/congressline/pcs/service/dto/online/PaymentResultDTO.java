package hu.congressline.pcs.service.dto.online;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PaymentResultDTO {
    private String congressName;
    private String congressUuid;
    private String website;
    private String colorCode;
    private String paymentTrxResultCode;
    private String paymentTrxResultMessage;
    private String paymentTrxStatus;
    private BigDecimal amount;
    private String currency;
}
