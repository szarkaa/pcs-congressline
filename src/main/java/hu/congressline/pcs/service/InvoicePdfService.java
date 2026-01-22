package hu.congressline.pcs.service;

import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.domain.InvoiceCharge;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.Rate;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.Language;
import hu.congressline.pcs.domain.enumeration.VatRateType;
import hu.congressline.pcs.repository.InvoiceChargeRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.RateRepository;
import hu.congressline.pcs.service.pdf.InvoiceHeaderFooter;
import hu.congressline.pcs.service.pdf.InvoicePdfContext;
import hu.congressline.pcs.service.pdf.PcsPdfFont;
import hu.congressline.pcs.service.pdf.PdfContext;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.domain.enumeration.Currency.HUF;

@Slf4j
@Service
public class InvoicePdfService extends AbstractPdfService {
    private static final String MESSAGE_KEY_PREFIX = "invoice.pdf.invoice";

    private final CompanyService companyService;
    private final CurrencyService currencyService;
    private final RateRepository rateRepository;

    public InvoicePdfService(InvoiceItemRepository invoiceItemRepository, InvoiceChargeRepository invoiceChargeRepository, DiscountService discountService,
                             CompanyService companyService, CurrencyService currencyService, RateRepository rateRepository, MessageSource messageSource) {
        super(invoiceItemRepository, invoiceChargeRepository, discountService, messageSource);
        this.companyService = companyService;
        this.currencyService = currencyService;
        this.rateRepository = rateRepository;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public InvoicePdfContext createInvoicePdfContext(InvoiceRegistration invoiceRegistration) {
        List<InvoiceItem> invoiceItemList = invoiceItemRepository.findAllByInvoice(invoiceRegistration.getInvoice());
        List<InvoiceCharge> invoiceChargeList = invoiceChargeRepository.findAllByInvoice(invoiceRegistration.getInvoice());
        return new InvoicePdfContext(invoiceRegistration, invoiceItemList, invoiceChargeList);
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    @Override
    public byte[] generatePdf(PdfContext context) {
        InvoicePdfContext pdfContext = (InvoicePdfContext) context;
        Locale locale = pdfContext.getLocale();
        pdfContext.setCompany(companyService.getCompanyProfile());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 20, 20, 40, 120);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            writer.setPageEvent(new InvoiceHeaderFooter(messageSource, pdfContext));
            writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

            document.open();
            addMetaData(document, pdfContext);
            generateContent(document, pdfContext);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error while creating invoice pdf", e);
        }
        return null;
    }

