package hu.congressline.pcs.service.pdf;

import org.springframework.context.MessageSource;

import java.util.Locale;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class InvoicePdfHeaderFooterTextContext {
    private final Locale locale;

    private final String mainTitleLabel;
    private final String supplierNameLabel;
    private final String supplierNameValue;
    private final String supplierAddressValue;
    private final String supplierAddress2Value;
    private final String supplierPhoneLabel;
    private final String supplierPhoneValue;
    private final String supplierFaxLabel;
    private final String supplierFaxValue;
    private final String supplierManagerLabel;
    private final String supplierManagerValue;
    private final String supplierEmailLabel;
    private final String supplierEmailValue;

    private final String customerLabel;
    private final String customerName1Value;
    private final String customerName2Value;
    private final String customerName3Value;
    private final String customerStreetValue;
    private final String customerCityValue;
    private final String customerZipCodeValue;
    private final String customerCountryValue;
    private final String customerVatRegNumberLabel;
    private final String customerVatRegNumberValue;

    private final String bankNameLabel;
    private final String bankNameValue;
    private final String bankAddressLabel;
    private final String bankAddressValue;
    private final String bankSwiftCodeLabel;
    private final String bankSwiftCodeValue;
    private final String bankAccountLabel;
    private final String bankAccountValue;
    private final String huTaxNoLabel;
    private final String huTaxNoValue;
    private final String euTaxNoLabel;
    private final String euTaxNoValue;
    private final String admissionNumberLabel;
    private final String admissionNumberValue;

    private final String invoiceGeneratedMsgLabel;
    private final String pageNumberLabel;

    public InvoicePdfHeaderFooterTextContext(@NonNull MessageSource messageSource, @NonNull PdfContext pdfContext) {
        this.locale = pdfContext.getLocale();
        //header
        if (pdfContext.getStorno()) {
            mainTitleLabel = messageSource.getMessage("invoice.pdf.invoiceStornoCaps", new Object[]{}, locale);
        } else {
            mainTitleLabel = switch (pdfContext.getInvoiceType()) {
                case PRO_FORMA -> messageSource.getMessage("invoice.pdf.proFormaInvoiceCaps", new Object[]{}, locale);
                case PREPAYMENT -> messageSource.getMessage("invoice.pdf.prePaymentInvoiceCaps", new Object[]{}, locale);
                default -> messageSource.getMessage("invoice.pdf.invoiceCaps", new Object[]{}, locale);
            };
        }

        this.supplierNameLabel = messageSource.getMessage("invoice.pdf.supplier", new Object[]{}, locale);
        this.supplierPhoneLabel = messageSource.getMessage("invoice.pdf.phone", new Object[]{}, locale);
        this.supplierFaxLabel = messageSource.getMessage("invoice.pdf.fax", new Object[]{}, locale);
        this.supplierManagerLabel = messageSource.getMessage("invoice.pdf.manager", new Object[]{}, locale);
        this.supplierEmailLabel = messageSource.getMessage("invoice.pdf.email", new Object[]{}, locale);

        this.supplierNameValue = pdfContext.getCompany().getName();
        this.supplierAddressValue = pdfContext.getCompany().getFullAddress();
        this.supplierAddress2Value = pdfContext.getCompany().getAddress2();
        this.supplierPhoneValue = pdfContext.getCompany().getPhone();
        this.supplierFaxValue = pdfContext.getCompany().getFax();
        this.supplierManagerValue = pdfContext.getContactPerson();
        this.supplierEmailValue = pdfContext.getContactEmail();

        this.customerLabel = messageSource.getMessage("invoice.pdf.customer", new Object[]{}, locale);
        this.customerName1Value = pdfContext.getName1();
        this.customerName2Value = pdfContext.getName2();
        this.customerName3Value = pdfContext.getName3();
        this.customerStreetValue = pdfContext.getStreet();
        this.customerCityValue = pdfContext.getCity();
        this.customerZipCodeValue = pdfContext.getZipCode();
        this.customerCountryValue = pdfContext.getCountry();
        this.customerVatRegNumberLabel = messageSource.getMessage("invoice.pdf.vatNumber", new Object[]{}, locale);
        this.customerVatRegNumberValue = pdfContext.getVatRegNumber();

        //footer
        this.bankNameLabel = messageSource.getMessage("invoice.pdf.footer.bank.name", new Object[]{}, locale);
        this.bankAddressLabel = messageSource.getMessage("invoice.pdf.footer.bank.address", new Object[]{}, locale);
        this.bankSwiftCodeLabel = messageSource.getMessage("invoice.pdf.footer.bank.swiftCode", new Object[]{}, locale);
        this.bankAccountLabel = messageSource.getMessage("invoice.pdf.footer.bank.accountNumber", new Object[]{}, locale);
        this.huTaxNoLabel = messageSource.getMessage("invoice.pdf.footer.huTaxNumber", new Object[]{}, locale);
        this.euTaxNoLabel = messageSource.getMessage("invoice.pdf.footer.euTaxNumber", new Object[]{}, locale);
        this.admissionNumberLabel = messageSource.getMessage("invoice.pdf.footer.admissionNumber", new Object[]{}, locale);
        this.pageNumberLabel = messageSource.getMessage("invoice.pdf.footer.page", new Object[]{}, locale);
        this.invoiceGeneratedMsgLabel = messageSource.getMessage("invoice.pdf.footer.invoiceGenerateMsg", new Object[]{}, locale);

        this.bankNameValue = pdfContext.getBankName();
        this.bankAddressValue = pdfContext.getBankAddress();
        this.bankSwiftCodeValue = pdfContext.getSwiftCode();
        this.bankAccountValue = pdfContext.getBankAccount();
        this.huTaxNoValue = pdfContext.getCompany().getTaxNumber();
        this.euTaxNoValue = pdfContext.getCompany().getEuTaxNumber();
        this.admissionNumberValue = pdfContext.getCompany().getLicenceNumber();
    }

}
