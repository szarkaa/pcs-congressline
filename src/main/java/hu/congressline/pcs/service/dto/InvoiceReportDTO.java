package hu.congressline.pcs.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.VatRateType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class InvoiceReportDTO implements Serializable {
    private Long id;
    private String invoiceNumber;
    private String stornoInvoiceNumber;
    private InvoiceReportType invoiceReportType;
    private InvoiceType invoiceType;
    private String name1;
    private String name2;
    private String name3;
    private String optionalName;
    private String vatRegNumber;
    private String city;
    private String zipCode;
    private String street;
    private String country;
    private String congressName;
    private String programNumber;
    private LocalDate createdDate;
    private LocalDate dateOfFulfilment;
    private LocalDate paymentDeadline;
    private String billingMethod;
    private String printLocale;
    private Boolean storno;
    private List<InvoiceReportVatItemDTO> items;
    private BigDecimal exchangeRate;
    private LocalDate dateOfPayment;
    private InvoiceNavStatus navStatus;

    public InvoiceReportDTO(InvoiceRegistration invoiceRegistration) {
        init(invoiceRegistration.getInvoice());
        this.congressName = invoiceRegistration.getRegistration().getCongress().getName();
        this.programNumber = invoiceRegistration.getRegistration().getCongress().getProgramNumber();
        this.dateOfPayment = invoiceRegistration.getDateOfPayment();
    }

    public InvoiceReportDTO(InvoicePayingGroup invoicePayingGroup) {
        init(invoicePayingGroup.getInvoice());
        this.congressName = invoicePayingGroup.getPayingGroup().getCongress().getName();
        this.programNumber = invoicePayingGroup.getPayingGroup().getCongress().getProgramNumber();
        this.dateOfPayment = invoicePayingGroup.getDateOfGroupPayment();
    }

    public InvoiceReportDTO(InvoiceCongress invoiceCongress) {
        init(invoiceCongress.getInvoice());
        this.congressName = invoiceCongress.getCongress().getName();
        this.programNumber = invoiceCongress.getCongress().getProgramNumber();
        this.dateOfPayment = invoiceCongress.getDateOfPayment();
    }

    private void init(Invoice invoice) {
        this.id = invoice.getId();
        this.invoiceNumber = invoice.getInvoiceNumber();
        this.stornoInvoiceNumber = invoice.getStornoInvoiceNumber();
        this.invoiceReportType = getInvoiceType(invoice);
        this.invoiceType = invoice.getInvoiceType();
        this.name1 = invoice.getName1();
        this.name2 = invoice.getName2();
        this.name3 = invoice.getName3();
        this.optionalName = invoice.getOptionalName();
        this.vatRegNumber = invoice.getVatRegNumber();
        this.billingMethod = invoice.getBillingMethod();
        this.country = invoice.getCountry();
        this.zipCode = invoice.getZipCode();
        this.city = invoice.getCity();
        this.street = invoice.getStreet();
        this.createdDate = invoice.getCreatedDate();
        this.dateOfFulfilment = invoice.getDateOfFulfilment();
        this.paymentDeadline = invoice.getPaymentDeadline();
        this.exchangeRate = invoice.getExchangeRate();
        this.navStatus = invoice.getNavStatus();
    }

    private InvoiceReportType getInvoiceType(Invoice invoice) {
        if (invoice.getStorno()) {
            return InvoiceReportType.S;
        } else if (invoice.getStornired()) {
            return InvoiceReportType.X;
        } else {
            return InvoiceReportType.N;
        }
    }

    @NoArgsConstructor
    @Data
    public static class InvoiceReportVatItemDTO {
        private String vatTypeId;
        private VatRateType vatType;
        private String currency;
        private BigDecimal exchangeRate;
        private BigDecimal vatBase;
        private Integer vat;
        private BigDecimal vatValue;
        private BigDecimal total;
    }
}
