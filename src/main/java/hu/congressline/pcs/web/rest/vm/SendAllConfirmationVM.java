package hu.congressline.pcs.web.rest.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SendAllConfirmationVM extends GeneralRegistrationReportVM {
    private String sendAllEmail;
    private String language;
    private String optionalText;
}
