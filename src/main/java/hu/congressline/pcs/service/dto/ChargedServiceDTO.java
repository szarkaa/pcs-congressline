package hu.congressline.pcs.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.ChargedServicePaymentMode;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ChargedServiceDTO {
    private Long id;
    private ChargedServicePaymentMode paymentMode;
    private ChargeableItemType paymentType;
    private LocalDate dateOfPayment;
    private BigDecimal amount;
    private String cardType;
    private String cardNumber;
    private String cardExpirationDate;
    private String transactionId;
    private String comment;
    private Long registrationId;
    private Long chargeableItemId;
    private String chargeableItemName;
    private String invoiceNumber;

    public ChargedServiceDTO(ChargedService chargedService) {
        this.id = chargedService.getId();
        this.paymentMode = chargedService.getPaymentMode();
        this.paymentType = chargedService.getPaymentType();
        this.dateOfPayment = chargedService.getDateOfPayment();
        this.amount = chargedService.getAmount();
        this.cardType = chargedService.getCardType();
        this.cardNumber = chargedService.getCardNumber();
        this.cardExpirationDate = chargedService.getCardExpirationDate();
        this.transactionId = chargedService.getTransactionId();
        this.comment = chargedService.getComment();
        this.registrationId = chargedService.getRegistration().getId();
        if (chargedService.getChargeableItem() != null) {
            this.chargeableItemId = chargedService.getChargeableItem().getId();
            this.chargeableItemName = chargedService.getChargeableItem().getChargeableItemName();
        }
    }
}
