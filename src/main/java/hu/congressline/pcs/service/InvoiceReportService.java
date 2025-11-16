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
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoiceNavValidation;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.enumeration.InvoiceNavStatus;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.VatRateType;
import hu.congressline.pcs.repository.InvoiceCongressRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.InvoiceNavValidationRepository;
import hu.congressline.pcs.repository.InvoicePayingGroupRepository;
import hu.congressline.pcs.repository.InvoiceRegistrationRepository;
import hu.congressline.pcs.service.dto.InvoiceReportDTO;
import hu.congressline.pcs.web.rest.vm.InvoiceReportVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.domain.enumeration.Currency.HUF;
import static java.util.Comparator.naturalOrder;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class InvoiceReportService extends XlsReportService implements MonetaryService {

    private static final String INVOICE_NUMBER = "Invoice number";
    private static final String STORNO_INVOICE_NUMBER = "Storno invoice number";
    private static final String INVOICE_TYPE = "Invoice type";
    private static final String NAME2 = "Name2";
    private static final String NAME1 = "Name1";
    private static final String OPTIONAL_NAME = "Optional name";
    private static final String TAX_NO = "Tax no.";
    private static final String ZIPCODE = "Zipcode";
    private static final String CITY = "City";
    private static final String STREET = "Street";
    private static final String COUNTRY = "Country";
    private static final String CONGRESS_NAME = "Congress name";
    private static final String PROGRAM_NUMBER = "Program number";
    private static final String DATE_OF_INVOICE = "Date of invoice";
    private static final String DATE_OF_FULFILMENT = "Date of fulfilment";
    private static final String METHOD_OF_PAYMENT = "Method of payment";
    private static final String DATE_OF_PAYMENT = "Date of payment";
    private static final String PAYMENT_DEADLINE = "Payment deadline";
    private static final String CURRENCY = "Currency";
    private static final String EXCHANGE_RATE = "Exchange rate";

    private final InvoiceRegistrationRepository invoiceRegistrationRepository;
    private final InvoicePayingGroupRepository invoicePayingGroupRepository;
    private final InvoiceCongressRepository invoiceCongressRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoicePdfService invoicePdfService;
    private final GroupDiscountInvoicePdfService payingGroupPdfService;
    private final MiscInvoicePdfService miscInvoicePdfService;
    private final InvoiceNavValidationRepository invoiceNavValidationRepository;

    @SuppressWarnings({"MissingJavadocMethod", "AbbreviationAsWordInName"})
    @Transactional(readOnly = true)
    public List<InvoiceReportDTO> findAllDTOS(InvoiceReportVM reportFilter) {
        log.debug("Request to get all InvoiceReportDTO");
        final List<InvoiceReportDTO> invoices = new ArrayList<>();
        final String programNumber = reportFilter.getProgramNumber();
        final String invoiceNumber = reportFilter.getInvoiceNumber();
        List<InvoiceType> invoiceTypes = new ArrayList<>();
        if (!reportFilter.getFilterProforma()) {
            invoiceTypes.add(InvoiceType.PRO_FORMA);
        } else {
            invoiceTypes.add(InvoiceType.REGULAR);
            invoiceTypes.add(InvoiceType.PREPAYMENT);
        }
        LocalDate defaultFromDate = LocalDate.of(1900, Month.JANUARY, 1);
        LocalDate defaultToDate = LocalDate.of(2200, Month.JANUARY, 1);
        LocalDate fromDate = reportFilter.getFromDate() != null ? reportFilter.getFromDate() : defaultFromDate;
        LocalDate toDate = reportFilter.getToDate() != null ? reportFilter.getToDate() : defaultToDate;
        if (invoiceNumber == null && programNumber == null) {
            invoices.addAll(invoiceRegistrationRepository
                    .findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(invoiceTypes, fromDate, toDate)
                    .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(invoiceTypes, fromDate, toDate)
                    .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(invoiceTypes, fromDate, toDate)
                    .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
        } else if (programNumber != null && invoiceNumber != null) {
            invoices.addAll(invoiceRegistrationRepository
                    .findByInvoiceInvoiceTypeInAndRegistrationCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                            invoiceTypes, programNumber, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository
                .findByInvoiceInvoiceTypeInAndPayingGroupCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                        invoiceTypes, programNumber, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository
                    .findByInvoiceInvoiceTypeInAndCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                            invoiceTypes, programNumber, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
        } else if (programNumber != null) {
            invoices.addAll(invoiceRegistrationRepository
                .findByInvoiceInvoiceTypeInAndRegistrationCongressProgramNumberAndInvoiceCreatedDateBetween(invoiceTypes, programNumber, fromDate, toDate)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository
                .findByInvoiceInvoiceTypeInAndPayingGroupCongressProgramNumberAndInvoiceCreatedDateBetween(invoiceTypes, programNumber, fromDate, toDate)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository
                .findByInvoiceInvoiceTypeInAndCongressProgramNumberAndInvoiceCreatedDateBetween(invoiceTypes, programNumber, fromDate, toDate)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
        } else if (invoiceNumber != null) {
            invoices.addAll(invoiceRegistrationRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                    invoiceTypes, fromDate, toDate, invoiceNumber, invoiceNumber)
                    .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                    invoiceTypes, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                    invoiceTypes, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceReportDTO::new).collect(Collectors.toList()));
        }

        List<InvoiceItem> invoiceItemList = invoiceItemRepository.findAllByInvoiceIdIn(invoices.stream().map(InvoiceReportDTO::getId).collect(Collectors.toList()));

        invoices.forEach(invoice -> addInvoiceItemsToDTO(invoice, invoiceItemList));
        invoices.sort(Comparator.comparing(InvoiceReportDTO::getInvoiceNumber, Comparator.nullsLast(naturalOrder())));
        return invoices;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Invoice> findAll(InvoiceReportVM reportFilter) {
        log.debug("Request to get all Invoices by report filter");
        final List<Invoice> invoices = new ArrayList<>();
        final String programNumber = reportFilter.getProgramNumber();
        final String invoiceNumber = reportFilter.getInvoiceNumber();
        List<InvoiceType> invoiceTypes = new ArrayList<>();
        if (!reportFilter.getFilterProforma()) {
            invoiceTypes.add(InvoiceType.PRO_FORMA);
        } else {
            invoiceTypes.add(InvoiceType.REGULAR);
            invoiceTypes.add(InvoiceType.PREPAYMENT);
        }
        LocalDate defaultFromDate = LocalDate.of(1900, Month.JANUARY, 1);
        LocalDate defaultToDate = LocalDate.of(2200, Month.JANUARY, 1);
        LocalDate fromDate = reportFilter.getFromDate() != null ? reportFilter.getFromDate() : defaultFromDate;
        LocalDate toDate = reportFilter.getToDate() != null ? reportFilter.getToDate() : defaultToDate;
        if (invoiceNumber == null && programNumber == null) {
            invoices.addAll(invoiceRegistrationRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(invoiceTypes, fromDate, toDate)
                    .stream().map(InvoiceRegistration::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(invoiceTypes, fromDate, toDate)
                    .stream().map(InvoicePayingGroup::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository.findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetween(invoiceTypes, fromDate, toDate)
                    .stream().map(InvoiceCongress::getInvoice).collect(Collectors.toList()));
        } else if (programNumber != null && invoiceNumber != null) {
            invoices.addAll(invoiceRegistrationRepository
                .findByInvoiceInvoiceTypeInAndRegistrationCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                        invoiceTypes, programNumber, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceRegistration::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository
                .findByInvoiceInvoiceTypeInAndPayingGroupCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                        invoiceTypes, programNumber, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoicePayingGroup::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository
                .findByInvoiceInvoiceTypeInAndCongressProgramNumberAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                        invoiceTypes, programNumber, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceCongress::getInvoice).collect(Collectors.toList()));
        } else if (programNumber != null) {
            invoices.addAll(invoiceRegistrationRepository
                .findByInvoiceInvoiceTypeInAndRegistrationCongressProgramNumberAndInvoiceCreatedDateBetween(
                        invoiceTypes, programNumber, fromDate, toDate)
                .stream().map(InvoiceRegistration::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository
                .findByInvoiceInvoiceTypeInAndPayingGroupCongressProgramNumberAndInvoiceCreatedDateBetween(
                        invoiceTypes, programNumber, fromDate, toDate)
                .stream().map(InvoicePayingGroup::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository
                .findByInvoiceInvoiceTypeInAndCongressProgramNumberAndInvoiceCreatedDateBetween(
                        invoiceTypes, programNumber, fromDate, toDate)
                .stream().map(InvoiceCongress::getInvoice).collect(Collectors.toList()));
        } else if (invoiceNumber != null) {
            invoices.addAll(invoiceRegistrationRepository
                    .findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                            invoiceTypes, fromDate, toDate, invoiceNumber, invoiceNumber)
                    .stream().map(InvoiceRegistration::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoicePayingGroupRepository
                .findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                        invoiceTypes, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoicePayingGroup::getInvoice).collect(Collectors.toList()));
            invoices.addAll(invoiceCongressRepository
                .findByInvoiceInvoiceTypeInAndInvoiceCreatedDateBetweenAndInvoiceInvoiceNumberOrInvoiceStornoInvoiceNumber(
                        invoiceTypes, fromDate, toDate, invoiceNumber, invoiceNumber)
                .stream().map(InvoiceCongress::getInvoice).collect(Collectors.toList()));
        }

        invoices.sort(Comparator.comparing(Invoice::getInvoiceNumber, Comparator.nullsLast(naturalOrder())));
        return invoices;
    }

    protected void addInvoiceItemsToDTO(InvoiceReportDTO invoice, List<InvoiceItem> invoiceItemList) {
        int scale = 0; //invoiceItemList.size() > 0 && HUF.toString().equalsIgnoreCase(invoiceItemList.get(0).getCurrency()) ? 0 : 2;
        final List<InvoiceItem> invoiceItems = invoiceItemList.stream().filter(item -> item.getInvoice().getId().equals(invoice.getId())).collect(Collectors.toList());
        final Map<Integer, InvoiceReportDTO.InvoiceReportVatItemDTO> vatItemDTOMap = new HashMap<>();
        invoiceItems.forEach(item -> {
            final Integer vat = item.getVat();
            BigDecimal vatBase = item.getVatBase();
            BigDecimal vatValue = item.getVatValue();
            BigDecimal total = item.getTotal();

            InvoiceReportDTO.InvoiceReportVatItemDTO vatItem = vatItemDTOMap.get(vat);
            if (vatItem == null) {
                vatItem = new InvoiceReportDTO.InvoiceReportVatItemDTO();
                vatItem.setVatTypeId(VatRateType.REGULAR.equals(item.getVatRateType()) ? item.getVat().toString() : item.getVatRateType().toString());
                vatItem.setVatType(item.getVatRateType());
                vatItem.setCurrency(item.getCurrency());
                vatItem.setExchangeRate(item.getInvoice().getExchangeRate());
                vatItem.setVatBase(vatBase);
                vatItem.setVat(vat);
                vatItem.setVatValue(vatValue);
                vatItem.setTotal(total);
            } else {
                vatItem.setVatBase(vatItem.getVatBase().add(vatBase));
                vatItem.setVatValue(vatItem.getVatValue().add(vatValue));
                vatItem.setTotal(vatItem.getTotal().add(total));
            }
            vatItemDTOMap.put(item.getVat(), vatItem);
        });
        final List<InvoiceReportDTO.InvoiceReportVatItemDTO> items = new ArrayList<>(vatItemDTOMap.values());
        items.forEach(dto -> {
            dto.setVatBase(dto.getVatBase());
            dto.setVatValue(dto.getVatValue());
            dto.setTotal(dto.getTotal());
        });
        invoice.setItems(items);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadNavXmlArchive(List<Invoice> invoices) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        try {
            invoices.stream().filter(i -> InvoiceNavStatus.DONE.equals(i.getNavStatus())).forEach(invoice -> {
                String fileName = invoice.getInvoiceNumber().replace("/", "_") + "_" + invoice.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                try {
                    ZipEntry ze = new ZipEntry(fileName + ".xml");
                    zos.putNextEntry(ze);
                    //final byte[] content = navOnlineService.createInvoiceDataXml(invoice).getBytes(StandardCharsets.UTF_8);
                    //zos.write(content, 0, content.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } finally {
            try {
                zos.closeEntry();
                zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(List<InvoiceReportDTO> dtos) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put(INVOICE_NUMBER, 100);
        columns.put(STORNO_INVOICE_NUMBER, 100);
        columns.put(INVOICE_TYPE, 50);
        columns.put(NAME2, 200);
        columns.put(NAME1, 200);
        columns.put(OPTIONAL_NAME, 100);
        columns.put(TAX_NO, 100);
        columns.put(ZIPCODE, 100);
        columns.put(CITY, 100);
        columns.put(STREET, 100);
        columns.put(COUNTRY, 100);
        columns.put(CONGRESS_NAME, 100);
        columns.put(PROGRAM_NUMBER, 100);
        columns.put(DATE_OF_INVOICE, 100);
        columns.put(DATE_OF_FULFILMENT, 100);
        columns.put(METHOD_OF_PAYMENT, 100);
        columns.put(DATE_OF_PAYMENT, 100);
        columns.put(PAYMENT_DEADLINE, 100);

        Map<String, VatRateType> vatColumnMap = new LinkedHashMap<>();

        dtos.forEach(dto -> {
            dto.getItems().forEach(item -> {
                vatColumnMap.put(item.getVatTypeId(), item.getVatType());
            });
        });

        final List<Map.Entry<String, VatRateType>> vatColumnEntries = new ArrayList<>(vatColumnMap.entrySet());
        vatColumnEntries.sort((Map.Entry<String, VatRateType> entry1, Map.Entry<String, VatRateType> entry2) -> {
            if (entry1.getValue().equals(VatRateType.REGULAR) && entry2.getValue().equals(VatRateType.REGULAR)) {
                return Integer.valueOf(entry1.getKey()).compareTo(Integer.valueOf(entry2.getKey()));
            } else if (entry1.getValue().equals(VatRateType.REGULAR) && !entry2.getValue().equals(VatRateType.REGULAR)) {
                return -1;
            } else {
                return entry1.getKey().compareTo(entry2.getKey());
            }
        });

        final List<String> vatColumnIds = vatColumnEntries.stream().map(Map.Entry::getKey).collect(Collectors.toList());

        vatColumnIds.forEach(col -> {
            columns.put(col + (VatRateType.REGULAR.equals(vatColumnMap.get(col)) ? "% Netto HUF" : " HUF"), 100);
            if (VatRateType.REGULAR.equals(vatColumnMap.get(col))) {
                columns.put(col + "% VAT HUF", 100);
            }
        });
        columns.put("N. Total HUF", 100);
        columns.put("V. Total HUF", 100);

        columns.put(CURRENCY, 100);

        vatColumnIds.forEach(col -> {
            columns.put(col + (VatRateType.REGULAR.equals(vatColumnMap.get(col)) ? "% Netto" : ""), 100);
            if (VatRateType.REGULAR.equals(vatColumnMap.get(col))) {
                columns.put(col + "% VAT", 100);
            }
        });

        columns.put("N. Total", 100);
        columns.put("V. Total", 100);
        columns.put(EXCHANGE_RATE, 100);

        columns.put("NAV status", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Invoice report", null, "", getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (InvoiceReportDTO dto : dtos) {
            if (dto.getItems().isEmpty()) {
                continue;
            }
            String currency = dto.getItems().stream().findFirst().isPresent() ? dto.getItems().stream().findFirst().get().getCurrency() : null;
            BigDecimal exchangeRate = dto.getItems().stream().findFirst().isPresent() ? dto.getItems().stream().findFirst().get().getExchangeRate() : BigDecimal.ZERO;
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getInvoiceNumber());
            addCell(row, wrappingCellStyle, 1, dto.getStornoInvoiceNumber());
            addCell(row, wrappingCellStyle, 2, dto.getInvoiceReportType().toString());
            addCell(row, wrappingCellStyle, 3, dto.getName1());
            addCell(row, wrappingCellStyle, 4, dto.getName2());
            addCell(row, wrappingCellStyle, 5, dto.getOptionalName());
            addCell(row, wrappingCellStyle, 6, dto.getVatRegNumber());
            addCell(row, wrappingCellStyle, 7, dto.getZipCode());
            addCell(row, wrappingCellStyle, 8, dto.getCity());
            addCell(row, wrappingCellStyle, 9, dto.getStreet());
            addCell(row, wrappingCellStyle, 10, dto.getCountry());
            addCell(row, wrappingCellStyle, 11, dto.getCongressName());
            addCell(row, wrappingCellStyle, 12, dto.getProgramNumber());
            addCell(row, wrappingCellStyle, 13, dto.getCreatedDate());
            addCell(row, wrappingCellStyle, 14, dto.getDateOfFulfilment());
            addCell(row, wrappingCellStyle, 15, dto.getBillingMethod());
            addCell(row, wrappingCellStyle, 16, dto.getDateOfPayment());
            addCell(row, wrappingCellStyle, 17, dto.getPaymentDeadline());
            int idx = 18;
            // HUF
            for (String vatColumnId : vatColumnIds) {
                final Optional<InvoiceReportDTO.InvoiceReportVatItemDTO> itemOpt = dto.getItems().stream().filter(o -> o.getVatTypeId().equals(vatColumnId)).findFirst();
                if (itemOpt.isPresent()) {
                    InvoiceReportDTO.InvoiceReportVatItemDTO item = itemOpt.get();
                    BigDecimal vatBase = item.getVatBase();
                    BigDecimal vatValue = item.getVatValue();
                    if (!HUF.toString().equalsIgnoreCase(currency)) {
                        vatBase = roundUp(vatBase.multiply(exchangeRate));
                        vatValue = roundUp(vatValue.multiply(exchangeRate));
                    }

                    addCell(row, wrappingCellStyle, idx++, vatBase);
                    if (VatRateType.REGULAR.equals(item.getVatType())) {
                        addCell(row, wrappingCellStyle, idx++, vatValue);
                    }
                } else {
                    addCell(row, wrappingCellStyle, idx++, "");
                    if (VatRateType.REGULAR.equals(vatColumnMap.get(vatColumnId))) {
                        addCell(row, wrappingCellStyle, idx++, "");
                    }
                }
            }
            BigDecimal netTotal = dto.getItems().stream().map(InvoiceReportDTO.InvoiceReportVatItemDTO::getVatBase).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal vatTotal = dto.getItems().stream().map(InvoiceReportDTO.InvoiceReportVatItemDTO::getVatValue).reduce(BigDecimal.ZERO, BigDecimal::add);

            if (!HUF.toString().equalsIgnoreCase(currency)) {
                netTotal = roundUp(netTotal.multiply(exchangeRate));
                vatTotal = roundUp(vatTotal.multiply(exchangeRate));
            }
            addCell(row, wrappingCellStyle, idx++, netTotal.compareTo(BigDecimal.ZERO) != 0 ? netTotal : null);
            addCell(row, wrappingCellStyle, idx++, vatTotal.compareTo(BigDecimal.ZERO) != 0 ? vatTotal : null);

            addCell(row, wrappingCellStyle, idx++, !HUF.toString().equalsIgnoreCase(currency) ? currency : "");
            // Invoice currency
            for (String vatColumnId : vatColumnIds) {
                if (!HUF.toString().equalsIgnoreCase(currency)) {
                    final Optional<InvoiceReportDTO.InvoiceReportVatItemDTO> itemOpt = dto.getItems().stream().filter(o -> o.getVatTypeId().equals(vatColumnId)).findFirst();
                    if (itemOpt.isPresent()) {
                        InvoiceReportDTO.InvoiceReportVatItemDTO item = itemOpt.get();
                        addCell(row, wrappingCellStyle, idx++, item.getVatBase());
                        if (VatRateType.REGULAR.equals(vatColumnMap.get(vatColumnId))) {
                            addCell(row, wrappingCellStyle, idx++, item.getVatValue());
                        }
                    } else {
                        addCell(row, wrappingCellStyle, idx++, "");
                        if (VatRateType.REGULAR.equals(vatColumnMap.get(vatColumnId))) {
                            addCell(row, wrappingCellStyle, idx++, "");
                        }
                    }
                } else {
                    addCell(row, wrappingCellStyle, idx++, "");
                    if (VatRateType.REGULAR.equals(vatColumnMap.get(vatColumnId))) {
                        addCell(row, wrappingCellStyle, idx++, "");
                    }
                }
            }

            if (!HUF.toString().equalsIgnoreCase(currency)) {
                final BigDecimal vatBase = dto.getItems().stream().map(InvoiceReportDTO.InvoiceReportVatItemDTO::getVatBase).reduce(BigDecimal.ZERO, BigDecimal::add);
                addCell(row, wrappingCellStyle, idx++, vatBase.compareTo(BigDecimal.ZERO) != 0 ? vatBase : null);
                final BigDecimal vatValue = dto.getItems().stream().map(InvoiceReportDTO.InvoiceReportVatItemDTO::getVatValue).reduce(BigDecimal.ZERO, BigDecimal::add);
                addCell(row, wrappingCellStyle, idx++, vatValue.compareTo(BigDecimal.ZERO) != 0 ? vatValue : null);
            } else {
                addCell(row, wrappingCellStyle, idx++, "");
                addCell(row, wrappingCellStyle, idx++, "");
            }

            addCell(row, wrappingCellStyle, idx++, dto.getExchangeRate());
            addCell(row, wrappingCellStyle, idx, dto.getNavStatus() != null ? dto.getNavStatus().toString() : "");
            rowIndex++;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the invoice report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    @Transactional(readOnly = true)
    public byte[] downloadAccountantReportXls(List<InvoiceReportDTO> dtos) throws IOException {
        final Map<Long, Set<InvoiceItem>> invoiceItemMap = new HashMap<>();
        List<InvoiceItem> invoiceItemList = invoiceItemRepository.findAllByInvoiceIdIn(dtos.stream().map(InvoiceReportDTO::getId).collect(Collectors.toList()));
        invoiceItemList.forEach(item -> {
            Set<InvoiceItem> invoiceItems = invoiceItemMap.computeIfAbsent(item.getInvoice().getId(), k -> new HashSet<>());
            invoiceItems.add(item);
        });

        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put(INVOICE_NUMBER, 100);
        columns.put(STORNO_INVOICE_NUMBER, 100);
        columns.put(INVOICE_TYPE, 50);
        columns.put(NAME2, 200);
        columns.put(NAME1, 200);
        columns.put(OPTIONAL_NAME, 100);
        columns.put(TAX_NO, 100);
        columns.put(ZIPCODE, 100);
        columns.put(CITY, 100);
        columns.put(STREET, 100);
        columns.put(COUNTRY, 100);
        columns.put(CONGRESS_NAME, 100);
        columns.put(PROGRAM_NUMBER, 100);
        columns.put(DATE_OF_INVOICE, 100);
        columns.put(DATE_OF_FULFILMENT, 100);
        columns.put(METHOD_OF_PAYMENT, 100);
        columns.put(DATE_OF_PAYMENT, 100);
        columns.put(PAYMENT_DEADLINE, 100);

        columns.put("Item name", 100);
        columns.put("SZ.J.", 100);
        columns.put("Vat base", 100);
        columns.put("Vat", 100);
        columns.put("Vat value", 100);
        columns.put("Vat exception", 100);
        columns.put(CURRENCY, 100);
        columns.put(EXCHANGE_RATE, 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Invoice accountant report", null, "", getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (InvoiceReportDTO dto : dtos) {
            final Set<InvoiceItem> items = invoiceItemMap.get(dto.getId());
            if (items.isEmpty()) {
                continue;
            }

            for (InvoiceItem item : items) {
                final XSSFRow row = sheet.createRow(rowIndex);
                addCell(row, wrappingCellStyle, 0, dto.getInvoiceNumber());
                addCell(row, wrappingCellStyle, 1, dto.getStornoInvoiceNumber());
                addCell(row, wrappingCellStyle, 2, dto.getInvoiceReportType().toString());
                addCell(row, wrappingCellStyle, 3, dto.getName1());
                addCell(row, wrappingCellStyle, 4, dto.getName2());
                addCell(row, wrappingCellStyle, 5, dto.getOptionalName());
                addCell(row, wrappingCellStyle, 6, dto.getVatRegNumber());
                addCell(row, wrappingCellStyle, 7, dto.getZipCode());
                addCell(row, wrappingCellStyle, 8, dto.getCity());
                addCell(row, wrappingCellStyle, 9, dto.getStreet());
                addCell(row, wrappingCellStyle, 10, dto.getCountry());
                addCell(row, wrappingCellStyle, 11, dto.getCongressName());
                addCell(row, wrappingCellStyle, 12, dto.getProgramNumber());
                addCell(row, wrappingCellStyle, 13, dto.getCreatedDate());
                addCell(row, wrappingCellStyle, 14, dto.getDateOfFulfilment());
                addCell(row, wrappingCellStyle, 15, dto.getBillingMethod());
                addCell(row, wrappingCellStyle, 16, dto.getDateOfPayment());
                addCell(row, wrappingCellStyle, 17, dto.getPaymentDeadline());

                addCell(row, wrappingCellStyle, 18, item.getItemName());
                addCell(row, wrappingCellStyle, 19, item.getSzj());
                addCell(row, wrappingCellStyle, 20, item.getVatBase());
                addCell(row, wrappingCellStyle, 21, item.getVat());
                addCell(row, wrappingCellStyle, 22, item.getVatValue());
                addCell(row, wrappingCellStyle, 23, item.getVatExceptionReason());
                addCell(row, wrappingCellStyle, 24, item.getCurrency());
                addCell(row, wrappingCellStyle, 25, dto.getExchangeRate());
                rowIndex++;
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the invoice accountant report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public byte[] createPdfByInvoiceId(Long invoiceId) {
        final InvoiceRegistration invoiceRegistration = invoiceRegistrationRepository.findByInvoiceId(invoiceId);
        if (invoiceRegistration != null) {
            return invoicePdfService.generatePdf(invoicePdfService.createInvoicePdfContext(invoiceRegistration));
        }

        final InvoicePayingGroup invoicePayingGroup = invoicePayingGroupRepository.findByInvoiceId(invoiceId);
        if (invoicePayingGroup != null) {
            return payingGroupPdfService.generatePdf(payingGroupPdfService.createInvoicePdfContext(invoicePayingGroup));
        }

        final InvoiceCongress invoiceCongress = invoiceCongressRepository.findByInvoiceId(invoiceId);
        if (invoiceCongress != null) {
            return miscInvoicePdfService.generatePdf(miscInvoicePdfService.createInvoicePdfContext(invoiceCongress));
        }

        throw new IllegalArgumentException("Invoice report pdf download failed! No invoice has been found for id:" + invoiceId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<InvoiceNavValidation> getAllInvoiceNavValidationById(Long invoiceId) {
        return invoiceNavValidationRepository.findByInvoiceId(invoiceId);
    }
}
