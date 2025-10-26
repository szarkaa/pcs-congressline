package hu.congressline.pcs.service.dto.online;

import java.io.Serializable;

import hu.congressline.pcs.domain.InvoiceNavValidation;
import hu.congressline.pcs.domain.enumeration.InvoiceNavValidationSeverity;
import hu.congressline.pcs.domain.enumeration.InvoiceNavValidationType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class InvoiceNavValidationDTO implements Serializable {

    private Long id;
    private Long invoiceId;
    private InvoiceNavValidationType validationType;
    private InvoiceNavValidationSeverity validationSeverity;
    private String errorCode;
    private String message;

    public InvoiceNavValidationDTO(InvoiceNavValidation validation) {
        this.id = validation.getId();
        this.invoiceId = validation.getInvoice().getId();
        this.validationType = validation.getValidationType();
        this.validationSeverity = validation.getValidationSeverity();
        this.errorCode = validation.getErrorCode();
        this.message = validation.getMessage();
    }
}
