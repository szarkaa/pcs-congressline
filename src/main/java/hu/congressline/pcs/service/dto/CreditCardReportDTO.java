package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.ChargedService;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class CreditCardReportDTO implements Serializable {

    private Long id;
    private Integer regId;
    private String name;
    private BigDecimal amount;
    private String currency;
    private LocalDate dateOfPayment;
    private String cardNumber;
    private String transactionId;
    private String congressName;
    private String programNumber;
    private String comment;

    public CreditCardReportDTO(ChargedService service) {
        this.id = service.getId();
        this.regId = service.getRegistration().getRegId();
        this.name = service.getRegistration().getLastName() + " " + service.getRegistration().getFirstName();
        this.amount = service.getAmount();
        this.currency = service.getChargeableItem() != null ? service.getChargeableItem().getChargeableItemCurrency() : null;
        this.dateOfPayment = service.getDateOfPayment();
        this.cardNumber = service.getCardNumber();
        this.transactionId = service.getTransactionId();
        this.congressName = service.getRegistration().getCongress().getName();
        this.programNumber = service.getRegistration().getCongress().getProgramNumber();
        this.comment = service.getComment();
    }

}
