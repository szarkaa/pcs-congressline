package hu.congressline.pcs.service.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PcsPdfFont {

    public static final Font H_1;
    public static final Font H_1_ITALIC;
    public static final Font H_2;
    public static final Font H_3;

    public static final Font P_NORMAL;
    public static final Font P_BOLD;
    public static final Font P_UNDERLINED;

    public static final Font P_SMALL_NORMAL;
    public static final Font P_SMALL_BOLD;
    public static final Font P_SMALL_UNDERLINED;

    public static final Font P_MINIATURE_NORMAL;
    public static final Font P_MINIATURE_BOLD;
    public static final Font P_MINIATURE_UNDERLINED;

    private static BaseFont baseFont;

    static {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            final String location = "classpath:openpdf/fonts/calibri.ttf";
            final byte[] bytes = IOUtils.toByteArray(resolver.getResource(location).getInputStream());
            baseFont = BaseFont.createFont(resolver.getResource(location).getFilename(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, true, bytes, null);
        } catch (DocumentException | IOException e) {
            log.error("Error while setting base font from a file.", e);
        }

        H_1 = new Font(baseFont, 20, Font.BOLD);
        H_1_ITALIC = new Font(baseFont, 20, Font.BOLD | Font.ITALIC);
        H_2 = new Font(baseFont, 14, Font.BOLD);
        H_3 = new Font(baseFont, 12, Font.BOLD);

        P_NORMAL = new Font(baseFont, 11, Font.NORMAL);
        P_BOLD = new Font(baseFont, 11, Font.BOLD);
        P_UNDERLINED = new Font(baseFont, 11, Font.UNDERLINE);

        P_SMALL_NORMAL = new Font(baseFont, 8, Font.NORMAL);
        P_SMALL_BOLD = new Font(baseFont, 8, Font.BOLD);
        P_SMALL_UNDERLINED = new Font(baseFont, 8, Font.UNDERLINE);

        P_MINIATURE_NORMAL = new Font(baseFont, 7, Font.NORMAL);
        P_MINIATURE_BOLD = new Font(baseFont, 7, Font.BOLD);
        P_MINIATURE_UNDERLINED = new Font(baseFont, 7, Font.UNDERLINE);

    }

    private PcsPdfFont() {

    }

    public static BaseFont getBaseFont() {
        return baseFont;
    }
}