    public boolean hasValidRate(String currency) {
        final List<Rate> rates = rateRepository.getRates(currency);
        return !rates.isEmpty();
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private void addMetaData(Document document, InvoicePdfContext pdfContext) {
        document.addTitle(getMessage(MESSAGE_KEY_PREFIX, pdfContext.getLocale()));
        document.addSubject(getMessage(MESSAGE_KEY_PREFIX, pdfContext.getLocale()));
        document.addKeywords("");
        final String pcsSystem = "PCS System";
        document.addAuthor(pcsSystem);
        document.addCreator(pcsSystem);
    }

    @SuppressWarnings("MethodLength")
    private void generateContent(Document document, InvoicePdfContext pdfContext) throws DocumentException {
        Locale locale = pdfContext.getLocale();
        Paragraph preface = new Paragraph();
        final String colon = ": ";
        final String congressName = getMessage("invoice.pdf.event", locale) + colon + pdfContext.getRegistration().getCongress().getName();
        PdfPTable table = createTable(1, 100, new float[]{1});
        PdfPCell cell1 = createCell(new Paragraph(congressName, PcsPdfFont.P_BOLD));
        cell1.setBorder(1);
        addTableCell(table, cell1);
        preface.add(table);

        //new row
        final String congressDate = getMessage("invoice.pdf.date", locale) + colon
                + pdfContext.getStartDate().format(pdfContext.getFormatter()) + " - "
                + pdfContext.getEndDate().format(pdfContext.getFormatter());
        final String progNumber = getMessage("invoice.pdf.workNo", locale) + colon + pdfContext.getRegistration().getCongress().getProgramNumber();

        table = createTable(2, 100, new float[]{1, 1});
        table.setSpacingAfter(5);
        cell1 = createCell(createParagraph(congressDate, PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell2 = createCell(createRightParagraph(progNumber, PcsPdfFont.P_SMALL_NORMAL));

        addTableCell(table, cell1, cell2);

        final String invoiceNumber = getMessage("invoice.pdf.invoiceNumber", locale) + colon + pdfContext.getInvoiceNumber();
        final String paymentMethod = getMessage("invoice.pdf.payment", locale) + colon + getMessage("invoice.pdf.payment." + pdfContext.getBillingMethod(),
                locale);
        cell1 = createCell(createParagraph(invoiceNumber, PcsPdfFont.P_SMALL_BOLD));
        cell2 = createCell(createRightParagraph(paymentMethod, PcsPdfFont.P_SMALL_BOLD));
        addTableCell(table, cell1, cell2);

        if (pdfContext.getStorno()) {
            final String invoiceStornoNumber = getMessage("invoice.pdf.invoiceStornoNumber", locale) + colon + pdfContext.getStornoInvoiceNumber();
            cell1 = createCell(createParagraph(invoiceStornoNumber, PcsPdfFont.P_SMALL_BOLD));
            cell1.setColspan(2);
            addTableCell(table, cell1);
        }

        table.setSpacingAfter(10);
        preface.add(table);

        //new row
        table = createTable(4, 100, new float[] {1, 1, 1, 1});
        final String issued = getMessage("invoice.pdf.issued", locale) + colon + pdfContext.getCreatedDate().format(pdfContext.getFormatter());
        final String dateOfFulfilment = getMessage("invoice.pdf.dateOfFulfilment", locale) + colon + pdfContext.getDateOfFulfilment().format(pdfContext.getFormatter());
        final String paymentDeadlineDate = getMessage("invoice.pdf.deadLine", locale) + colon + pdfContext.getPaymentDeadlineDate().format(pdfContext.getFormatter());
        final String regNumber = getMessage("invoice.pdf.regNumber", locale) + colon + pdfContext.getRegistration().getRegId().toString();
        cell1 = createCell(createParagraph(issued, PcsPdfFont.P_SMALL_NORMAL));
        cell2 = createCell(createCenterParagraph(dateOfFulfilment, PcsPdfFont.P_SMALL_NORMAL, locale));
        PdfPCell cell3 = createCell(createCenterParagraph(paymentDeadlineDate, PcsPdfFont.P_SMALL_NORMAL, locale));
        PdfPCell cell4 = createCell(createRightParagraph(regNumber, PcsPdfFont.P_SMALL_NORMAL));

        setBorder(1, cell1, cell2, cell3, cell4);
        addTableCell(table, cell1, cell2, cell3, cell4);

        preface.add(table);

        PdfPTable itemDetailsTable = createTable(7, 100, new float[]{5, 0.3f, 1, 1, 0.7f, 1, 1});
        itemDetailsTable.setSplitRows(true);
        itemDetailsTable.setSpacingBefore(7);

        //new row
        cell1 = createCell(createParagraphWithMessage("invoice.pdf.service", PcsPdfFont.P_SMALL_BOLD, locale));
        cell2 = createCell(createRightParagraphWithMessage("invoice.pdf.unit", PcsPdfFont.P_SMALL_NORMAL, locale));
        cell3 = createCell(createRightParagraphWithMessage("invoice.pdf.unitPrice", PcsPdfFont.P_SMALL_NORMAL, locale));
        cell4 = createCell(createRightParagraphWithMessage("invoice.pdf.vatBase", PcsPdfFont.P_SMALL_NORMAL, locale));
        final String invoicePdfVat = "invoice.pdf.vat";
        PdfPCell cell5 = createCell(createRightParagraphWithMessage(invoicePdfVat, PcsPdfFont.P_SMALL_NORMAL, locale));
        PdfPCell cell6 = createCell(createRightParagraphWithMessage("invoice.pdf.vatValue", PcsPdfFont.P_SMALL_NORMAL, locale));
        final String invoicePdfSum = "invoice.pdf.sum";
        PdfPCell cell7 = createCell(createRightParagraphWithMessage(invoicePdfSum, PcsPdfFont.P_SMALL_NORMAL, locale));

        setBorder(1, cell1, cell2, cell3, cell4, cell5, cell6, cell7);
        setPaddingBottom(3, cell1, cell2, cell3, cell4, cell5, cell6, cell7);

        addTableCell(itemDetailsTable, cell1, cell2, cell3, cell4, cell5, cell6, cell7);

        // Registrations
        Map<String, List<InvoiceItem>> registrationTypes = getSortedChargeableItemRows(ChargeableItemType.REGISTRATION, pdfContext.getInvoiceItemList());

        //new row
        final String invoicePdfPax = "invoice.pdf.pax";
        final String openParenthesis = " (";
        registrationTypes.keySet().stream()
                .filter(key -> containsNonPrePayedItem(registrationTypes.get(key)))
                .forEach(key -> {
                    //new row
                    String itemKeyHeader = getMessage("invoice.pdf.registrationFee", locale) + (!key.isEmpty() ? openParenthesis + key + ")" : "");
                    PdfPCell cell = createCell(createParagraph(itemKeyHeader, PcsPdfFont.P_SMALL_BOLD));
                    cell.setColspan(7);
                    addTableCell(itemDetailsTable, cell);

                    registrationTypes.get(key).forEach(item -> {
                        String unit = item.getUnit() != null ? item.getUnit() + " " + getMessage(invoicePdfPax, locale) : "";
                        PdfPCell c1 = createCell(createParagraph(item.getItemName(), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c2 = createCell(createRightParagraph(unit, PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getUnitPrice(), item.getCurrency()), pdfContext.getLocale()),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatBase(), item.getCurrency()), pdfContext.getLocale()),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c5 = createCell(createRightParagraph(VatRateType.REGULAR.equals(item.getVatRateType()) ? item.getVat() + "%" : item.getVatRateType().toString(),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatValue(), item.getCurrency()), pdfContext.getLocale()),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c7 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getTotal(), item.getCurrency()), pdfContext.getLocale())
                                + " " + item.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));

                        addTableCell(itemDetailsTable, c1, c2, c3, c4, c5, c6, c7);
                    });
                });

