package hu.congressline.pcs.web.rest.vm;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ConfirmationPdfVM {

    private String customConfirmationEmail;
    private ConfirmationTitleType confirmationTitleType;
    private String language;
    private Long registrationId;
    private Long congressId;
    private String optionalText;
    private List<Long> ignoredChargeableItemIdList;
    private List<Long> ignoredChargedServiceIdList;

}
