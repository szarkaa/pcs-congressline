package hu.congressline.pcs.service.pdf;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCharge;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoicePdfContext extends PdfContext {
    private Registration registration;
    private List<InvoiceItem> invoiceItemList;
    private List<InvoiceCharge> invoiceChargeList;
    private final Map<ChargeableItemType, BigDecimal> chargedServiceAmountsMap;
    private Map<PayingGroupItem, BigDecimal> activePayingGroups = new HashMap<>();

    public InvoicePdfContext(InvoiceRegistration invoiceRegistration, List<InvoiceItem> invoiceItemList, List<InvoiceCharge> invoiceChargeList) {
        super(invoiceRegistration.getInvoice().getOptionalText(), Locale.forLanguageTag(invoiceRegistration.getInvoice().getPrintLocale()));
        final Invoice invoice = invoiceRegistration.getInvoice();
        setRegistration(invoiceRegistration.getRegistration());
        setBankName(invoice.getBankName());
        setBankAddress(invoice.getBankAddress());
        setBankAccount(invoice.getBankAccount());
        setSwiftCode(invoice.getSwiftCode());
        setInvoiceType(invoice.getInvoiceType());
        setInvoiceNumber(invoice.getInvoiceNumber());
        setName1(invoice.getName1());
        setName2(invoice.getName2());
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
        setContactPerson(invoiceRegistration.getRegistration().getCongress().getContactPerson());
        setContactEmail(invoiceRegistration.getRegistration().getCongress().getContactEmail());
        this.invoiceItemList = invoiceItemList;
        this.invoiceChargeList = invoiceChargeList;
        this.chargedServiceAmountsMap = new HashMap<>();
        init(invoice);
    }

    private void init(Invoice invoice) {
        chargedServiceAmountsMap.put(ChargeableItemType.HOTEL, BigDecimal.ZERO);
        chargedServiceAmountsMap.put(ChargeableItemType.OPTIONAL_SERVICE, BigDecimal.ZERO);
        chargedServiceAmountsMap.put(ChargeableItemType.REGISTRATION, BigDecimal.ZERO);
        chargedServiceAmountsMap.put(ChargeableItemType.MISCELLANEOUS, BigDecimal.ZERO);

        invoiceChargeList.forEach(charge -> {
            ChargeableItemType key = charge.getItemType();
            BigDecimal amount = chargedServiceAmountsMap.get(key);
            chargedServiceAmountsMap.put(key, amount.add(charge.getAmount()));
        });

        if (invoiceItemList.size() > 0) {
            setCurrency(invoiceItemList.get(0).getCurrency());
        } else if (invoiceChargeList.size() > 0) {
            setCurrency(invoiceChargeList.get(0).getCurrency());
        }
        setCurrencyExchangeRate(invoice.getExchangeRate());
    }

}
