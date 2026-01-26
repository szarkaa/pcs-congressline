package hu.congressline.pcs.service.pdf;

import org.openpdf.text.Document;
import org.openpdf.text.Rectangle;
import org.openpdf.text.pdf.BaseFont;
import org.openpdf.text.pdf.PdfContentByte;
import org.openpdf.text.pdf.PdfPageEventHelper;
import org.openpdf.text.pdf.PdfTemplate;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.context.MessageSource;

import java.util.Locale;

public class OnlineRegHeaderFooter extends PdfPageEventHelper {

    private final MessageSource messageSource;
    private final Locale locale;
    private final BaseFont baseFont;
    private PdfTemplate total;

    public OnlineRegHeaderFooter(MessageSource messageSource, Locale locale) {
        this.baseFont = PcsPdfFont.getBaseFont();
        this.messageSource = messageSource;
        this.locale = locale;
    }

    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(100, 100);
        total.setBoundingBox(new Rectangle(-20, -20, 100, 100));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void onEndPage(PdfWriter writer, Document document) {
        String text = String.format(messageSource.getMessage("confirmation.pdf.footer.page", null, locale) + " %d /", writer.getPageNumber());

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
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void onCloseDocument(PdfWriter writer, Document document) {
        total.beginText();
        total.setFontAndSize(baseFont, 6);
        total.setTextMatrix(0, 0);
        total.showText(String.valueOf(writer.getPageNumber()));
        total.endText();
    }
}
