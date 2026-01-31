package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import hu.congressline.pcs.domain.BankAccount;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.MiscInvoiceItem;
import hu.congressline.pcs.domain.MiscService;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.repository.BankAccountRepository;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.InvoiceCongressRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.MiscInvoiceItemRepository;
import hu.congressline.pcs.web.rest.vm.MiscInvoiceItemVM;
import hu.congressline.pcs.web.rest.vm.MiscInvoiceVM;
import hu.congressline.pcs.web.rest.vm.SetPaymentDateVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.domain.enumeration.InvoiceType.PRO_FORMA;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MiscInvoiceService {

    private final InvoiceService invoiceService;
    private final CompanyService companyService;
    private final MiscInvoiceItemRepository miscInvoiceItemRepository;
    private final InvoiceCongressRepository invoiceCongressRepository;
    private final PriceService priceService;
    private final CurrencyService currencyService;
    private final InvoiceItemRepository invoiceItemRepository;
    private final CountryRepository countyRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CongressService congressService;
    private final MiscServiceService miscServiceService;

    @SuppressWarnings("MissingJavadocMethod")
    public InvoiceCongress save(MiscInvoiceVM invoiceVM) {
        log.debug("Request to save misc invoice : {}", invoiceVM);
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(invoiceVM.getInvoiceType());
        invoice.setNavVatCategory(invoiceVM.getNavVatCategory());
        invoice.setCreatedDate(LocalDate.now());
        invoice.setInvoiceNumber(!PRO_FORMA.equals(invoiceVM.getInvoiceType()) ? companyService.getNextFullInvoiceNumber() : companyService.getNextProformaInvoiceNumber());
        invoice.setName1(invoiceVM.getName1());
        invoice.setName2(invoiceVM.getName2());
        invoice.setName3(invoiceVM.getName3());
        invoice.setPrintLocale(invoiceVM.getLanguage());
        invoice.setVatRegNumber(invoiceVM.getVatRegNumber());
        invoice.setBillingMethod(invoiceVM.getBillingMethod());
        invoice.setStreet(invoiceVM.getStreet());
        invoice.setCity(invoiceVM.getCity());
        invoice.setZipCode(invoiceVM.getZipCode());
        invoice.setCountryCode(invoiceVM.getCountry());
        countyRepository.findOneByCodeIgnoreCase(invoiceVM.getCountry()).ifPresent(c -> invoice.setCountry(c.getName()));
        invoice.setStartDate(invoiceVM.getStartDate());
        invoice.setEndDate(invoiceVM.getEndDate());
        invoice.setDateOfFulfilment(invoiceVM.getDateOfFulfilment());
        invoice.setPaymentDeadline(invoiceVM.getPaymentDeadline());
        BankAccount bankAccount = bankAccountRepository.findById(invoiceVM.getBankAccountId())
            .orElseThrow(() -> new IllegalArgumentException("Bank account not found by id: " + invoiceVM.getBankAccountId()));
        invoice.setBankName(bankAccount.getBankName());
        invoice.setBankAddress(bankAccount.getBankAddress());
        invoice.setBankAccount(bankAccount.getBankAccount());
        invoice.setSwiftCode(bankAccount.getSwiftCode());
        invoice.setOptionalText(invoiceVM.getOptionalText());
        final Map<Long, MiscService> miscServiceMap = getMiscServices(invoiceVM.getMiscInvoiceItems());
        final String currency = miscServiceMap.values().stream().findFirst().map(MiscService::getCurrency).map(hu.congressline.pcs.domain.Currency::getCurrency)
            .orElseThrow(() -> new IllegalArgumentException("Currency of the misc service is not identifiable"));

        invoice.setExchangeRate(!Currency.HUF.toString().equalsIgnoreCase(currency) ? currencyService.getRateForDate(currency, LocalDate.now()) : null);
        Invoice result = invoiceService.save(invoice);

        InvoiceCongress ic = new InvoiceCongress();
        ic.setInvoice(result);
        ic.setCongress(congressService.getById(invoiceVM.getCongressId()));
        InvoiceCongress invoiceCongress = invoiceCongressRepository.save(ic);

        invoiceVM.getMiscInvoiceItems().forEach(vm -> {
            MiscInvoiceItem item = new MiscInvoiceItem();
            item.setItemQuantity(vm.getItemQuantity());
            item.setInvoice(result);
            item.setMiscService(miscServiceMap.get(vm.getMiscServiceId()));
            miscInvoiceItemRepository.save(item);

            BigDecimal netPrice = priceService.getVatBase(item.getMiscService().getPrice(), item.getMiscService().getVatInfo().getVat(),
                    !Currency.HUF.toString().equalsIgnoreCase(item.getMiscService().getCurrency().getCurrency()) ? 2 : 0);
            BigDecimal vatValue = priceService.getVatValue(item.getMiscService().getPrice(), item.getMiscService().getVatInfo().getVat(),
                    !Currency.HUF.toString().equalsIgnoreCase(item.getMiscService().getCurrency().getCurrency()) ? 2 : 0);

            InvoiceItem invoiceItem = new InvoiceItem();
            invoiceItem.setInvoice(result);
            invoiceItem.setItemType(ChargeableItemType.MISCELLANEOUS);
            invoiceItem.setCurrency(currency);
            invoiceItem.setSzj(item.getMiscService().getVatInfo().getSzj());
            invoiceItem.setVat(item.getMiscService().getVatInfo().getVat());
            invoiceItem.setItemName(item.getMiscService().getName());
            invoiceItem.setItemDesc(item.getMiscService().getDescription());
            invoiceItem.setUnit(item.getItemQuantity());
            invoiceItem.setUnitOfMeasure(item.getMiscService().getMeasure());
            invoiceItem.setUnitPrice(netPrice);
            invoiceItem.setVatName(item.getMiscService().getVatInfo().getName());
            invoiceItem.setVatBase(netPrice.multiply(new BigDecimal(item.getItemQuantity())));
            invoiceItem.setVatValue(vatValue.multiply(new BigDecimal(item.getItemQuantity())));
            invoiceItem.setVatRateType(item.getMiscService().getVatInfo().getVatRateType());
            invoiceItem.setVatExceptionReason(item.getMiscService().getVatInfo().getVatExceptionReason());
            invoiceItem.setTotal(item.getMiscService().getPrice().multiply(new BigDecimal(item.getItemQuantity())));
            invoiceItemRepository.save(invoiceItem);
        });
        return invoiceCongress;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public InvoiceCongress setPaymentDate(SetPaymentDateVM setPaymentDateVM) {
        InvoiceCongress ic = getById(setPaymentDateVM.getId());
        if (ic == null) {
            return null;
        }
        ic.setDateOfPayment(setPaymentDateVM.getPaymentDate());
        InvoiceCongress result = invoiceCongressRepository.save(ic);

        final List<MiscInvoiceItem> miscInvoiceItems = miscInvoiceItemRepository.findAllByInvoice(ic.getInvoice());
        miscInvoiceItems.forEach(miscInvoiceItem -> miscInvoiceItem.setDateOfPayment(setPaymentDateVM.getPaymentDate()));
        miscInvoiceItemRepository.saveAll(miscInvoiceItems);
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public InvoiceCongress stornoInvoice(Long invoiceId) {
        log.debug("Request to get storno Invoice : {}", invoiceId);
        Invoice invoice = invoiceService.getById(invoiceId);

        if (invoice.getStornired()) {
            throw new IllegalArgumentException("This invoice is already stornired invoice invoiceId: " + invoiceId);
        }

        Invoice stornoInvoice = new Invoice();
        stornoInvoice.setInvoiceType(invoice.getInvoiceType());
        stornoInvoice.setNavVatCategory(invoice.getNavVatCategory());
        stornoInvoice.setStornoInvoiceNumber(invoice.getInvoiceNumber());
        stornoInvoice.setInvoiceNumber(companyService.getNextFullInvoiceNumber());
        stornoInvoice.setStorno(true);
        stornoInvoice.setCreatedDate(LocalDate.now());
        stornoInvoice.setBillingMethod(invoice.getBillingMethod());
        stornoInvoice.setOptionalText(invoice.getOptionalText());
        stornoInvoice.setPrintLocale(invoice.getPrintLocale());

        stornoInvoice.setStartDate(invoice.getStartDate());
        stornoInvoice.setEndDate(invoice.getEndDate());
        stornoInvoice.setPaymentDeadline(invoice.getPaymentDeadline());
        stornoInvoice.setDateOfFulfilment(invoice.getDateOfFulfilment());
        stornoInvoice.setBankName(invoice.getBankName());
        stornoInvoice.setBankAddress(invoice.getBankAddress());
        stornoInvoice.setBankAccount(invoice.getBankAccount());
        stornoInvoice.setSwiftCode(invoice.getSwiftCode());
        stornoInvoice.setName1(invoice.getName1());
        stornoInvoice.setName2(invoice.getName2());
        //stornoInvoice.setName3(invoice.getName3());
        stornoInvoice.setVatRegNumber(invoice.getVatRegNumber());
        stornoInvoice.setStreet(invoice.getStreet());
        stornoInvoice.setCity(invoice.getCity());
        stornoInvoice.setZipCode(invoice.getZipCode());
        stornoInvoice.setCountryCode(invoice.getCountryCode());
        stornoInvoice.setCountry(invoice.getCountry());
        stornoInvoice.setExchangeRate(invoice.getExchangeRate());
        Invoice result = invoiceService.save(stornoInvoice);

        Congress congress = invoiceCongressRepository.findByInvoice(invoice).stream().map(InvoiceCongress::getCongress).findFirst().orElse(null);
        InvoiceCongress ic = new InvoiceCongress();
        ic.setInvoice(result);
        ic.setCongress(congress);
        InvoiceCongress invoiceCongress = invoiceCongressRepository.save(ic);

        final List<MiscInvoiceItem> items = miscInvoiceItemRepository.findAllByInvoice(invoice);
        items.forEach(oldItem -> {
            MiscInvoiceItem item = new MiscInvoiceItem();
            item.setInvoice(result);
            item.setMiscService(oldItem.getMiscService());
            item.setItemQuantity(oldItem.getItemQuantity());
            miscInvoiceItemRepository.save(item);
        });

        List<InvoiceItem> invoiceItemList = invoiceItemRepository.findAllByInvoice(invoice);
        invoiceItemList.forEach(item -> {
            InvoiceItem stornoItem = InvoiceItem.stornoItem(item, result);
            invoiceItemRepository.save(stornoItem);
        });

        invoice.setStornired(true);
        invoiceService.save(invoice);
        return invoiceCongress;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<InvoiceCongress> findByCongressId(Long id) {
        log.debug("Request to get all misc invoices by congress id: {}", id);
        return invoiceCongressRepository.findByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public InvoiceCongress findInvoiceCongressByInvoiceId(Long invoiceId) {
        log.debug("Request to get invoice congress by id : {}", invoiceId);
        return invoiceCongressRepository.findByInvoiceId(invoiceId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<InvoiceItem> findItems(Long invoiceCongressId) {
        log.debug("Request to get misc invoice items by invoice congress id : {}", invoiceCongressId);
        final InvoiceCongress invoiceCongress = getById(invoiceCongressId);
        return invoiceItemRepository.findAllByInvoice(invoiceCongress.getInvoice());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<InvoiceCongress> findById(Long id) {
        log.debug("Request to find invoice congress : {}", id);
        return invoiceCongressRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public InvoiceCongress getById(Long id) {
        log.debug("Request to get invoice congress : {}", id);
        return invoiceCongressRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice congress not found by id: " + id));
    }

    private Map<Long, MiscService> getMiscServices(List<MiscInvoiceItemVM> items) {
        Map<Long, MiscService> serviceMap = new HashMap<>();
        for (MiscInvoiceItemVM item : items) {
            serviceMap.put(item.getMiscServiceId(), miscServiceService.getById(item.getMiscServiceId()));
        }
        return serviceMap;
    }
}
