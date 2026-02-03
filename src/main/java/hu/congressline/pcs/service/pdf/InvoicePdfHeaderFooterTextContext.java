package hu.congressline.pcs.service.pdf;

import org.springframework.context.MessageSource;

import java.util.Locale;
import java.util.Objects;

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

        this.supplierNameValue = Objects.toString(pdfContext.getCompany().getName(), "");
        this.supplierAddressValue = Objects.toString(pdfContext.getCompany().getFullAddress(), "");
        this.supplierAddress2Value = Objects.toString(pdfContext.getCompany().getAddress2(), "");
        this.supplierPhoneValue = Objects.toString(pdfContext.getCompany().getPhone(), "");
        this.supplierFaxValue = Objects.toString(pdfContext.getCompany().getFax(), "");
        this.supplierManagerValue = Objects.toString(pdfContext.getContactPerson(), "");
        this.supplierEmailValue = Objects.toString(pdfContext.getContactEmail(), "");

        this.customerLabel = messageSource.getMessage("invoice.pdf.customer", new Object[]{}, locale);
        this.customerName1Value = Objects.toString(pdfContext.getName1(), "");
        this.customerName2Value = Objects.toString(pdfContext.getName2(), "");
        this.customerName3Value = Objects.toString(pdfContext.getName3(), "");
        this.customerStreetValue = Objects.toString(pdfContext.getStreet(), "");
        this.customerCityValue = Objects.toString(pdfContext.getCity(), "");
        this.customerZipCodeValue = Objects.toString(pdfContext.getZipCode(), "");
        this.customerCountryValue = Objects.toString(pdfContext.getCountry(), "");
        this.customerVatRegNumberLabel = messageSource.getMessage("invoice.pdf.vatNumber", new Object[]{}, locale);
        this.customerVatRegNumberValue = Objects.toString(pdfContext.getVatRegNumber(), "");

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

        this.bankNameValue = Objects.toString(pdfContext.getBankName(), "");
        this.bankAddressValue = Objects.toString(pdfContext.getBankAddress(), "");
        this.bankSwiftCodeValue = Objects.toString(pdfContext.getSwiftCode(), "");
        this.bankAccountValue = Objects.toString(pdfContext.getBankAccount(), "");
        this.huTaxNoValue = Objects.toString(pdfContext.getCompany().getTaxNumber(), "");
        this.euTaxNoValue = Objects.toString(pdfContext.getCompany().getEuTaxNumber(), "");
        this.admissionNumberValue = Objects.toString(pdfContext.getCompany().getLicenceNumber(), "");
    }

}
