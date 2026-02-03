package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.NavVatCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data

public class InvoiceDTO implements Serializable {
    private Long id;
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

    public InvoiceDTO(Invoice invoice) {
        this.id = invoice.getId();
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvoiceDTO invoice = (InvoiceDTO) o;
        if (invoice.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, invoice.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
