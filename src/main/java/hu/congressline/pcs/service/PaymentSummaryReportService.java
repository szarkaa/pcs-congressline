package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.MiscInvoiceItem;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.InvoiceCongressRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.InvoicePayingGroupRepository;
import hu.congressline.pcs.repository.MiscInvoiceItemRepository;
import hu.congressline.pcs.service.dto.PaymentSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentSummaryReportService extends XlsReportService {

    private final ChargedServiceService csService;
    private final InvoicePayingGroupRepository ipRepository;
    private final DiscountService discountService;
    private final GroupDiscountInvoiceHistoryRepository gdihRepository;
    private final InvoiceCongressRepository icRepository;
    private final MiscInvoiceItemRepository miiRepository;
    private final InvoiceItemRepository iiRepository;
    private RegistrationService registrationService;

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    @Transactional(readOnly = true)
    public List<PaymentSummaryDTO> findAll(Congress congress) {
        log.debug("Request to get all PaymentSummaryDTOs");
        final Set<PaymentSummaryDTO> paymentSet = new HashSet<>();
        congress.getCurrencies().forEach(currency -> {
            PaymentSummaryDTO dto = new PaymentSummaryDTO();
            dto.setCurrency(currency.getCurrency());
            paymentSet.add(dto);
        });
        final Set<String> currencies = paymentSet.stream().map(PaymentSummaryDTO::getCurrency).collect(Collectors.toSet());

        final List<ChargedService> chargedServices = csService.findAllByCongress(congress);
        currencies.forEach(currency -> {
            PaymentSummaryDTO dto = paymentSet.stream().filter(o -> currency.equals(o.getCurrency())).findFirst().get();

            final BigDecimal registrationFee = chargedServices.stream()
                    .filter(cs -> cs.getChargeableItem() != null)
                    .filter(cs -> currency.equals(cs.getChargeableItem().getChargeableItemCurrency()))
                    .filter(cs -> ChargeableItemType.REGISTRATION.equals(cs.getChargeableItem().getChargeableItemType()))
                    .map(ChargedService::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setRegistrationFee(registrationFee);

            final BigDecimal reservationFee = chargedServices.stream()
                    .filter(cs -> cs.getChargeableItem() != null)
                    .filter(cs -> currency.equals(cs.getChargeableItem().getChargeableItemCurrency()))
                    .filter(cs -> ChargeableItemType.HOTEL.equals(cs.getChargeableItem().getChargeableItemType()))
                    .map(ChargedService::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setReservationFee(reservationFee);

            final BigDecimal optionalFee = chargedServices.stream()
                    .filter(cs -> cs.getChargeableItem() != null)
                    .filter(cs -> currency.equals(cs.getChargeableItem().getChargeableItemCurrency()))
                    .filter(cs -> ChargeableItemType.OPTIONAL_SERVICE.equals(cs.getChargeableItem().getChargeableItemType()))
                    .map(ChargedService::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setOptionalFee(optionalFee);

            final BigDecimal miscFee = chargedServices.stream()
                    .filter(cs -> cs.getChargeableItem() == null) // all miscellaneous payment has null chargeable item
                    .filter(cs -> currency.equals(registrationService.getRegistrationCurrency(cs.getRegistration())))
                    .map(ChargedService::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setMiscFee(miscFee);

            dto.setTotal(dto.getRegistrationFee().add(dto.getReservationFee()).add(dto.getOptionalFee()).add(dto.getMiscFee()));
        });

        //Discount invoice payment summary
        List<Invoice> invoices = ipRepository.findByPayingGroupCongressId(congress.getId()).stream()
                .filter(ipg -> !ipg.getInvoice().getStornired())
                .filter(ipg -> ipg.getDateOfGroupPayment() != null)
                .map(InvoicePayingGroup::getInvoice)
                .collect(Collectors.toList());
        final List<GroupDiscountInvoiceHistory> discountInvoiceHistories = gdihRepository.findAllByInvoiceIn(invoices);
        final List<ChargeableItem> chargeableItems = discountInvoiceHistories.stream().map(GroupDiscountInvoiceHistory::getChargeableItem).collect(Collectors.toList());

        currencies.forEach(currency -> {
            PaymentSummaryDTO dto = paymentSet.stream().filter(o -> currency.equals(o.getCurrency())).findFirst().get();

            final BigDecimal registrationFee = chargeableItems.stream()
                    .filter(cs -> currency.equals(cs.getChargeableItemCurrency()))
                    .filter(cs -> ChargeableItemType.REGISTRATION.equals(cs.getChargeableItemType()))
                    .map(ci -> discountService.getAmountOfDiscount((RegistrationRegistrationType) ci)).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setRegistrationFee(dto.getRegistrationFee().add(registrationFee));

            final BigDecimal reservationFee = chargeableItems.stream()
                    .filter(cs -> currency.equals(cs.getChargeableItemCurrency()))
                    .filter(cs -> ChargeableItemType.HOTEL.equals(cs.getChargeableItemType()))
                    .map(ci -> discountService.getAmountOfDiscount((RoomReservationRegistration) ci)).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setReservationFee(dto.getReservationFee().add(reservationFee));

            final BigDecimal optionalFee = chargeableItems.stream()
                    .filter(cs -> currency.equals(cs.getChargeableItemCurrency()))
                    .filter(cs -> ChargeableItemType.OPTIONAL_SERVICE.equals(cs.getChargeableItemType()))
                    .map(ci -> discountService.getAmountOfDiscount((OrderedOptionalService) ci)).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setOptionalFee(dto.getOptionalFee().add(optionalFee));

            dto.setTotal(dto.getRegistrationFee().add(dto.getReservationFee()).add(dto.getOptionalFee()).add(dto.getMiscFee()));
        });

        //Misc invoice payment summary
        invoices = icRepository.findByCongressId(congress.getId()).stream()
                .filter(ic -> ic.getDateOfPayment() != null)
                .map(InvoiceCongress::getInvoice)
                .filter(invoice -> !invoice.getStornired()).collect(Collectors.toList());
        final List<MiscInvoiceItem> miscInvoiceItems = miiRepository.findAllByInvoiceIn(invoices);
        invoices = new ArrayList<>(miscInvoiceItems.stream().filter(miscInvoiceItem -> miscInvoiceItem.getDateOfPayment() != null).map(MiscInvoiceItem::getInvoice)
                .collect(Collectors.toSet()));
        List<InvoiceItem> invoiceItems = iiRepository.findAllByInvoiceIn(invoices);

        currencies.forEach(currency -> {
            PaymentSummaryDTO dto = paymentSet.stream().filter(o -> currency.equals(o.getCurrency())).findFirst().get();
            final BigDecimal miscFee = invoiceItems.stream()
                    .filter(item -> currency.equals(item.getCurrency()))
                    .map(InvoiceItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setMiscFee(dto.getMiscFee().add(miscFee));

            dto.setTotal(dto.getRegistrationFee().add(dto.getReservationFee()).add(dto.getOptionalFee()).add(dto.getMiscFee()));
        });

        return new ArrayList<>(paymentSet);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress) throws IOException {
        final List<PaymentSummaryDTO> summaryList = findAll(congress);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Currency", 100);
        columns.put("Registration fee", 200);
        columns.put("Reservation fee", 200);
        columns.put("Optional service fee", 200);
        columns.put("Misc fee", 100);
        columns.put("Total", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Payment summary", null, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (PaymentSummaryDTO dto : summaryList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getCurrency());
            addCell(row, wrappingCellStyle, 1, dto.getRegistrationFee());
            addCell(row, wrappingCellStyle, 2, dto.getReservationFee());
            addCell(row, wrappingCellStyle, 3, dto.getOptionalFee());
            addCell(row, wrappingCellStyle, 4, dto.getMiscFee());
            addCell(row, wrappingCellStyle, 5, dto.getTotal());
            rowIndex++;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the payment summary report XLSX file", e);
            throw e;
        }
    }
}
