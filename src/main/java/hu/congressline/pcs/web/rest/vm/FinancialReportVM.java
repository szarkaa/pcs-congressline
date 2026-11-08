package hu.congressline.pcs.web.rest.vm;

import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class FinancialReportVM extends GeneralReportVM {
    private Boolean participantsToPay;
    private Set<Long> registrationIds;
}
