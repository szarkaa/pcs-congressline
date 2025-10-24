package hu.congressline.pcs.web.rest.vm;

import java.time.LocalDate;
import java.util.List;

import hu.congressline.pcs.domain.BankAccount;
import hu.congressline.pcs.domain.enumeration.NavVatCategory;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class InvoiceVM {
    private String name1;
    private String name2;
    private String vatRegNumber;
    private String city;
    private String zipCode;
    private String street;
    private String country;
    private String optionalText;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dateOfFulfilment;
    private LocalDate paymentDeadline;
    private String billingMethod;
    private String language;
    private String customInvoiceEmail;
    private LocalDate createdDate;
    @NotNull
    private Long registrationId;
    private BankAccount bankAccount;
    @NotNull
    private NavVatCategory navVatCategory;
    private List<Long> ignoredChargeableItemIdList;
    private List<Long> ignoredChargedServiceIdList;

}
