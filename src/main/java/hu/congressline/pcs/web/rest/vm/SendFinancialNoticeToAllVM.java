package hu.congressline.pcs.web.rest.vm;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SendFinancialNoticeToAllVM extends GeneralRegistrationReportVM {
    private String sendAllEmail;
    private String language;
    private String optionalText;
    private Set<Long> registrationIds;
}
