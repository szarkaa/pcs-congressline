package hu.congressline.pcs.service.pdf;

import org.apache.commons.io.IOUtils;
import org.openpdf.text.Chunk;
import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.Image;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfTemplate;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvoiceHeaderFooter extends PdfPageEventHelper {

    private static final String DOCUMENT_EXCEPTION = "Document exception";

    protected Font h1;
    protected Font h2;
    protected Font h3;

    protected Font parNormal;
    protected Font parBold;

    protected Font parSmallNormal;
    protected Font parSmallBold;

    protected PdfPTable embeddedTable;
    protected PdfPCell cell1;
    protected PdfPCell cell2;
    protected PdfPCell cell3;
    protected PdfPCell cell4;

    private final BaseFont baseFont;
    private PdfTemplate total;
    private PdfPTable table;

    private final InvoicePdfHeaderFooterTextContext textContext;

    public InvoiceHeaderFooter(@NonNull InvoicePdfHeaderFooterTextContext textContext) {
        this.textContext = textContext;
        this.baseFont = PcsPdfFont.getBaseFont();
        //Font styles of the pdf
        h1 = new Font(baseFont, 18, Font.BOLD);
        h2 = new Font(baseFont, 14, Font.BOLD);
        h3 = new Font(baseFont, 12, Font.BOLD);

        parNormal = new Font(baseFont, 10, Font.NORMAL);
        parBold = new Font(baseFont, 10, Font.BOLD);

        parSmallNormal = new Font(baseFont, 8, Font.NORMAL);
        parSmallBold = new Font(baseFont, 8, Font.BOLD);
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
        Paragraph header = new Paragraph();

        Paragraph tempParagraph = new Paragraph(textContext.getMainTitleLabel(), h1);
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
        tempParagraph.add(new Chunk(textContext.getCustomerLabel(), parSmallBold));
        tempParagraph.add(Chunk.NEWLINE);

        if (StringUtils.hasText(textContext.getCustomerName1Value())) {
            tempParagraph.add(new Chunk(textContext.getCustomerName1Value(), parSmallBold));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(textContext.getCustomerName2Value())) {
            tempParagraph.add(new Chunk(textContext.getCustomerName2Value(), parSmallBold));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(textContext.getCustomerName3Value())) {
            tempParagraph.add(new Chunk(textContext.getCustomerName3Value(), parSmallBold));
            tempParagraph.add(Chunk.NEWLINE);
        }

        final String colon = ": ";
        if (StringUtils.hasText(textContext.getCustomerVatRegNumberValue())) {
            tempParagraph.add(new Chunk(textContext.getCustomerVatRegNumberLabel()
                    + colon + textContext.getCustomerVatRegNumberValue(), parSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(textContext.getCustomerCityValue())) {
            tempParagraph.add(new Chunk(textContext.getCustomerCityValue(), parSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(textContext.getCustomerStreetValue())) {
            tempParagraph.add(new Chunk(textContext.getCustomerStreetValue(), parSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(textContext.getCustomerZipCodeValue())) {
            tempParagraph.add(new Chunk(textContext.getCustomerZipCodeValue(), parSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }

        if (StringUtils.hasText(textContext.getCustomerCountryValue())) {
            tempParagraph.add(new Chunk(textContext.getCustomerCountryValue(), parSmallNormal));
        }

        cell1 = new PdfPCell(tempParagraph);

        tempParagraph = new Paragraph();
        tempParagraph.setLeading(11);
        tempParagraph.add(new Chunk(textContext.getSupplierNameLabel(), parSmallBold));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(textContext.getSupplierNameValue() + (!"hu".equals(textContext.getLocale().getLanguage()) ? " (Ltd.)" : ""), parSmallBold));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(textContext.getSupplierAddressValue(), parSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        if (StringUtils.hasText(textContext.getSupplierAddress2Value())) {
            tempParagraph.add(new Chunk(textContext.getSupplierAddress2Value(), parSmallNormal));
            tempParagraph.add(Chunk.NEWLINE);
        }
        final String dotAndColon = ".: ";
        tempParagraph.add(new Chunk(textContext.getSupplierPhoneLabel() + dotAndColon + textContext.getSupplierPhoneValue(), parSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(textContext.getSupplierFaxLabel() + dotAndColon + textContext.getSupplierFaxValue(), parSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(textContext.getSupplierManagerLabel() + dotAndColon + textContext.getSupplierManagerValue(), parSmallNormal));
        tempParagraph.add(Chunk.NEWLINE);
        tempParagraph.add(new Chunk(textContext.getSupplierEmailLabel() + dotAndColon + textContext.getSupplierEmailValue(), parSmallNormal));
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
        float footerBase = document.bottom() - 25 - 65;
        float textSize = 9;

        //this will render the total number of the pages
        PdfContentByte cb = writer.getDirectContent();
        cb.beginText();
        cb.setFontAndSize(baseFont, 6);
        cb.setTextMatrix(document.right() - textSize - 15, footerBase);
        cb.showText(String.format(textContext.getPageNumberLabel() + " %d /", writer.getPageNumber()));
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
        cell3 = new PdfPCell(new Paragraph(textContext.getBankNameLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getBankNameValue(), parSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(textContext.getBankAddressLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getBankAddressValue(), parSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(textContext.getBankSwiftCodeLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getBankSwiftCodeValue(), parSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(textContext.getBankAccountLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getBankAccountValue(), parSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(textContext.getEuTaxNoLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getEuTaxNoValue(), parSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(textContext.getHuTaxNoLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getHuTaxNoValue(), parSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(textContext.getAdmissionNumberLabel(), parSmallNormal));
        cell4 = new PdfPCell(new Paragraph(textContext.getAdmissionNumberValue(), parSmallNormal));

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
        cb.showText(textContext.getInvoiceGeneratedMsgLabel());
        cb.endText();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void onCloseDocument(PdfWriter writer, Document document) {
        total.beginText();
        total.setFontAndSize(baseFont, 6);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber() - 1)); // bug? show one more page than actually exists
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
