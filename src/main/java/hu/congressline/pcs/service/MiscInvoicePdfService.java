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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoiceItem;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.domain.enumeration.VatRateType;
import hu.congressline.pcs.repository.InvoiceChargeRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.service.pdf.InvoiceHeaderFooter;
import hu.congressline.pcs.service.pdf.MiscInvoicePdfContext;
import hu.congressline.pcs.service.pdf.PcsPdfFont;
import hu.congressline.pcs.service.pdf.PdfContext;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.domain.enumeration.Currency.HUF;

@Slf4j
@Service
public class MiscInvoicePdfService extends AbstractPdfService {

    private final CompanyService companyService;
    private final CurrencyService currencyService;
    private final MiscInvoiceService miscInvoiceService;

    public MiscInvoicePdfService(InvoiceItemRepository invoiceItemRepository, InvoiceChargeRepository invoiceChargeRepository, DiscountService discountService,
                                 CompanyService companyService, CurrencyService currencyService, MiscInvoiceService miscInvoiceService, MessageSource messageSource) {
        super(invoiceItemRepository, invoiceChargeRepository, discountService, messageSource);
        this.companyService = companyService;
        this.currencyService = currencyService;
        this.miscInvoiceService = miscInvoiceService;
    }

    public MiscInvoicePdfContext createInvoicePdfContext(InvoiceCongress invoiceCongress) {
        return new MiscInvoicePdfContext(invoiceCongress, miscInvoiceService.findItems(invoiceCongress.getId()));
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    @Override
    public byte[] generatePdf(PdfContext context) {
        MiscInvoicePdfContext pdfContext = (MiscInvoicePdfContext) context;
        pdfContext.setCompany(companyService.getCompanyProfile());
        //todo change the whole logic only registration and ignoredChargeableItemIdList should come from frontend

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            //A4 size with predefined margins (bottom is 100, because the footer section will take that place
            //Top header section is generated on the fly, not after the pdf creation, like the footer)
            Document document = new Document(PageSize.A4, 20, 20, 40, 120);
            //set a writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //set the listener for events like (new page, end page, open document, close document etc.)
            InvoiceHeaderFooter event = new InvoiceHeaderFooter(messageSource, context);
            writer.setPageEvent(event);
            writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));  //this box contains the footer's page of pages section

            document.open();
            addMetaData(document, pdfContext);
            generateContent(document, pdfContext);

            /*
            document.newPage();
            document.setPageCount(1);
            generateContent(document, pdfContext);

            document.newPage();
            document.setPageCount(1);
            generateContent(document, pdfContext);
            */