        // Room reservations
        Map<String, List<InvoiceItem>> roomReservations = getSortedChargeableItemRows(ChargeableItemType.HOTEL, pdfContext.getInvoiceItemList());
        roomReservations.keySet().stream()
                .filter(key -> containsNonPrePayedItem(roomReservations.get(key)))
                .forEach(key -> {
                    //new row
                    String itemKeyHeader = getMessage("invoice.pdf.accomodation", locale) + (!key.isEmpty() ? openParenthesis + key + ")" : "");
                    PdfPCell cell = createCell(createParagraph(itemKeyHeader, PcsPdfFont.P_SMALL_BOLD));
                    cell.setColspan(7);
                    addTableCell(itemDetailsTable, cell);

                    roomReservations.get(key).forEach(item -> {
                        //new row
                        PdfPCell c1 = createCell(createParagraph(item.getItemName().replace(")", ")\n"), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c2 = createCell(createRightParagraph(item.getUnit() + " " + getMessage("invoice.pdf.night", locale), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getUnitPrice(), item.getCurrency()),
                                pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatBase(), item.getCurrency()),
                                pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c5 = createCell(createRightParagraph(VatRateType.REGULAR.equals(item.getVatRateType()) ? item.getVat() + "%" : item.getVatRateType().toString(),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatValue(), item.getCurrency()), pdfContext.getLocale()),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c7 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getTotal(), item.getCurrency()),
                                pdfContext.getLocale()) + " " + item.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));

                        addTableCell(itemDetailsTable, c1, c2, c3, c4, c5, c6, c7);
                    });
                });

        // Optional services
        Map<String, List<InvoiceItem>> optionalServices = getSortedChargeableItemRows(ChargeableItemType.OPTIONAL_SERVICE, pdfContext.getInvoiceItemList());
        //new row
        optionalServices.keySet().stream()
                .filter(key -> containsNonPrePayedItem(optionalServices.get(key)))
                .forEach(key -> {
                    //new row
                    String itemKeyHeader = getMessage("invoice.pdf.programs", locale) + (!key.isEmpty() ? openParenthesis + key + ")" : "");
                    PdfPCell cell = createCell(createParagraph(itemKeyHeader, PcsPdfFont.P_SMALL_BOLD));
                    cell.setColspan(7);
                    addTableCell(itemDetailsTable, cell);

                    optionalServices.get(key).forEach(item -> {
                        //new row
                        PdfPCell c1 = createCell(createParagraph(item.getItemName(), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c2 = createCell(createRightParagraph(item.getUnit() + " " + getMessage(invoicePdfPax, locale), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getUnitPrice(), item.getCurrency()),
                                pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatBase(), item.getCurrency()),
                                pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c5 = createCell(createRightParagraph(VatRateType.REGULAR.equals(item.getVatRateType()) ? item.getVat() + "%" : item.getVatRateType().toString(),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatValue(), item.getCurrency()), pdfContext.getLocale()),
                                PcsPdfFont.P_MINIATURE_NORMAL));
                        PdfPCell c7 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getTotal(), item.getCurrency()),
                                pdfContext.getLocale()) + " " + item.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                        addTableCell(itemDetailsTable, c1, c2, c3, c4, c5, c6, c7);
                    });
                });
        preface.add(itemDetailsTable);

        // charged services
        if (!pdfContext.getInvoiceChargeList().isEmpty()) {
            PdfPTable invoiceChargeTable = createTable(2, 100, new float[]{2, 1});
            invoiceChargeTable.setSpacingBefore(10);

            List<InvoiceItem> list = new ArrayList<>();
            registrationTypes.values().forEach(list::addAll);
            roomReservations.values().forEach(list::addAll);
            optionalServices.values().forEach(list::addAll);
            final String totalWithCurrency = formatter.format(sumPricesWithDiscounts(list), pdfContext.getLocale()) + " " + pdfContext.getCurrency();

            cell1 = createCell(createParagraphWithMessage("invoice.pdf.servicesTotal", PcsPdfFont.P_SMALL_BOLD, locale));
            cell2 = createCell(createRightParagraph(totalWithCurrency, PcsPdfFont.P_SMALL_NORMAL));
            setBorder(1, cell1, cell2);
            addTableCell(invoiceChargeTable, cell1, cell2);

            //new row
            cell1 = createCell(new Paragraph(getMessage("invoice.pdf.payments", locale) + colon, PcsPdfFont.P_SMALL_BOLD));
            cell1.setColspan(2);
            addTableCell(invoiceChargeTable, cell1);

            pdfContext.getInvoiceChargeList().sort(getInvoiceChargeComparator());

            pdfContext.getInvoiceChargeList().forEach(charge -> {
                if (!charge.getAmount().equals(BigDecimal.ZERO)) {
                    PdfPCell c1 = createCell(createParagraph(charge.getItemName(), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c2 = createCell(createRightParagraph(formatter.format(charge.getAmount(), pdfContext.getLocale()) + " " + charge.getCurrency(),
                            PcsPdfFont.P_MINIATURE_NORMAL));
                    addTableCell(invoiceChargeTable, c1, c2);
                }
            });

            preface.add(invoiceChargeTable);
        }

        //new row
        List<InvoiceItem> list = new ArrayList<>();
        registrationTypes.values().forEach(list::addAll);
        roomReservations.values().forEach(list::addAll);
        optionalServices.values().forEach(list::addAll);
        BigDecimal sumPricesWithDiscounts = sumPricesWithDiscounts(list)
                .add(getChargedServicesSumAmount(pdfContext.getInvoiceChargeList()));

        //Fizetendő blokk
        table = createTable(2, 100, new float[]{1, 1});
        table.setSpacingBefore(7);

        cell1 = createCell(createParagraphWithMessage("invoice.pdf.payable", PcsPdfFont.P_SMALL_BOLD, locale));
        cell2 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(sumPricesWithDiscounts, pdfContext.getCurrency()), pdfContext.getLocale()) + " "
                + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));
        cell1.setBorder(1);
        cell2.setBorder(1);
        addTableCell(table, cell1, cell2);

        if (!HUF.toString().equalsIgnoreCase(pdfContext.getCurrency())) {
            //new row
            final String convertedCurrency = formatter.format(currencyService.convertCurrencyToHuf(sumPricesWithDiscounts, pdfContext.getCurrencyExchangeRate()),
                    pdfContext.getLocale());
            cell1 = createCell(createParagraph(" ", PcsPdfFont.P_SMALL_BOLD));
            cell2 = createCell(createRightParagraph(convertedCurrency + " " + HUF.toString(), PcsPdfFont.P_SMALL_NORMAL));
            addTableCell(table, cell1, cell2);

            //new row
            final String exchangeRateCalc = getMessage("invoice.pdf.exchangeRate", locale) + ": 1 " + pdfContext.getCurrency() + " = "
                    + formatter.format(pdfContext.getCurrencyExchangeRate(), pdfContext.getLocale()) + " " + HUF.toString();
            cell1 = createCell(createRightParagraph(exchangeRateCalc, PcsPdfFont.P_MINIATURE_NORMAL));
            cell1.setColspan(2);

            addTableCell(table, cell1);
        }
        preface.add(table);

        // Optional text
        if (StringUtils.hasText(pdfContext.getOptionalText())) {
            table = createTable(1, 100, new float[]{1});
            table.setSpacingBefore(4);
            cell1 = createCell(new Paragraph(pdfContext.getOptionalText(), PcsPdfFont.P_MINIATURE_BOLD));
            cell1.setBorder(1);
            addTableCell(table, cell1);
            preface.add(table);
        }

        // Summary table
        // Table summarizing the item prices by VAT
        Map<String, ServiceUtil.ItemRowByVat> itemRowsByVat = ServiceUtil.getItemRowsByVat(pdfContext.getInvoiceItemList());
        boolean hasVatException = itemRowsByVat.values().stream().anyMatch(itemRowByVat -> itemRowByVat.getVat().equals(0));
        if (hasVatException) {
            table = createTable(4, 70, new float[]{4, 1, 1, 1});
        } else {
            table = createTable(4, 50, new float[]{1, 1, 1, 1});
        }

        table.setHorizontalAlignment(PdfPTable.ALIGN_RIGHT);
        table.setSpacingBefore(30);
        table.setSpacingAfter(10);

        //new row
        cell1 = createCell(createParagraph(" ", PcsPdfFont.P_SMALL_NORMAL));
        cell2 = createCell(createRightParagraphWithMessage("invoice.pdf.netto", PcsPdfFont.P_SMALL_NORMAL, locale));
        cell3 = createCell(createRightParagraphWithMessage("invoice.pdf.tax", PcsPdfFont.P_SMALL_NORMAL, locale));
        cell4 = createCell(createRightParagraphWithMessage(invoicePdfSum, PcsPdfFont.P_SMALL_NORMAL, locale));

        addTableCell(table, cell1, cell2, cell3, cell4);
        /*

        Iterator<String> iterator = itemRowsByVat.keySet().iterator();
        if (iterator.hasNext()) {
            while (iterator.hasNext()) {
                String key = iterator.next();
                String itemName;
                if (itemRowsByVat.get(key).getVat().equals(0)) {
                    itemName = key;
                } else {
                    itemName = key + "% " + getMessage(invoicePdfVat, locale);
                }
                String vatBase = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getVatBase(), pdfContext.getCurrency()), pdfContext.getLocale())
                        + " " + pdfContext.getCurrency();
                String vatValue = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getVatValue(), pdfContext.getCurrency()), pdfContext.getLocale())
                        + " " + pdfContext.getCurrency();
                String total = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getTotal(), pdfContext.getCurrency()), pdfContext.getLocale())
                        + " " + pdfContext.getCurrency();
                cell1 = createCell(createParagraph(itemName, PcsPdfFont.P_SMALL_NORMAL));
                cell2 = createCell(createRightParagraph(vatBase, PcsPdfFont.P_SMALL_NORMAL));
                cell3 = createCell(createRightParagraph(vatValue, PcsPdfFont.P_SMALL_NORMAL));
                cell4 = createCell(createRightParagraph(total, PcsPdfFont.P_SMALL_NORMAL));

                if (HUF.toString().equalsIgnoreCase(pdfContext.getCurrency())) {
                    setPaddingBottom(5, cell1, cell2, cell3, cell4);
                }

                addTableCell(table, cell1, cell2, cell3, cell4);

                if (!HUF.toString().equalsIgnoreCase(pdfContext.getCurrency())) {
                    vatBase = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getVatBase().multiply(pdfContext.getCurrencyExchangeRate())
                            .setScale(0, RoundingMode.HALF_UP), HUF.toString()), pdfContext.getLocale()) + " " + HUF.toString();
                    vatValue = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getVatValue().multiply(pdfContext.getCurrencyExchangeRate())
                            .setScale(0, RoundingMode.HALF_UP), HUF.toString()), pdfContext.getLocale()) + " " + HUF.toString();
                    total = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getTotal().multiply(pdfContext.getCurrencyExchangeRate())
                            .setScale(0, RoundingMode.HALF_UP), HUF.toString()), pdfContext.getLocale()) + " " + HUF.toString();
                    cell1 = createCell(createParagraph("", PcsPdfFont.P_SMALL_NORMAL));
                    cell2 = createCell(createRightParagraph(vatBase, PcsPdfFont.P_SMALL_NORMAL));
                    cell3 = createCell(createRightParagraph(vatValue, PcsPdfFont.P_SMALL_NORMAL));
                    cell4 = createCell(createRightParagraph(total, PcsPdfFont.P_SMALL_NORMAL));

                    setPaddingBottom(5, cell1, cell2, cell3, cell4);
                    addTableCell(table, cell1, cell2, cell3, cell4);
                }
            }
        } else {
            cell1 = createCell(createParagraph(" ", PcsPdfFont.P_SMALL_NORMAL));
            cell1.setColspan(4);
            addTableCell(table, cell1);
        }

        //new row
        BigDecimal totalVatBase = BigDecimal.ZERO;
        for (String key : itemRowsByVat.keySet()) {
            totalVatBase = totalVatBase.add(itemRowsByVat.get(key).getVatBase());
        }
        BigDecimal totalVatValue = BigDecimal.ZERO;
        for (String key : itemRowsByVat.keySet()) {
            totalVatValue = totalVatValue.add(itemRowsByVat.get(key).getVatValue());
        }
        BigDecimal grandTotal = BigDecimal.ZERO;
        for (String key : itemRowsByVat.keySet()) {
            grandTotal = grandTotal.add(itemRowsByVat.get(key).getTotal());
        }

        cell1 = createCell(createParagraphWithMessage(invoicePdfSum, PcsPdfFont.P_SMALL_BOLD, locale));
        cell3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(totalVatValue, pdfContext.getCurrency()), pdfContext.getLocale())
                + " " + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));
        cell2 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(totalVatBase, pdfContext.getCurrency()), pdfContext.getLocale())
                + " " + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));
        cell4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(grandTotal, pdfContext.getCurrency()), pdfContext.getLocale())
                + " " + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));

        cell1.setBorder(1);
        cell2.setBorder(1);
        cell3.setBorder(1);
        cell4.setBorder(1);

        addTableCell(table, cell1, cell2, cell3, cell4);

        if (!HUF.toString().equalsIgnoreCase(pdfContext.getCurrency())) {
            final String totalVatValueWithCurrency = formatter.format(formatter.formatByCurrency(totalVatBase.multiply(pdfContext.getCurrencyExchangeRate()), HUF.toString()),
                    pdfContext.getLocale()) + " " + HUF.toString();
            final String totalVatBaseWithCurrency = formatter.format(formatter.formatByCurrency(totalVatValue.multiply(pdfContext.getCurrencyExchangeRate()), HUF.toString()),
                    pdfContext.getLocale()) + " " + HUF.toString();
            final String grandTotalWithCurrency = formatter.format(formatter.formatByCurrency(grandTotal.multiply(pdfContext.getCurrencyExchangeRate()), HUF.toString()),
                    pdfContext.getLocale()) + " " + HUF.toString();
            cell1 = createCell(createParagraph("", PcsPdfFont.P_SMALL_BOLD));
            cell2 = createCell(createRightParagraph(totalVatValueWithCurrency, PcsPdfFont.P_SMALL_BOLD));
            cell3 = createCell(createRightParagraph(totalVatBaseWithCurrency, PcsPdfFont.P_SMALL_BOLD));
            cell4 = createCell(createRightParagraph(grandTotalWithCurrency, PcsPdfFont.P_SMALL_BOLD));

            addTableCell(table, cell1, cell2, cell3, cell4);
        }
        */
        preface.add(table);

        String additionalBillingText = pdfContext.getLocale().equals(Locale.forLanguageTag(Language.HU.toString().toLowerCase()))
                ? pdfContext.getRegistration().getCongress().getAdditionalBillingTextHu() : pdfContext.getRegistration().getCongress().getAdditionalBillingTextEn();
        if (StringUtils.hasText(additionalBillingText)) {
            Paragraph tempParagraph = new Paragraph();
            tempParagraph.setLeading(7);
            tempParagraph.setSpacingAfter(-6);
            tempParagraph.add(new Chunk(additionalBillingText, PcsPdfFont.P_MINIATURE_NORMAL));
            tempParagraph.setAlignment(Element.ALIGN_CENTER);
            preface.add(tempParagraph);
        }

        //adding the whole preface to the document
        document.add(preface);
    }

    private Comparator<InvoiceCharge> getInvoiceChargeComparator() {
        return (InvoiceCharge o1, InvoiceCharge o2) -> {
            if (o1.getItemType().equals(ChargeableItemType.REGISTRATION)) {
                return -1;
            } else if (o1.getItemType().equals(ChargeableItemType.OPTIONAL_SERVICE)) {
                return 1;
            } else if (o1.getItemType().equals(ChargeableItemType.HOTEL) && o2.getItemType().equals(ChargeableItemType.REGISTRATION)) {
                return 1;
            } else if (o1.getItemType().equals(ChargeableItemType.HOTEL) && o2.getItemType().equals(ChargeableItemType.OPTIONAL_SERVICE)) {
                return -1;
            } else {
                return 0;
            }
        };
    }

    public BigDecimal getChargedServicesSumAmount(List<InvoiceCharge> list) {
        return list.stream().map(InvoiceCharge::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
