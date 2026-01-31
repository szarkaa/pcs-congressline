package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.NavVatCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MiscInvoiceDTO implements Serializable {

    private Long invoiceCongressId;
    private Long invoiceId;
    private String invoiceNumber;
    private String stornoInvoiceNumber;
    private String name1;
    private String name2;
    private String name3;
    private String optionalName;
    private String vatRegNumber;
    private String city;
    private String zipCode;
    private String street;
    private String country;
    private String countryCode;
    private String optionalText;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate dateOfFulfilment;
    private LocalDate paymentDeadline;
    private String billingMethod;
    private String bankName;
    private String bankAccount;
    private String bankAddress;
    private String swiftCode;
    private String printLocale;
    private Boolean storno = Boolean.FALSE;
    private Boolean stornired = Boolean.FALSE;
    private LocalDate createdDate;
    private BigDecimal exchangeRate;
    private String navTrxId;
    private InvoiceNavStatus navStatus;
    private NavVatCategory navVatCategory;
    private InvoiceType invoiceType;
    private LocalDate dateOfPayment;
    private Long congressId;

    public MiscInvoiceDTO(InvoiceCongress invoiceCongress) {
        this.invoiceCongressId = invoiceCongress.getId();
        var invoice = invoiceCongress.getInvoice();
        this.invoiceId = invoice.getId();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.stornoInvoiceNumber = invoice.getStornoInvoiceNumber();
        this.name1 = invoice.getName1();
        this.name2 = invoice.getName2();
        this.name3 = invoice.getName3();
        this.optionalName = invoice.getOptionalName();
        this.vatRegNumber = invoice.getVatRegNumber();
        this.city = invoice.getCity();
        this.zipCode = invoice.getZipCode();
        this.street = invoice.getStreet();
        this.country = invoice.getCountry();
        this.countryCode = invoice.getCountryCode();
        this.optionalText = invoice.getOptionalText();
        this.startDate = invoice.getStartDate();
        this.endDate = invoice.getEndDate();
        this.dateOfFulfilment = invoice.getDateOfFulfilment();
        this.paymentDeadline = invoice.getPaymentDeadline();
        this.billingMethod = invoice.getBillingMethod();
        this.bankName = invoice.getBankName();
        this.bankAccount = invoice.getBankAccount();
        this.bankAddress = invoice.getBankAddress();
        this.swiftCode = invoice.getSwiftCode();
        this.printLocale = invoice.getPrintLocale();
        this.storno = invoice.getStorno();
        this.stornired = invoice.getStornired();
        this.createdDate = invoice.getCreatedDate();
        this.exchangeRate = invoice.getExchangeRate();
        this.navTrxId = invoice.getNavTrxId();
        this.navStatus = invoice.getNavStatus();
        this.navVatCategory = invoice.getNavVatCategory();
        this.invoiceType = invoice.getInvoiceType();

        this.congressId = invoiceCongress.getCongress().getId();
        this.dateOfPayment = invoiceCongress.getDateOfPayment();
    }

    @Override
    public String toString() {
        return "MiscInvoiceDTO{" + "invoiceCongressId=" + invoiceCongressId + "}";
    }
}
