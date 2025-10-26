package hu.congressline.pcs.service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SendAllConfirmationPdfToEmailDTO {
    private Integer regId;
    private byte[] pdfBytes;

    public SendAllConfirmationPdfToEmailDTO(Integer regId, byte[] pdfBytes) {
        this.regId = regId;
        this.pdfBytes = pdfBytes;
    }
}
