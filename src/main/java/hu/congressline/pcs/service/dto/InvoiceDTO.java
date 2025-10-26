package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import hu.congressline.pcs.domain.InvoiceRegistration;
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

    public InvoiceDTO(InvoiceRegistration invoiceReg) {
        this.id = invoiceReg.getInvoice().getId();
        this.invoiceNumber = invoiceReg.getInvoice().getInvoiceNumber();
        this.stornoInvoiceNumber = invoiceReg.getInvoice().getStornoInvoiceNumber();
        this.name1 = invoiceReg.getInvoice().getName1();
        this.name2 = invoiceReg.getInvoice().getName2();
        this.name3 = invoiceReg.getInvoice().getName3();
        this.optionalName = invoiceReg.getInvoice().getOptionalName();
        this.vatRegNumber = invoiceReg.getInvoice().getVatRegNumber();
        this.city = invoiceReg.getInvoice().getCity();
        this.zipCode = invoiceReg.getInvoice().getZipCode();
        this.street = invoiceReg.getInvoice().getStreet();
        this.country = invoiceReg.getInvoice().getCountry();
        this.countryCode = invoiceReg.getInvoice().getCountryCode();
        this.optionalText = invoiceReg.getInvoice().getOptionalText();
        this.startDate = invoiceReg.getInvoice().getStartDate();
        this.endDate = invoiceReg.getInvoice().getEndDate();
        this.dateOfFulfilment = invoiceReg.getInvoice().getDateOfFulfilment();
        this.paymentDeadline = invoiceReg.getInvoice().getPaymentDeadline();
        this.billingMethod = invoiceReg.getInvoice().getBillingMethod();
        this.bankName = invoiceReg.getInvoice().getBankName();
        this.bankAccount = invoiceReg.getInvoice().getBankAccount();
        this.bankAddress = invoiceReg.getInvoice().getBankAddress();
        this.swiftCode = invoiceReg.getInvoice().getSwiftCode();
        this.printLocale = invoiceReg.getInvoice().getPrintLocale();
        this.storno = invoiceReg.getInvoice().getStorno();
        this.stornired = invoiceReg.getInvoice().getStornired();
        this.createdDate = invoiceReg.getInvoice().getCreatedDate();
        this.exchangeRate = invoiceReg.getInvoice().getExchangeRate();
        this.navTrxId = invoiceReg.getInvoice().getNavTrxId();
        this.navStatus = invoiceReg.getInvoice().getNavStatus();
        this.navVatCategory = invoiceReg.getInvoice().getNavVatCategory();
        this.invoiceType = invoiceReg.getInvoice().getInvoiceType();
        this.dateOfPayment = invoiceReg.getDateOfPayment();

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