            document.close();
            writer.flush();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error while creating misc invoice pdf", e);
        }
        return null;
    }

    // iText allows to add metadata to the PDF which can be viewed in your Adobe
    // Reader
    // under File -> Properties
    private void addMetaData(Document document, MiscInvoicePdfContext pdfContext) {
        final String messageKeyPrefix = "invoice.pdf.invoice";
        document.addTitle(getMessage(messageKeyPrefix, pdfContext.getLocale()));
        document.addSubject(getMessage(messageKeyPrefix, pdfContext.getLocale()));
        document.addKeywords("");
        final String pcsSystem = "PCS System";
        document.addAuthor(pcsSystem);
        document.addCreator(pcsSystem);
    }

    @SuppressWarnings("MethodLength")
    private void generateContent(Document document, MiscInvoicePdfContext pdfContext) throws DocumentException {
        Locale locale = pdfContext.getLocale();
        Paragraph preface = new Paragraph();
        final String colon = ": ";
        final String congressName = getMessage("miscInvoice.pdf.event", locale) + colon + pdfContext.getCongress().getName();
        PdfPTable table = createTable(1, 100, new float[]{1});
        PdfPCell cell1 = createCell(new Paragraph(congressName, PcsPdfFont.P_BOLD));
        cell1.setBorder(1);
        addTableCell(table, cell1);
        preface.add(table);

        //new row
        final String congressDate = getMessage("invoice.pdf.date", locale) + colon + pdfContext.getStartDate().format(pdfContext.getFormatter()) + " - "
                + pdfContext.getEndDate().format(pdfContext.getFormatter());
        final String progNumber = getMessage("invoice.pdf.workNo", locale) + colon + pdfContext.getCongress().getProgramNumber();

        table = createTable(2, 100, new float[]{1, 1});
        table.setSpacingAfter(5);
        cell1 = createCell(createParagraph(congressDate, PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell2 = createCell(createRightParagraph(progNumber, PcsPdfFont.P_SMALL_NORMAL));

        addTableCell(table, cell1, cell2);

        //new row
        final String invoiceNumber = getMessage("invoice.pdf." + (InvoiceType.PRO_FORMA.equals(pdfContext.getInvoiceType()) ? "proFormaInvoiceNumber" : "invoiceNumber"),
                locale) + colon + pdfContext.getInvoiceNumber();
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
        //final String regNumber = getMessage("invoice.pdf.regNumber", locale) + ": " + pdfContext.getRegistration().getRegId().toString();
        cell1 = createCell(createParagraph(issued, PcsPdfFont.P_SMALL_NORMAL));
        cell2 = createCell(createCenterParagraph(dateOfFulfilment, PcsPdfFont.P_SMALL_NORMAL, locale));
        PdfPCell cell3 = createCell(createCenterParagraph(paymentDeadlineDate, PcsPdfFont.P_SMALL_NORMAL, locale));
        PdfPCell cell4 = createCell(createRightParagraph("", PcsPdfFont.P_SMALL_NORMAL));

        setBorder(1, cell1, cell2, cell3, cell4);
        addTableCell(table, cell1, cell2, cell3, cell4);

        preface.add(table);

        table.setSplitRows(true);  //the table will be break into new page if it reaches the bottom of the current page
        table = createTable(7, 100, new float[]{5, 1, 1, 1, 0.7f, 1, 1});
        table.setSplitRows(true);  //the table will be break into new page if it reaches the bottom of the current page
        table.setSpacingBefore(7);

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

        addTableCell(table, cell1, cell2, cell3, cell4, cell5, cell6, cell7);

        // Misc item list
        Map<String, List<InvoiceItem>> invoiceItemMap = getSortedChargeableItemRows(ChargeableItemType.MISCELLANEOUS, pdfContext.getInvoiceItemList());
        for (String key : invoiceItemMap.keySet()) {
            //table header row
            final String openParenthesis = " (";
            String itemKeyHeader = getMessage("miscInvoice.pdf.miscItemFees", locale) + (!key.isEmpty() ? openParenthesis + key + ")" : "");
            cell1 = createCell(createParagraph(itemKeyHeader, PcsPdfFont.P_SMALL_BOLD));
            cell1.setColspan(7);
            addTableCell(table, cell1);

            for (InvoiceItem item : invoiceItemMap.get(key)) {
                String title = item.getItemName() + (StringUtils.hasText(item.getItemDesc()) ? openParenthesis + item.getItemDesc() + ")" : "");
                String unit = item.getUnit() != null ? item.getUnit().toString() + (item.getUnitOfMeasure() != null ? " " + item.getUnitOfMeasure() : "") : "";
                cell1 = createCell(createParagraph(title, PcsPdfFont.P_MINIATURE_NORMAL));
                cell2 = createCell(createRightParagraph(unit, PcsPdfFont.P_MINIATURE_NORMAL));
                cell3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getUnitPrice(), pdfContext.getCurrency()), pdfContext.getLocale()),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                cell4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatBase(), pdfContext.getCurrency()), pdfContext.getLocale()),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                cell5 = createCell(createRightParagraph(VatRateType.REGULAR.equals(item.getVatRateType()) ? item.getVat() + "%" : item.getVatRateType().toString(),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                cell6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getVatValue(), pdfContext.getCurrency()), pdfContext.getLocale()),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                cell7 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(item.getTotal(), pdfContext.getCurrency()), pdfContext.getLocale())
                        + " " + item.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));

                addTableCell(table, cell1, cell2, cell3, cell4, cell5, cell6, cell7);
            }
        }

        preface.add(table);

        //Fizetendő blokk
        table = createTable(2, 100, new float[]{1, 1});
        table.setSpacingBefore(7);
        BigDecimal sumPrices = getInvoiceItemsSumAmount(pdfContext.getInvoiceItemList());
        cell1 = createCell(createParagraphWithMessage("invoice.pdf.payable", PcsPdfFont.P_SMALL_BOLD, locale));
        cell2 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(sumPrices, pdfContext.getCurrency()), pdfContext.getLocale())
                + " " + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));
        cell1.setBorder(1);
        cell2.setBorder(1);
        addTableCell(table, cell1, cell2);

        if (!HUF.toString().equalsIgnoreCase(pdfContext.getCurrency())) {
            //new row
            final String convertedCurrency = formatter.format(formatter.formatByCurrency(currencyService.convertCurrencyToHuf(sumPrices, pdfContext.getCurrencyExchangeRate()),
                    HUF.toString()), pdfContext.getLocale());
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
        if (!InvoiceType.PRO_FORMA.equals(pdfContext.getInvoiceType())) {
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
                        vatBase = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getVatBase().multiply(pdfContext.getCurrencyExchangeRate()), HUF.toString()),
                                pdfContext.getLocale()) + " " + HUF.toString();
                        vatValue = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getVatValue().multiply(pdfContext.getCurrencyExchangeRate()), HUF.toString()),
                                pdfContext.getLocale()) + " " + HUF.toString();
                        total = formatter.format(formatter.formatByCurrency(itemRowsByVat.get(key).getTotal().multiply(pdfContext.getCurrencyExchangeRate()), HUF.toString()),
                                pdfContext.getLocale()) + " " + HUF.toString();
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
            cell3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(totalVatValue, pdfContext.getCurrency()), pdfContext.getLocale()) + " "
                    + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));
            cell2 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(totalVatBase, pdfContext.getCurrency()), pdfContext.getLocale()) + " "
                    + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));
            cell4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(grandTotal, pdfContext.getCurrency()), pdfContext.getLocale()) + " "
                    + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_BOLD));

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
            preface.add(table);
        }

        String additionalBillingText = pdfContext.getLocale().equals(Locale.forLanguageTag("hu")) ? pdfContext.getCongress().getAdditionalBillingTextHu()
                : pdfContext.getCongress().getAdditionalBillingTextEn();
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

}
