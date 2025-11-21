package hu.congressline.pcs.service.pdf;

import java.util.List;
import java.util.Locale;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoiceItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MiscInvoicePdfContext extends PdfContext {

    private List<InvoiceItem> invoiceItemList;
    private Congress congress;

    public MiscInvoicePdfContext(InvoiceCongress invoiceCongress, List<InvoiceItem> invoiceItemList) {
        super(invoiceCongress.getInvoice().getOptionalText(), Locale.forLanguageTag(invoiceCongress.getInvoice().getPrintLocale()));
        final Invoice invoice = invoiceCongress.getInvoice();
        setBankName(invoice.getBankName());
        setBankAddress(invoice.getBankAddress());
        setBankAccount(invoice.getBankAccount());
        setSwiftCode(invoice.getSwiftCode());
        setInvoiceType(invoice.getInvoiceType());
        setInvoiceNumber(invoice.getInvoiceNumber());
        setName1(invoice.getName1());
        setName2(invoice.getName2());
        setName3(invoice.getName3());
        setStreet(invoice.getStreet());
        setCity(invoice.getCity());
        setZipCode(invoice.getZipCode());
        setCountry(invoice.getCountry());
        setVatRegNumber(invoice.getVatRegNumber());
        setBillingMethod(invoice.getBillingMethod());
        setStartDate(invoice.getStartDate());
        setEndDate(invoice.getEndDate());
        setDateOfFulfilment(invoice.getDateOfFulfilment());
        setPaymentDeadlineDate(invoice.getPaymentDeadline());
        setCreatedDate(invoice.getCreatedDate());
        setStorno(invoice.getStorno() != null && invoice.getStorno());
        setStornoInvoiceNumber(invoice.getStornoInvoiceNumber());
        setContactPerson(invoiceCongress.getCongress().getContactPerson());
        setContactEmail(invoiceCongress.getCongress().getContactEmail());
        this.invoiceItemList = invoiceItemList;
        this.congress = invoiceCongress.getCongress();
        init(invoice, invoiceItemList);
    }

    private void init(Invoice invoice, List<InvoiceItem> invoiceItems) {
        if (!invoiceItems.isEmpty()) {
            setCurrency(invoiceItems.get(0).getCurrency());
        }
        setCurrencyExchangeRate(invoice.getExchangeRate());
    }
}
