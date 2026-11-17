package hu.congressline.pcs.web.rest.vm;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class SendMiscFinancialNoticeToSelectedVM extends GeneralRegistrationReportVM {
    private String sendAllEmail;
    private String language;
    private String optionalText;
    private Set<Long> miscInvoiceIds;
}
