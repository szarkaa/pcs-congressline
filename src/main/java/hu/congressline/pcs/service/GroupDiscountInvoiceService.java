package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.VatInfo;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.InvoicePayingGroupRepository;
import hu.congressline.pcs.repository.InvoiceRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.web.rest.vm.GroupDiscountInvoiceVM;
import hu.congressline.pcs.web.rest.vm.SetPaymentDateVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class GroupDiscountInvoiceService {

    private final CompanyService companyService;
    private final GroupDiscountInvoiceHistoryRepository groupDiscountInvoiceHistoryRepository;
    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final OrderedOptionalServiceRepository oosRepository;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoicePayingGroupRepository invoicePayingGroupRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final DiscountService discountService;
    private final PriceService priceService;
    private final CurrencyService currencyService;
    private CountryRepository countyRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public InvoicePayingGroup save(GroupDiscountInvoiceVM invoiceVM) {
        log.debug("Request to save invoice from GroupDiscountInvoice : {}", invoiceVM);
        Invoice invoice = new Invoice();
        invoice.setInvoiceType(InvoiceType.REGULAR);
        invoice.setNavVatCategory(invoiceVM.getNavVatCategory());
        invoice.setCreatedDate(LocalDate.now());
        invoice.setStorno(false);
        invoice.setInvoiceNumber(companyService.getNextFullInvoiceNumber());
        invoice.setName1(invoiceVM.getName());
        invoice.setPrintLocale(invoiceVM.getLanguage());
        invoice.setVatRegNumber(invoiceVM.getTaxNumber());
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
        invoice.setBankName(invoiceVM.getBankAccount().getBankName());
        invoice.setBankAddress(invoiceVM.getBankAccount().getBankAddress());
        invoice.setBankAccount(invoiceVM.getBankAccount().getBankAccount());
        invoice.setSwiftCode(invoiceVM.getBankAccount().getSwiftCode());
        invoice.setOptionalText(invoiceVM.getOptionalText());
        final String currency = invoiceVM.getPayingGroup().getCurrency().getCurrency();
        invoice.setExchangeRate(!Currency.HUF.toString().equalsIgnoreCase(currency) ? currencyService.getRateForDate(currency, LocalDate.now()) : null);
        Invoice result = invoiceRepository.save(invoice);

        InvoicePayingGroup ipg = new InvoicePayingGroup();
        ipg.setInvoice(result);
        ipg.setPayingGroup(invoiceVM.getPayingGroup());
        InvoicePayingGroup invoicePayingGroup = invoicePayingGroupRepository.save(ipg);

        final List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByIdIn(invoiceVM.getChargeableItemIdList());
        saveGroupDiscountInvoiceHistoryForRRT(result, rrtList);
        saveInvoiceRRTItems(invoicePayingGroup, rrtList);
        final List<RoomReservationRegistration> rrrList = rrrRepository.findAllByIdIn(invoiceVM.getChargeableItemIdList());
        saveGroupDiscountInvoiceHistoryForRRR(result, rrrList);
        saveInvoiceRRRItems(invoicePayingGroup, rrrList);
        final List<OrderedOptionalService> oosList = oosRepository.findAllByIdIn(invoiceVM.getChargeableItemIdList());
        saveGroupDiscountInvoiceHistoryForOOS(result, oosList);
        saveInvoiceOOSItems(invoicePayingGroup, oosList);

        return invoicePayingGroup;
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private void saveInvoiceRRTItems(InvoicePayingGroup ipg, List<RegistrationRegistrationType> rrtList) {
        final Map<VatInfo, List<RegistrationRegistrationType>> map = getSortedRRTsByVAT(rrtList);
        map.keySet().forEach(vatInfo -> {
            final List<RegistrationRegistrationType> rrts = map.get(vatInfo);
            if (!rrts.isEmpty()) {
                BigDecimal amount = discountService.getSumAmountOfDiscountForRegistration(rrts);
                saveInvoiceItem(ipg, ChargeableItemType.REGISTRATION, vatInfo, amount);
            }
        });
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private void saveInvoiceRRRItems(InvoicePayingGroup ipg, List<RoomReservationRegistration> rrrList) {
        final Map<VatInfo, List<RoomReservationRegistration>> map = getSortedRRRRowsByVAT(rrrList);
        map.keySet().forEach(vatInfo -> {
            final List<RoomReservationRegistration> rrrs = map.get(vatInfo);
            if (!rrrs.isEmpty()) {
                BigDecimal amount = discountService.getSumAmountOfDiscountForHotel(rrrs);
                saveInvoiceItem(ipg, ChargeableItemType.HOTEL, vatInfo, amount);
            }
        });
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private void saveInvoiceOOSItems(InvoicePayingGroup ipg, List<OrderedOptionalService> oosList) {
        final Map<VatInfo, List<OrderedOptionalService>> map = getSortedOOSRowsByVAT(oosList);
        map.keySet().forEach(vatInfo -> {
            final List<OrderedOptionalService> ooss = map.get(vatInfo);
            if (!ooss.isEmpty()) {
                BigDecimal amount = discountService.getSumAmountOfDiscountForOptionalService(ooss);
                saveInvoiceItem(ipg, ChargeableItemType.OPTIONAL_SERVICE, vatInfo, amount);
            }
        });
    }

    @SuppressWarnings("MissingSwitchDefault")
    private void saveInvoiceItem(InvoicePayingGroup ipg, ChargeableItemType itemType, VatInfo vatInfo, BigDecimal amount) {
        InvoiceItem item = new InvoiceItem();
        item.setItemType(itemType);
        item.setInvoice(ipg.getInvoice());
        switch (itemType) {
            case REGISTRATION -> item.setItemName("Regisztráció");
            case HOTEL -> item.setItemName("Szállás");
            case OPTIONAL_SERVICE -> item.setItemName("Fakultatív program");
        }
        item.setSzj(vatInfo.getSzj() != null ? vatInfo.getSzj() : "");
        item.setCurrency(ipg.getPayingGroup().getCurrency().getCurrency());
        item.setUnit(1);
        item.setUnitPrice(priceService.getVatBase(amount, vatInfo.getVat(), !Currency.HUF.toString().equalsIgnoreCase(ipg.getPayingGroup().getCurrency().getCurrency()) ? 2 : 0));
        item.setVatBase(priceService.getVatBase(amount, vatInfo.getVat(), !Currency.HUF.toString().equalsIgnoreCase(ipg.getPayingGroup().getCurrency().getCurrency()) ? 2 : 0));
        item.setVat(vatInfo.getVat());
        item.setVatName(vatInfo.getName());
        item.setVatValue(priceService.getVatValue(amount, vatInfo.getVat(), !Currency.HUF.toString().equalsIgnoreCase(ipg.getPayingGroup().getCurrency().getCurrency()) ? 2 : 0));
        item.setVatRateType(vatInfo.getVatRateType());
        item.setVatExceptionReason(vatInfo.getVatExceptionReason());
        item.setTotal(amount);
        invoiceItemRepository.save(item);
    }

    private Map<VatInfo, List<RegistrationRegistrationType>> getSortedRRTsByVAT(List<RegistrationRegistrationType> regRegTypeList) {
        Map<VatInfo, List<RegistrationRegistrationType>> retVal = new HashMap<>();
        for (RegistrationRegistrationType rrt : regRegTypeList) {
            if (rrt.getPayingGroupItem() != null) {
                VatInfo vatInfo = rrt.getRegistrationType().getVatInfo();
                if (retVal.get(vatInfo) == null) {
                    List<RegistrationRegistrationType> list = new ArrayList<>();
                    list.add(rrt);
                    retVal.put(vatInfo, list);
                } else {
                    List<RegistrationRegistrationType> list = retVal.get(vatInfo);
                    list.add(rrt);
                    retVal.put(vatInfo, list);
                }
            }
        }
        return retVal;
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private Map<VatInfo, List<RoomReservationRegistration>> getSortedRRRRowsByVAT(List<RoomReservationRegistration> roomList) {
        Map<VatInfo, List<RoomReservationRegistration>> retVal = new HashMap<>();
        for (RoomReservationRegistration rrr : roomList) {
            if (rrr.getPayingGroupItem() != null) {
                VatInfo vatInfo = rrr.getRoomReservation().getRoom().getVatInfo();
                if (retVal.get(vatInfo) == null) {
                    List<RoomReservationRegistration> list = new ArrayList<>();
                    list.add(rrr);
                    retVal.put(vatInfo, list);
                } else {
                    List<RoomReservationRegistration> list = retVal.get(vatInfo);
                    list.add(rrr);
                    retVal.put(vatInfo, list);
                }
            }
        }
        return retVal;
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private Map<VatInfo, List<OrderedOptionalService>> getSortedOOSRowsByVAT(List<OrderedOptionalService> ordOptServiceList) {
        Map<VatInfo, List<OrderedOptionalService>> retVal = new HashMap<>();
        for (OrderedOptionalService oos : ordOptServiceList) {
            if (oos.getPayingGroupItem() != null) {
                VatInfo vatInfo = oos.getOptionalService().getVatInfo();
                if (retVal.get(vatInfo) == null) {
                    List<OrderedOptionalService> list = new ArrayList<>();
                    list.add(oos);
                    retVal.put(vatInfo, list);
                } else {
                    List<OrderedOptionalService> list = retVal.get(vatInfo);
                    list.add(oos);
                    retVal.put(vatInfo, list);
                }
            }
        }
        return retVal;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public InvoicePayingGroup findOne(Long id) {
        log.debug("Request to get InvoicePayingGroup : {}", id);
        return invoicePayingGroupRepository.findById(id).orElse(null);
    }

    private void saveGroupDiscountInvoiceHistoryForRRT(Invoice invoice, List<RegistrationRegistrationType> rrtList) {
        for (RegistrationRegistrationType item : rrtList) {
            GroupDiscountInvoiceHistory history = new GroupDiscountInvoiceHistory();
            history.setChargeableItem(item);
            history.setInvoice(invoice);
            groupDiscountInvoiceHistoryRepository.save(history);
        }
    }

    private void saveGroupDiscountInvoiceHistoryForRRR(Invoice invoice, List<RoomReservationRegistration> rrrList) {
        for (RoomReservationRegistration item : rrrList) {
            GroupDiscountInvoiceHistory history = new GroupDiscountInvoiceHistory();
            history.setChargeableItem(item);
            history.setInvoice(invoice);
            groupDiscountInvoiceHistoryRepository.save(history);
        }
    }

    private void saveGroupDiscountInvoiceHistoryForOOS(Invoice invoice, List<OrderedOptionalService> oosList) {
        for (OrderedOptionalService item : oosList) {
            GroupDiscountInvoiceHistory history = new GroupDiscountInvoiceHistory();
            history.setChargeableItem(item);
            history.setInvoice(invoice);
            groupDiscountInvoiceHistoryRepository.save(history);
        }
    }

    public List<InvoicePayingGroup> findByCongressId(Long id) {
        return invoicePayingGroupRepository.findByPayingGroupCongressId(id);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Transactional
    public InvoicePayingGroup setPaymentDate(SetPaymentDateVM groupSetPaymentDateVM) {
        InvoicePayingGroup ipg = invoicePayingGroupRepository.findById(groupSetPaymentDateVM.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice paying group not found by id: " + groupSetPaymentDateVM.getId()));
        ipg.setDateOfGroupPayment(groupSetPaymentDateVM.getPaymentDate());
        InvoicePayingGroup result = invoicePayingGroupRepository.save(ipg);

        final List<GroupDiscountInvoiceHistory> discountInvoiceHistories = groupDiscountInvoiceHistoryRepository.findAllByInvoice(ipg.getInvoice());
        final List<ChargeableItem> chargeableItems = discountInvoiceHistories.stream().map(GroupDiscountInvoiceHistory::getChargeableItem).toList();
        chargeableItems.forEach(item -> {
            item.setDateOfGroupPayment(groupSetPaymentDateVM.getPaymentDate());
            if (ChargeableItemType.REGISTRATION.equals(item.getChargeableItemType())) {
                rrtRepository.save((RegistrationRegistrationType) item);
            } else if (ChargeableItemType.HOTEL.equals(item.getChargeableItemType())) {
                rrrRepository.save((RoomReservationRegistration) item);
            } else if (ChargeableItemType.OPTIONAL_SERVICE.equals(item.getChargeableItemType())) {
                oosRepository.save((OrderedOptionalService) item);
            }
        });

        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public InvoicePayingGroup findInvoicePayingGroupByInvoiceId(Long invoiceId) {
        log.debug("Request to get Invoice : {}", invoiceId);
        return invoicePayingGroupRepository.findByInvoiceId(invoiceId).orElse(null);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public Invoice stornoInvoice(Long id) {
        log.debug("Request to storno GroupDiscountInvoice : {}", id);
        Invoice invoice = invoiceRepository.findById(id).orElse(null);
        if (invoice == null) {
            throw new IllegalArgumentException("Storno can not be done, invoice is not found for id: " + id);
        } else if (invoice.getStornired()) {
            throw new IllegalArgumentException("This invoice is already stornired invoice id: " + id);
        }

        Invoice stornoInvoice = new Invoice();
        stornoInvoice.setInvoiceType(invoice.getInvoiceType());
        stornoInvoice.setNavVatCategory(invoice.getNavVatCategory());
        stornoInvoice.setCreatedDate(LocalDate.now());
        stornoInvoice.setStornoInvoiceNumber(invoice.getInvoiceNumber());
        stornoInvoice.setInvoiceNumber(companyService.getNextFullInvoiceNumber());
        stornoInvoice.setStorno(true);
        stornoInvoice.setName1(invoice.getName1());
        stornoInvoice.setPrintLocale(invoice.getPrintLocale());
        stornoInvoice.setVatRegNumber(invoice.getVatRegNumber());
        stornoInvoice.setBillingMethod(invoice.getBillingMethod());
        stornoInvoice.setStreet(invoice.getStreet());
        stornoInvoice.setCity(invoice.getCity());
        stornoInvoice.setZipCode(invoice.getZipCode());
        stornoInvoice.setCountryCode(invoice.getCountryCode());
        stornoInvoice.setCountry(invoice.getCountry());
        stornoInvoice.setStartDate(invoice.getStartDate());
        stornoInvoice.setEndDate(invoice.getEndDate());
        stornoInvoice.setDateOfFulfilment(invoice.getDateOfFulfilment());
        stornoInvoice.setPaymentDeadline(invoice.getPaymentDeadline());
        stornoInvoice.setBankName(invoice.getBankName());
        stornoInvoice.setBankAddress(invoice.getBankAddress());
        stornoInvoice.setBankAccount(invoice.getBankAccount());
        stornoInvoice.setSwiftCode(invoice.getSwiftCode());
        stornoInvoice.setExchangeRate(invoice.getExchangeRate());
        stornoInvoice.setOptionalText(invoice.getOptionalText());
        final Invoice result = invoiceRepository.save(stornoInvoice);

        InvoicePayingGroup payingGroup = invoicePayingGroupRepository.findByInvoiceId(invoice.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invoice paying group not found by id: " + invoice.getId()));
        InvoicePayingGroup ipg = new InvoicePayingGroup();
        ipg.setInvoice(result);
        ipg.setPayingGroup(payingGroup.getPayingGroup());
        invoicePayingGroupRepository.save(ipg);

        List<GroupDiscountInvoiceHistory> histories = groupDiscountInvoiceHistoryRepository.findAllByInvoice(invoice);
        histories.forEach(history -> {
            GroupDiscountInvoiceHistory gdih = new GroupDiscountInvoiceHistory();
            gdih.setChargeableItem(history.getChargeableItem());
            gdih.setInvoice(result);
            groupDiscountInvoiceHistoryRepository.save(gdih);
        });

        List<InvoiceItem> invoiceItemList = invoiceItemRepository.findAllByInvoice(invoice);
        invoiceItemList.forEach(item -> {
            invoiceItemRepository.save(InvoiceItem.stornoItem(item, result));
        });

        invoice.setStornired(true);
        invoiceRepository.save(invoice);
        return result;
    }

}
