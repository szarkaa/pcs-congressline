package hu.congressline.pcs.service.pdf;

import java.util.List;
import java.util.Locale;

import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationOptionalService;
import hu.congressline.pcs.domain.OnlineRegistrationRegistrationType;

public class OnlineRegPdfContext extends PdfContext {

    private final OnlineRegistration onlineReg;
    private final List<OnlineRegistrationRegistrationType> orrts;
    private final List<OnlineRegistrationOptionalService> oros;

    public OnlineRegPdfContext(OnlineRegistration onlineReg, List<OnlineRegistrationRegistrationType> orrts, List<OnlineRegistrationOptionalService> oros) {
        super("", Locale.forLanguageTag("en"));
        this.onlineReg = onlineReg;
        this.orrts = orrts;
        this.oros = oros;
    }

    public OnlineRegistration getOnlineReg() {
        return onlineReg;
    }

    public List<OnlineRegistrationRegistrationType> getOrrts() {
        return orrts;
    }

    public List<OnlineRegistrationOptionalService> getOros() {
        return oros;
    }
}
