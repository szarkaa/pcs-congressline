package hu.congressline.pcs.service.pdf;

import org.apache.commons.io.IOUtils;
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

import java.util.Locale;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfirmationHeaderFooter extends PdfPageEventHelper {

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
    private final Locale locale;
    private final BaseFont baseFont;
    private PdfTemplate total;

    @SuppressWarnings("MissingJavadocMethod")
    public ConfirmationHeaderFooter(MessageSource messageSource, PdfContext pdfContext) {
        this.baseFont = PcsPdfFont.BASE_FONT;
        this.messageSource = messageSource;
        this.pdfContext = pdfContext;
        this.locale = pdfContext.getLocale();

        h1 = new Font(baseFont, 18, Font.BOLD);
        h2 = new Font(baseFont, 14, Font.BOLD);
        h3 = new Font(baseFont, 12, Font.BOLD);

        paragraphNormal = new Font(baseFont, 10, Font.NORMAL);
        paragraphBold = new Font(baseFont, 10, Font.BOLD);

        paragraphSmallNormal = new Font(baseFont, 8, Font.NORMAL);
        paragraphSmallBold = new Font(baseFont, 8, Font.BOLD);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(100, 100);
        total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void onEndPage(PdfWriter writer, Document document) {
        String text = String.format(messageSource.getMessage("confirmation.pdf.page", null, locale) + " %d /", writer.getPageNumber());

        float footerBase = document.bottom() - 25 - 65;
        float textSize = 9;
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

    @SuppressWarnings("MissingJavadocMethod")
    public void onCloseDocument(PdfWriter writer, Document document) {
        total.beginText();
        total.setFontAndSize(baseFont, 6);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber()));
        total.endText();
    }

    @SuppressWarnings({"MethodLength", "ParameterAssignment"})
    private void generateFooterContent(PdfWriter writer, Document document, float footerBase, PdfContentByte cb) {
        PdfPTable table = new PdfPTable(2);
        table.setSpacingBefore(0);
        table.setSpacingAfter(0);
        table.setWidthPercentage(60);
        try {
            table.setWidths(new float[]{0.5F, 2});
        } catch (DocumentException e) {
            log.error("Document exception", e);
        }

        //new row
        //this cell holds the logo image of the congress
        cell1 = new PdfPCell(insertLogo());
        cell2 = new PdfPCell();

        cell1.setBorder(0);
        cell1.setPaddingTop(0);
        cell1.setPaddingBottom(0);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell2.setBorder(0);
        cell2.setPaddingBottom(0);
        cell2.setPaddingLeft(0);

        //preparing embedded table for cell 2
        embeddedTable = new PdfPTable(2);
        embeddedTable.setSpacingBefore(0);
        embeddedTable.setSpacingAfter(0);
        embeddedTable.setWidthPercentage(100);
        try {
            embeddedTable.setWidths(new float[]{2, 3});
        } catch (DocumentException e) {
            log.error("Document expception", e);
        }
        embeddedTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.companyName", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.bankName", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.address1", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.bankAddress", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.address2", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.swiftCode", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.email", new Object[]{}, pdfContext.getLocale())
                + " " + Objects.toString(pdfContext.getContactEmail(), ""), paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.bankAccountHuf", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.phone", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.bankAccountEur", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));

        cell3.setBorder(0);
        cell3.setPaddingBottom(0);
        cell3.setPaddingLeft(0);
        cell4.setBorder(0);
        cell4.setPaddingBottom(0);
        cell4.setPaddingLeft(0);

        addTableCell(embeddedTable, cell3, cell4);

        //new row for embedded table
        cell3 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.website", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));
        cell4 = new PdfPCell(new Paragraph(messageSource.getMessage("confirmation.pdf.footer.taxNo", new Object[]{}, pdfContext.getLocale()), paragraphSmallNormal));

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

    private void addTableCell(PdfPTable table, PdfPCell... cells) {
        for (PdfPCell cell : cells) {
            table.addCell(cell);
        }
    }
}
