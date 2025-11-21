package hu.congressline.pcs.service.pdf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.PayingGroup;
import hu.congressline.pcs.domain.PayingGroupItem;

public class GroupDiscountInvoicePdfContext extends PdfContext {
    private PayingGroup payingGroup;
    private List<InvoiceItem> invoiceItemList;
    private Map<PayingGroupItem, BigDecimal> activePayingGroups = new HashMap<>();

    public GroupDiscountInvoicePdfContext(InvoicePayingGroup invoicePayingGroup, List<InvoiceItem> invoiceItemList) {
        super(invoicePayingGroup.getInvoice().getOptionalText(), Locale.forLanguageTag(invoicePayingGroup.getInvoice().getPrintLocale()));
        final Invoice invoice = invoicePayingGroup.getInvoice();
        setBankName(invoice.getBankName());
        setBankAddress(invoice.getBankAddress());
        setBankAccount(invoice.getBankAccount());
        setSwiftCode(invoice.getSwiftCode());
        setInvoiceType(invoice.getInvoiceType());
        setInvoiceNumber(invoice.getInvoiceNumber());
        setName1(invoice.getName1());
        setName2(invoice.getName2());
        //setName3(invoice.getName3());
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
        setContactPerson(invoicePayingGroup.getPayingGroup().getCongress().getContactPerson());
        setContactEmail(invoicePayingGroup.getPayingGroup().getCongress().getContactEmail());
        this.payingGroup = invoicePayingGroup.getPayingGroup();
        this.invoiceItemList = invoiceItemList;
        init(invoice);
    }

    public void setActivePayingGroups(Map<PayingGroupItem, BigDecimal> activePayingGroups) {
        this.activePayingGroups = activePayingGroups;
    }

    public PayingGroup getPayingGroup() {
        return payingGroup;
    }

    public void setPayingGroup(PayingGroup payingGroup) {
        this.payingGroup = payingGroup;
    }

    public List<InvoiceItem> getInvoiceItemList() {
        return invoiceItemList;
    }

    public void setInvoiceItemList(List<InvoiceItem> invoiceItemList) {
        this.invoiceItemList = invoiceItemList;
    }

    public Map<PayingGroupItem, BigDecimal> getActivePayingGroups() {
        return activePayingGroups;
    }

    private void init(Invoice invoice) {
        if (invoiceItemList.size() > 0) {
            setCurrency(invoiceItemList.get(0).getCurrency());
        }

        setCurrencyExchangeRate(invoice.getExchangeRate());
    }
}
