package hu.congressline.pcs.service.pdf;

import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Objects;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ConfirmationPdfHeaderFooterTextContext {
    private final Locale locale;

    private final String companyNameLabel;
    private final String companyAddress1Label;
    private final String companyAddress2Label;
    private final String companyEmailLabel;
    private final String companyPhoneLabel;
    private final String companyWebsiteLabel;
    private final String companyTaxNumberLabel;

    private final String bankNameLabel;
    private final String bankAddressLabel;
    private final String bankSwiftCodeLabel;
    private final String bankAccountHufLabel;
    private final String bankAccountEurLabel;

    private final String contactEmailValue;
    private final String pageNumberLabel;

    public ConfirmationPdfHeaderFooterTextContext(@NonNull MessageSource messageSource, @NonNull Locale locale, String contactEmail) {
        this.locale = locale;
        this.companyNameLabel = messageSource.getMessage("confirmation.pdf.footer.company.name", new Object[]{}, locale);
        this.companyAddress1Label = messageSource.getMessage("confirmation.pdf.footer.company.address1", new Object[]{}, locale);
        this.companyAddress2Label = messageSource.getMessage("confirmation.pdf.footer.company.address2", new Object[]{}, locale);
        this.companyEmailLabel = messageSource.getMessage("confirmation.pdf.footer.company.email", new Object[]{}, locale);
        this.companyPhoneLabel = messageSource.getMessage("confirmation.pdf.footer.company.phone", new Object[]{}, locale);
        this.companyWebsiteLabel = messageSource.getMessage("confirmation.pdf.footer.company.website", new Object[]{}, locale);
        this.companyTaxNumberLabel = messageSource.getMessage("confirmation.pdf.footer.company.taxNo", new Object[]{}, locale);
        this.bankNameLabel = messageSource.getMessage("confirmation.pdf.footer.bank.name", new Object[]{}, locale);
        this.bankAddressLabel = messageSource.getMessage("confirmation.pdf.footer.bank.address", new Object[]{}, locale);
        this.bankSwiftCodeLabel = messageSource.getMessage("confirmation.pdf.footer.bank.swiftCode", new Object[]{}, locale);
        this.bankAccountHufLabel = messageSource.getMessage("confirmation.pdf.footer.bank.accountHuf", new Object[]{}, locale);
        this.bankAccountEurLabel = messageSource.getMessage("confirmation.pdf.footer.bank.accountEur", new Object[]{}, locale);

        this.contactEmailValue = Objects.toString(contactEmail, messageSource.getMessage("confirmation.pdf.footer.contact.defaultEmail", new Object[]{}, locale));
        this.pageNumberLabel = messageSource.getMessage("confirmation.pdf.footer.pageNumber", new Object[]{}, locale);
    }

}
