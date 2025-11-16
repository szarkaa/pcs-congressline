package hu.congressline.pcs.service.pdf;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.web.rest.vm.ConfirmationTitleType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationPdfContext extends PdfContext {

    private ConfirmationTitleType confirmationTitleType;
    private Registration registration;
    private Map<ChargeableItemType, BigDecimal> chargedServiceAmountsMap;
    private List<Long> ignoredChargeableItemIdList;
    private Map<PayingGroupItem, BigDecimal> activePayingGroups = new HashMap<>();

    public ConfirmationPdfContext() {
        ignoredChargeableItemIdList = new ArrayList<>();
        chargedServiceAmountsMap = new HashMap<>();
        activePayingGroups = new HashMap<>();
    }

    public ConfirmationPdfContext(String optionalText, ConfirmationTitleType confirmationTitleType, String language, Registration registration,
                                  List<Long> ignoredChargeableItemIdList, Map<ChargeableItemType, BigDecimal> chargedServiceAmountsMap) {
        super(optionalText, new Locale(language));
        this.confirmationTitleType = confirmationTitleType;
        this.registration = registration;
        this.ignoredChargeableItemIdList = ignoredChargeableItemIdList;
        this.chargedServiceAmountsMap = chargedServiceAmountsMap;
        setContactEmail(registration.getCongress().getContactEmail());
    }
}
