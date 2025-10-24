package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SendAllConfirmationVM extends GeneralRegistrationReportVM {
    private String sendAllEmail;
    private String language;
    private String optionalText;
}
