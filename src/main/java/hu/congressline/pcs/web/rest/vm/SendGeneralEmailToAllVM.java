package hu.congressline.pcs.web.rest.vm;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SendGeneralEmailToAllVM extends GeneralRegistrationReportVM {
    private String topic;
    private String emailBody;
}
