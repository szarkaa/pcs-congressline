package hu.congressline.pcs.service.pdf;

import org.apache.commons.io.IOUtils;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.context.MessageSource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvoiceHeaderFooter extends PdfPageEventHelper {

    private static final String DOCUMENT_EXCEPTION = "Document exception";
    protected Font h1;
    protected Font h2;
    protected Font h3;

    protected Font paragraphNormal;
    protected Font paragraphBold;

    protected Font paragraphSmallNormal;
    protected Font paragraphSmallBold;

    protected PdfPTable embeddedTable;
    protected PdfPCell cell1;
    protected PdfPCell cell2;
    protected PdfPCell cell3;
    protected PdfPCell cell4;

    private final MessageSource messageSource;
    private final PdfContext pdfContext;
    private final BaseFont baseFont;
    private PdfTemplate total;
    private PdfPTable table;

    public InvoiceHeaderFooter(MessageSource messageSource, PdfContext pdfContext) {
        this.baseFont = PcsPdfFont.BASE_FONT;
        this.messageSource = messageSource;
        this.pdfContext = pdfContext;
        //Font styles of the pdf
        h1 = new Font(baseFont, 18, Font.BOLD);
        h2 = new Font(baseFont, 14, Font.BOLD);
        h3 = new Font(baseFont, 12, Font.BOLD);

        paragraphNormal = new Font(baseFont, 10, Font.NORMAL);
        paragraphBold = new Font(baseFont, 10, Font.BOLD);

        paragraphSmallNormal = new Font(baseFont, 8, Font.NORMAL);
        paragraphSmallBold = new Font(baseFont, 8, Font.BOLD);
    }

    //This callback function will be called when the document will be opened
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(100, 100);
        total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
    }

    //This callback function will be called when a new page starts in the pdf
    //This will give us the opportunity, to insert the header as any other content
    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    public void onStartPage(PdfWriter writer, Document document) {
        ////////////////////////////
        //        HEADER          //
        ////////////////////////////

        Paragraph header = new Paragraph();

        String mainTite;
        if (pdfContext.getStorno()) {
            mainTite = "invoice.pdf.invoiceStornoCaps";
        } else {
            mainTite = switch (pdfContext.getInvoiceType()) {
                case PRO_FORMA -> "invoice.pdf.proFormaInvoiceCaps";
                case PREPAYMENT -> "invoice.pdf.prePaymentInvoiceCaps";
                default -> "invoice.pdf.invoiceCaps";
            };
        }
        Paragraph tempParagraph = new Paragraph(messageSource.getMessage(mainTite, new Object[]{}, pdfContext.getLocale()), h1);
        tempParagraph.setAlignment(Element.ALIGN_CENTER);
        header.add(tempParagraph);
        addEmptyLine(header, 1); // enters

        //new table with 2 columns
        table = new PdfPTable(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{2, 1});
        } catch (DocumentException e) {
            log.error(DOCUMENT_EXCEPTION, e);
        }
        table.setSpacingAfter(0);
        table.setSpacingBefore(0);

        tempParagraph = new Paragraph();
        tempParagraph.setLeading(11);
        tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.customer", new Object[]{}, pdfContext.getLocale()), paragraphSmallBold));
        tempParagraph.add(Chunk.NEWLINE);

        if (StringUtils.hasText(pdfContext.getName1())) {
            tempParagraph.add(new Chunk(pdfContext.getName1(), paragraphSmallBold));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(pdfContext.getName2())) {
            tempParagraph.add(new Chunk(pdfContext.getName2(), paragraphSmallBold));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(pdfContext.getName3())) {
            tempParagraph.add(new Chunk(pdfContext.getName3(), paragraphSmallBold));
            tempParagraph.add(Chunk.NEWLINE);
        }

        final String colon = ": ";
        if (StringUtils.hasText(pdfContext.getVatRegNumber())) {
            tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.vatNumber", new Object[]{}, pdfContext.getLocale())
                    + colon + pdfContext.getVatRegNumber(), paragraphSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (pdfContext.getCity() != null) {
            tempParagraph.add(new Chunk(pdfContext.getCity(), paragraphSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (pdfContext.getStreet() != null) {
            tempParagraph.add(new Chunk(pdfContext.getStreet(), paragraphSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (pdfContext.getZipCode() != null) {
            tempParagraph.add(new Chunk(pdfContext.getZipCode(), paragraphSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (pdfContext.getCountry() != null) {
            tempParagraph.add(new Chunk(pdfContext.getCountry(), paragraphSmallNormal));
        }

        cell1 = new PdfPCell(tempParagraph);

        tempParagraph = new Paragraph();
        tempParagraph.setLeading(11);
        tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.supplier", new Object[]{}, pdfContext.getLocale()), paragraphSmallBold));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(pdfContext.getCompany().getName() + (!"hu".equals(pdfContext.getLocale().getLanguage()) ? " (Ltd.)" : ""),
                paragraphSmallBold));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(pdfContext.getCompany().getFullAddress(), paragraphSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        if (pdfContext.getCompany().getAddress2() != null && !pdfContext.getCompany().getAddress2().isEmpty()) {
            tempParagraph.add(new Chunk(pdfContext.getCompany().getAddress2(), paragraphSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }
        final String dotAndColon = ".: ";
        tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.tel", new Object[]{}, pdfContext.getLocale())
                + dotAndColon + pdfContext.getCompany().getPhone(), paragraphSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.fax", new Object[]{}, pdfContext.getLocale())
                + dotAndColon + pdfContext.getCompany().getFax(), paragraphSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.manager", new Object[]{}, pdfContext.getLocale())
                + colon + pdfContext.getContactPerson(), paragraphSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(messageSource.getMessage("invoice.pdf.email", new Object[]{}, pdfContext.getLocale())
                + colon + pdfContext.getContactEmail(), paragraphSmallNormal));

        cell2 = new PdfPCell(tempParagraph);

        cell1.setBorder(0);
        cell1.setPaddingLeft(0);
        cell2.setBorder(0);
        cell2.setPaddingRight(0);
        table.setSpacingAfter(10);
        addTableCell(table, cell1, cell2);
        header.add(table);

        try {
            document.add(header);
        } catch (DocumentException e) {
            log.error(DOCUMENT_EXCEPTION, e);
        }
    }

    //This callback function will be called when a page ends in the pdf
    //This will give us the opportunity, to render the page number, and the total number, or even a complex footer
    @SuppressWarnings("MissingJavadocMethod")
    public void onEndPage(PdfWriter writer, Document document) {
        //Rendering the page number (without the total page number)
        String text = String.format(messageSource.getMessage("invoice.pdf.page", new Object[]{}, pdfContext.getLocale()) + " %d /", writer.getPageNumber());

        float footerBase = document.bottom() - 25 - 65;
        float textSize = 9;

        //this will render the total number of the pages
        PdfContentByte cb = writer.getDirectContent();
        cb.beginText();
        cb.setFontAndSize(baseFont, 6);
        cb.setTextMatrix(document.right() - textSize - 15, footerBase);
        cb.showText(text);
        cb.endText();
        cb.addTemplate(total, document.right(), footerBase);
        //important to control pdf errors
        cb.sanityCheck();

        generateFooterContent(writer, document, footerBase, cb);
    }

    @SuppressWarnings({"MethodLength", "ParameterAssignment"})
    private void generateFooterContent(PdfWriter writer, Document document, float footerBase, PdfContentByte cb) {

        //new table
        table = new PdfPTable(2);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);
        table.setWidthPercentage(100);
        try {
            table.setWidths(new float[]{1, 2});
        } catch (DocumentException e) {
            log.error(DOCUMENT_EXCEPTION, e);
        }

        //new row
        //this cell holds the logo image of the congress
        cell1 = new PdfPCell(insertLogo());
        cell2 = new PdfPCell();

        cell1.setBorder(0);
        cell1.setPaddingTop(10);
        cell1.setPaddingBottom(0);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(0);
        cell2.setPaddingBottom(0);
        cell2.setPaddingLeft(0);

        //preparing embedded table for cell 2
        embeddedTable = new PdfPTable(2);
        embeddedTable.setSpacingBefore(0);
        embeddedTable.setSpacingAfter(0);
        embeddedTable.setWidthPercentage(90);
        try {
            embeddedTable.setWidths(new float[]{2, 3});
        } catch (DocumentException e) {
            log.error("Document expception", e);
        }
        embeddedTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.bank", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getBankName(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.address", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getBankAddress(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.swiftCode", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getSwiftCode(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.accountNumber", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getBankAccount(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.EUTaxNumber", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getCompany().getEuTaxNumber(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.HUNTaxNumber", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getCompany().getTaxNumber(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("invoice.pdf.admissionNumber", new Object[]{}, pdfContext.getLocale()) + ":", paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(pdfContext.getCompany().getLicenceNumber(), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        addTableCell(table, cell1);
        cell2.addElement(embeddedTable);
        addTableCell(table, cell2);

        // write the table to an absolute position
        cb.beginText();
        cb = writer.getDirectContent();
        cb.setTextMatrix(document.left(), footerBase + 7);
        table.setTotalWidth(600);
        table.writeSelectedRows(0, -1, 0, table.getTotalHeight() + 30, cb);
        cb.endText();

        //the generator system of the invoice
        cb.beginText();
        cb.setFontAndSize(baseFont, 6);
        //cb.setTextMatrix(document.left(), footerBase + 7);
        cb.setTextMatrix(document.left(), footerBase);
        cb.showText(messageSource.getMessage("invoice.pdf.invoiceGenerateMsg", new Object[]{}, pdfContext.getLocale()));
        cb.endText();

        //the generator system of the invoice
        //cb.beginText();
        //cb.setFontAndSize(baseFont, 6);
        //cb.setTextMatrix(document.left(), footerBase);
        //cb.showText("The invoice has been created by the AAT Development's PCS system.");
        //cb.endText();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void onCloseDocument(PdfWriter writer, Document document) {
        total.beginText();
        total.setFontAndSize(baseFont, 6);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber()));
        total.endText();
    }

    @SuppressWarnings("IllegalCatch")
    private Image insertLogo() {
        Image logo = null;
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final byte[] bytes = IOUtils.toByteArray(resolver.getResource("classpath:openpdf/images/logo.jpg").getInputStream());
            logo = Image.getInstance(bytes);
            logo.scaleToFit(75, 75);
        } catch (Exception e) {
            log.error("Can't find logo", e);
            return null;
        }
        return logo;
    }

    private void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    private void addTableCell(PdfPTable tbl, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            tbl.addCell(cell);
        }
    }

}
