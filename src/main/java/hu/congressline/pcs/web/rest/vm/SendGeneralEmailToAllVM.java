package hu.congressline.pcs.web.rest.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SendGeneralEmailToAllVM extends GeneralRegistrationReportVM {
    private String topic;
    private String emailBody;
}
