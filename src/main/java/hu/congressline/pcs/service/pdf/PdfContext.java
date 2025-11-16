package hu.congressline.pcs.service.pdf;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import hu.congressline.pcs.domain.Company;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import lombok.Getter;
import lombok.Setter;

import static hu.congressline.pcs.service.util.DateUtil.DATE_FORMAT_EN;
import static hu.congressline.pcs.service.util.DateUtil.DATE_FORMAT_HU;

@Getter
@Setter
public abstract class PdfContext {
    private InvoiceType invoiceType;
    private String invoiceNumber;
    private String stornoInvoiceNumber;
    private String name1;
    private String name2;
    private String name3;
    private String street;
    private String city;
    private String zipCode;
    private String country;
    private String vatRegNumber;
    private String billingMethod;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dateOfFulfilment;
    private LocalDate paymentDeadlineDate;
    private LocalDate createdDate;
    private Boolean storno = Boolean.FALSE;
    private String bankName;
    private String bankAddress;
    private String bankAccount;
    private String swiftCode;
    private String optionalText;
    private Locale locale;
    private String currency;
    private BigDecimal currencyExchangeRate;
    private Company company;
    private String contactPerson;
    private String contactEmail;
    private DateTimeFormatter formatter;

    public PdfContext() {
    }

    public PdfContext(String optionalText, Locale locale) {
        this.optionalText = optionalText;
        this.locale = locale;
        this.formatter = DateTimeFormatter.ofPattern(locale.getLanguage().equals("hu") ? DATE_FORMAT_HU : DATE_FORMAT_EN);
    }
}
