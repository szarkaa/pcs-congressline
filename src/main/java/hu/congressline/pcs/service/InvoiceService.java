package hu.congressline.pcs.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.ChargeableItemInvoiceHistory;
import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.ChargedServiceInvoiceHistory;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCharge;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.Language;
import hu.congressline.pcs.repository.ChargeableItemInvoiceHistoryRepository;
import hu.congressline.pcs.repository.ChargedServiceInvoiceHistoryRepository;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.InvoiceChargeRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.InvoiceRegistrationRepository;
import hu.congressline.pcs.repository.InvoiceRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.service.dto.InvoiceDTO;
import hu.congressline.pcs.web.rest.vm.InvoiceVM;
import hu.congressline.pcs.web.rest.vm.SetPaymentDateVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.service.util.DateUtil.DATE_FORMAT_EN;
import static hu.congressline.pcs.service.util.DateUtil.DATE_FORMAT_HU;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class InvoiceService {

    protected final MessageSource messageSource;
    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final OrderedOptionalServiceRepository oosRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceRegistrationRepository invoiceRegistrationRepository;
    private final ChargeableItemInvoiceHistoryRepository chargeableItemInvoiceHistoryRepository;
    private final ChargedServiceInvoiceHistoryRepository chargedServiceInvoiceHistoryRepository;
    private final CompanyService companyService;
    private final ChargedServiceService chargedServiceService;
    private final RegistrationService registrationService;
    private final DiscountService discountService;
    private final InvoiceChargeRepository invoiceChargeRepository;
    private final PriceService priceService;
    private final CurrencyService currencyService;
    private final CountryRepository countyRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public InvoiceRegistration save(InvoiceVM invoiceVM) {
        log.debug("Request to save invoice : {}", invoiceVM);
        final Registration registration = registrationService.getById(invoiceVM.getRegistrationId());
        final String currency = registrationService.getRegistrationCurrency(registration);

        Invoice invoice = new Invoice();
        invoice.setInvoiceType(InvoiceType.REGULAR);
        invoice.setNavVatCategory(invoiceVM.getNavVatCategory());
        invoice.setCreatedDate(LocalDate.now());
        invoice.setInvoiceNumber(companyService.getNextFullInvoiceNumber());
        invoice.setName1(invoiceVM.getName1());
        invoice.setName2(invoiceVM.getName2());
        //invoice.setOptionalName(invoiceVM.getOptionalName());
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
        invoice.setBankName(invoiceVM.getBankAccount().getBankName());
        invoice.setBankAddress(invoiceVM.getBankAccount().getBankAddress());
        invoice.setBankAccount(invoiceVM.getBankAccount().getBankAccount());
        invoice.setSwiftCode(invoiceVM.getBankAccount().getSwiftCode());
        invoice.setOptionalText(invoiceVM.getOptionalText());
        invoice.setExchangeRate(currencyService.getRateForDate(currency, LocalDate.now()));
        Invoice result = invoiceRepository.save(invoice);

        InvoiceRegistration ir = new InvoiceRegistration();
        ir.setInvoice(result);
        ir.setRegistration(registration);
        InvoiceRegistration invoiceRegistration = invoiceRegistrationRepository.save(ir);

        final List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationId(registration.getId());
        final List<RoomReservationRegistration> rrrList = rrrRepository.findAllByRegistrationId(registration.getId());
        final List<OrderedOptionalService> oosList = oosRepository.findAllByRegistrationId(registration.getId());

        saveInvoiceItemDetails(result, invoiceVM.getIgnoredChargeableItemIdList(), rrtList);
        saveInvoiceItemDetails(result, invoiceVM.getIgnoredChargeableItemIdList(), rrrList);
        saveInvoiceItemDetails(result, invoiceVM.getIgnoredChargeableItemIdList(), oosList);

        saveInvoiceChargeDetails(invoice, invoiceVM.getIgnoredChargedServiceIdList(), chargedServiceService.findAllByRegistrationId(invoiceRegistration.getRegistration().getId()));
        return invoiceRegistration;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Invoice> findById(Long id) {
        log.debug("Request to find invoice : {}", id);
        return id != null ? invoiceRepository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Invoice getById(Long id) {
        log.debug("Request to get invoice : {}", id);
        return invoiceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invoice not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public InvoiceRegistration getInvoiceRegistrationByInvoiceId(Long id) {
        log.debug("Request to get invoice by invoice id: {}", id);
        return invoiceRegistrationRepository.findByInvoiceId(id)
                .orElseThrow(() -> new IllegalArgumentException("InvoiceRegistration not found by invoice id:" + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public Invoice stornoInvoice(Long id) {
        log.debug("Request to get storno invoice : {}", id);
        Invoice invoice = getById(id);
        if (invoice.getStornired()) {
            throw new IllegalArgumentException("This invoice is already stornired invoice id: " + id);
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
        stornoInvoice.setOptionalName(invoice.getOptionalName());
        stornoInvoice.setVatRegNumber(invoice.getVatRegNumber());
        stornoInvoice.setStreet(invoice.getStreet());
        stornoInvoice.setCity(invoice.getCity());
        stornoInvoice.setZipCode(invoice.getZipCode());
        stornoInvoice.setCountryCode(invoice.getCountryCode());
        stornoInvoice.setCountry(invoice.getCountry());
        stornoInvoice.setExchangeRate(invoice.getExchangeRate());
        final Invoice result = invoiceRepository.save(stornoInvoice);

        Registration registration = invoiceRegistrationRepository.findByInvoice(invoice).stream().map(InvoiceRegistration::getRegistration).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Registration not found by invoice id: " + invoice.getId()));
        InvoiceRegistration ir = new InvoiceRegistration();
        ir.setInvoice(result);
        ir.setRegistration(registration);
        invoiceRegistrationRepository.save(ir);

        List<ChargeableItemInvoiceHistory> itemHistories = chargeableItemInvoiceHistoryRepository.findAllByInvoice(invoice);
        itemHistories.forEach(history -> saveInvoiceHistory(result, history.getChargeableItem()));

        List<ChargedServiceInvoiceHistory> chargedServiceHistories = chargedServiceInvoiceHistoryRepository.findAllByInvoice(invoice);
        chargedServiceHistories.forEach(history -> {
            ChargedServiceInvoiceHistory csih = new ChargedServiceInvoiceHistory();
            csih.setChargedService(history.getChargedService());
            csih.setInvoice(result);
            chargedServiceInvoiceHistoryRepository.save(csih);
        });

        List<InvoiceItem> invoiceItemList = invoiceItemRepository.findAllByInvoice(invoice);
        invoiceItemList.forEach(item -> {
            InvoiceItem stornoItem = InvoiceItem.stornoItem(item, result);
            invoiceItemRepository.save(stornoItem);
        });

        List<InvoiceCharge> invoiceChargeList = invoiceChargeRepository.findAllByInvoice(invoice);
        invoiceChargeList.forEach(charge -> {
            InvoiceCharge stornoCharge = InvoiceCharge.stornoCharge(charge);
            stornoCharge.setInvoice(result);
            invoiceChargeRepository.save(stornoCharge);
        });

        invoice.setStornired(true);
        invoiceRepository.save(invoice);
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<InvoiceDTO> findAllByRegistrationId(Long id) {
        log.debug("Request to get all invoices by registration id");
        List<InvoiceRegistration> result = invoiceRegistrationRepository.findByRegistrationIdOrderByIdDesc(id);
        return result.stream().map(invoiceRegistration -> {
            InvoiceDTO dto = new InvoiceDTO(invoiceRegistration.getInvoice());
            dto.setDateOfPayment(invoiceRegistration.getDateOfPayment());
            return dto;
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<Long> getInvoicedChargeableItemIds(Long registrationId) {
        final List<InvoiceRegistration> irList = invoiceRegistrationRepository.findByRegistrationId(registrationId);
        List<ChargeableItemInvoiceHistory> histories = chargeableItemInvoiceHistoryRepository
                .findAllByInvoiceInOrderById(irList.stream().map(InvoiceRegistration::getInvoice).collect(Collectors.toList()));
        final Map<Long, Invoice> itemMap = new HashMap<>();
        histories.forEach(history -> itemMap.put(history.getChargeableItem().getId(), history.getInvoice()));

        return itemMap.keySet().stream().filter(itemId -> itemMap.get(itemId).getStorno() == null || !itemMap.get(itemId).getStorno()).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<Long> getInvoicedChargedServiceIds(Long registrationId) {
        final Map<Long, Invoice> itemMap = getInvoicedChargedServices(registrationId);
        return itemMap.keySet().stream().filter(itemId -> itemMap.get(itemId).getStorno() == null || !itemMap.get(itemId).getStorno()).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Map<Long, Invoice> getInvoicedChargedServices(Long registrationId) {
        final List<InvoiceRegistration> irList = invoiceRegistrationRepository.findByRegistrationId(registrationId);
        List<ChargedServiceInvoiceHistory> histories = chargedServiceInvoiceHistoryRepository
                .findAllByInvoiceInOrderById(irList.stream().map(InvoiceRegistration::getInvoice).collect(Collectors.toList()));
        final Map<Long, Invoice> itemMap = new HashMap<>();
        histories.forEach(history -> itemMap.put(history.getChargedService().getId(), history.getInvoice()));
        return itemMap;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete invoice : {}", id);
        invoiceRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public InvoiceDTO setPaymentDate(SetPaymentDateVM viewModel) {
        InvoiceRegistration ir = getInvoiceRegistrationByInvoiceId(viewModel.getId());
        if (ir == null) {
            return null;
        }
        ir.setDateOfPayment(viewModel.getPaymentDate());
        InvoiceRegistration result = invoiceRegistrationRepository.save(ir);

        final List<ChargeableItemInvoiceHistory> invoiceHistories = chargeableItemInvoiceHistoryRepository.findAllByInvoice(ir.getInvoice());
        final List<ChargeableItem> chargeableItems = invoiceHistories.stream().map(ChargeableItemInvoiceHistory::getChargeableItem).collect(Collectors.toList());
        chargeableItems.forEach(item -> {
            item.setDateOfGroupPayment(viewModel.getPaymentDate());
            if (ChargeableItemType.REGISTRATION.equals(item.getChargeableItemType())) {
                rrtRepository.save((RegistrationRegistrationType) item);
            } else if (ChargeableItemType.HOTEL.equals(item.getChargeableItemType())) {
                rrrRepository.save((RoomReservationRegistration) item);
            } else if (ChargeableItemType.OPTIONAL_SERVICE.equals(item.getChargeableItemType())) {
                oosRepository.save((OrderedOptionalService) item);
            }
        });

        final InvoiceDTO dto = new InvoiceDTO(result.getInvoice());
        dto.setDateOfPayment(result.getDateOfPayment());
        return dto;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<Invoice> findAllByNavStatus(List<InvoiceNavStatus> statuses) {
        return invoiceRepository.findAllByNavStatusIn(statuses);
    }

    private void saveInvoiceItemDetails(Invoice invoice, List<Long> ignoredChargeableItemIdList, List<? extends ChargeableItem> chargeableItems) {
        for (ChargeableItem item : chargeableItems) {
            if (!ignoredChargeableItemIdList.contains(item.getId())) {
                saveInvoiceHistory(invoice, item);
                saveInvoiceItems(invoice, item);
            }
        }
    }

    private void saveInvoiceChargeDetails(Invoice invoice, List<Long> ignoreList, List<ChargedService> chargedServices) {
        for (ChargedService service : chargedServices) {
            if (!ignoreList.contains(service.getId())) {
                saveChargedServiceInvoiceNumber(invoice, service);
                saveInvoiceCharges(invoice, service);
            }
        }
    }

    private ChargeableItemInvoiceHistory saveInvoiceHistory(Invoice invoice, ChargeableItem item) {
        ChargeableItemInvoiceHistory history = new ChargeableItemInvoiceHistory();
        history.setInvoice(invoice);
        history.setChargeableItem(item);
        return chargeableItemInvoiceHistoryRepository.save(history);
    }

    private void saveInvoiceItems(Invoice invoice, ChargeableItem item) {
        if (item instanceof RegistrationRegistrationType) {
            saveInvoiceRRTItems(invoice, (RegistrationRegistrationType) item);
        } else if (item instanceof RoomReservationRegistration) {
            saveInvoiceRRRItems(invoice, (RoomReservationRegistration) item);
        } else if (item instanceof OrderedOptionalService) {
            saveInvoiceOOSItems(invoice, (OrderedOptionalService) item);
        }
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private void saveInvoiceRRTItems(Invoice invoice, RegistrationRegistrationType rrt) {
        final BigDecimal total = discountService.getPriceWithDiscount(rrt);
        //if (BigDecimal.ZERO.compareTo(total) < 0) {
        InvoiceItem item = new InvoiceItem();
        item.setItemType(ChargeableItemType.REGISTRATION);
        item.setInvoice(invoice);
        item.setItemName(rrt.getChargeableItemName());
        item.setSzj(rrt.getChargeableItemSZJ());
        item.setCurrency(rrt.getChargeableItemCurrency());
        item.setUnit(rrt.getAccPeople() != null && rrt.getAccPeople() > 0 ? rrt.getAccPeople() : 1);
        item.setUnitPrice(priceService.getVatBase(discountService.getPriceWithDiscount(rrt.getPayingGroupItem(), rrt.getRegFee(), priceService.getScale(rrt)),
                rrt.getChargeableItemVAT(), priceService.getScale(rrt)));
        item.setVatBase(priceService.getVatBase(discountService.getPriceWithDiscount(rrt), rrt.getChargeableItemVAT(), priceService.getScale(rrt)));
        item.setVat(rrt.getChargeableItemVAT());
        item.setVatName(rrt.getRegistrationType().getVatInfo().getName());
        item.setVatValue(priceService.getVatValue(discountService.getPriceWithDiscount(rrt), rrt.getChargeableItemVAT(), priceService.getScale(rrt)));
        item.setVatRateType(rrt.getRegistrationType().getVatInfo().getVatRateType());
        item.setVatExceptionReason(rrt.getRegistrationType().getVatInfo().getVatExceptionReason());
        item.setTotal(total);
        invoiceItemRepository.save(item);
        //}
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private void saveInvoiceRRRItems(Invoice invoice, RoomReservationRegistration rrr) {
        final BigDecimal total = discountService.getPriceWithDiscount(rrr);
        //if (BigDecimal.ZERO.compareTo(total) < 0) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(invoice.getPrintLocale().equals(Language.HU.toString().toLowerCase()) ? DATE_FORMAT_HU : DATE_FORMAT_EN);
        InvoiceItem item = new InvoiceItem();
        item.setItemType(ChargeableItemType.HOTEL);
        item.setInvoice(invoice);
        final LocalDate arrivalDate = rrr.getRoomReservation().getArrivalDate();
        final LocalDate departureDate = rrr.getRoomReservation().getDepartureDate();
        final Hotel hotel = rrr.getRoomReservation().getRoom().getCongressHotel().getHotel();
        item.setItemName(hotel.getName() + "," + " " + rrr.getRoomReservation().getRoom().getRoomType() + " (" + arrivalDate.format(formatter) + "-"
                + departureDate.format(formatter) + ")" + hotel.getZipCode() + ", " + hotel.getCity() + " " + hotel.getStreet());
        item.setSzj(rrr.getChargeableItemSZJ());
        item.setVat(rrr.getChargeableItemVAT());
        item.setVatName(rrr.getRoomReservation().getRoom().getVatInfo().getName());
        item.setCurrency(rrr.getChargeableItemCurrency());
        item.setUnit((int) (ChronoUnit.DAYS.between(arrivalDate, departureDate)));

        BigDecimal vatBase = priceService.getVatBase(discountService.getRoomReservationPriceWithDiscount(discountService.getPayingGroupItemFromRoomReservation(rrr), rrr),
                    rrr.getChargeableItemVAT(), priceService.getScale(rrr));
        vatBase = vatBase.divide(new BigDecimal(ChronoUnit.DAYS.between(arrivalDate, departureDate)), 2, RoundingMode.HALF_UP);
        item.setUnitPrice(vatBase);

        item.setVatBase(priceService.getVatBase(discountService.getPriceWithDiscount(rrr), rrr.getChargeableItemVAT(), priceService.getScale(rrr)));
        item.setVatValue(priceService.getVatValue(discountService.getPriceWithDiscount(rrr), rrr.getChargeableItemVAT(), priceService.getScale(rrr)));
        item.setVatRateType(rrr.getRoomReservation().getRoom().getVatInfo().getVatRateType());
        item.setVatExceptionReason(rrr.getRoomReservation().getRoom().getVatInfo().getVatExceptionReason());
        item.setTotal(discountService.getPriceWithDiscount(rrr));
        invoiceItemRepository.save(item);
        //}
    }

    @SuppressWarnings("AbbreviationAsWordInName")
    private void saveInvoiceOOSItems(Invoice invoice, OrderedOptionalService oos) {
        final BigDecimal total = discountService.getPriceWithDiscount(oos);
        //if (BigDecimal.ZERO.compareTo(total) < 0) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(invoice.getPrintLocale().equals("hu") ? DATE_FORMAT_HU : DATE_FORMAT_EN);
        InvoiceItem item = new InvoiceItem();
        item.setItemType(ChargeableItemType.OPTIONAL_SERVICE);
        item.setInvoice(invoice);
        item.setItemName(oos.getChargeableItemName());
        item.setItemDesc(oos.getOptionalService().getStartDate().format(formatter));
        item.setSzj(oos.getChargeableItemSZJ());
        item.setCurrency(oos.getChargeableItemCurrency());
        item.setUnit(oos.getParticipant());
        item.setUnitPrice(priceService.getVatBase(discountService.getOptionalServicePriceWithDiscountPerParticipants(oos), oos.getChargeableItemVAT(), priceService.getScale(oos)));
        item.setVatBase(priceService.getVatBase(discountService.getPriceWithDiscount(oos), oos.getChargeableItemVAT(), priceService.getScale(oos)));
        item.setVat(oos.getChargeableItemVAT());
        item.setVatName(oos.getOptionalService().getVatInfo().getName());
        item.setVatValue(priceService.getVatValue(discountService.getPriceWithDiscount(oos), oos.getChargeableItemVAT(), priceService.getScale(oos)));
        item.setVatRateType(oos.getOptionalService().getVatInfo().getVatRateType());
        item.setVatExceptionReason(oos.getOptionalService().getVatInfo().getVatExceptionReason());
        item.setTotal(discountService.getPriceWithDiscount(oos));
        invoiceItemRepository.save(item);
        //}
    }

    private void saveInvoiceCharges(Invoice invoice, ChargedService service) {
        InvoiceCharge charge = new InvoiceCharge();
        charge.setItemType(service.getPaymentType());
        charge.setInvoice(invoice);
        if (service.getPaymentType().equals(ChargeableItemType.HOTEL)) {
            charge.setItemName(((RoomReservationRegistration) service.getChargeableItem()).getRoomReservation().getRoom().getCongressHotel().getHotel().getName());
            charge.setCurrency(service.getChargeableItem().getChargeableItemCurrency());
        } else if (service.getPaymentType().equals(ChargeableItemType.MISCELLANEOUS)) {
            charge.setItemName(messageSource.getMessage("invoice.pdf.miscPayment", new Object[]{}, Locale.forLanguageTag(invoice.getPrintLocale())));
            charge.setCurrency(registrationService.getRegistrationCurrency(service.getRegistration()));
        } else {
            charge.setItemName(service.getChargeableItem().getChargeableItemName());
            charge.setCurrency(service.getChargeableItem().getChargeableItemCurrency());
        }
        charge.setAmount(service.getAmount().negate());
        invoiceChargeRepository.save(charge);
    }

    private void saveChargedServiceInvoiceNumber(Invoice invoice, ChargedService service) {
        ChargedServiceInvoiceHistory chargedServiceinvoiceHistory = new ChargedServiceInvoiceHistory();
        chargedServiceinvoiceHistory.setInvoice(invoice);
        chargedServiceinvoiceHistory.setChargedService(service);
        chargedServiceInvoiceHistoryRepository.save(chargedServiceinvoiceHistory);
    }
}
