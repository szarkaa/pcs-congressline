package hu.congressline.pcs.service.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static java.util.Objects.requireNonNull;

public final class RichTextHtmlCodec {

    private static final String LINE_BREAK = "\n";

    private RichTextHtmlCodec() {

    }

    /**
     * Encode rich-text HTML for DB storage.
     *  - Unicode normalize (NFC)
     *  - Normalize line endings to \n
     *  - Remove common invisible junk chars (ZWSP/BOM/etc.)
     *  - Canonicalize + minify HTML (safe: does NOT collapse whitespace inside text nodes)
     *  - gzip compress
     *  - Base64 encode (so you can store it in a TEXT/VARCHAR column if needed)
     * Reversible by decodeFromDb() (returns canonical/minified HTML, not necessarily byte-identical to the original).
     */
    public static String encodeForDb(String richTextHtml) {
        if (richTextHtml == null || richTextHtml.isBlank()) {
            return null;
        }

        String normalized = preprocess(richTextHtml);
        String minified = minifyHtml(normalized);

        byte[] gz = gzip(minified.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(gz);
    }

    /**
     * Decode stored value back to HTML.
     *  - Base64 decode
     *  - gzip decompress
     *  - return UTF-8 string
     */
    public static String decodeFromDb(String stored) {
        if (stored == null || stored.isBlank()) {
            return null;
        }

        byte[] gz = Base64.getDecoder().decode(stored);
        byte[] utf8 = gunzip(gz);
        return new String(utf8, StandardCharsets.UTF_8);
    }

    /**
     * If you prefer storing readable HTML in a TEXT/CLOB column (no compression),
     * use this pair instead.
     */
    public static String encodeMinifiedPlain(String richTextHtml) {
        if (richTextHtml == null || richTextHtml.isBlank()) {
            return "";
        }
        return minifyHtml(preprocess(richTextHtml));
    }

    public static String decodeMinifiedPlain(String storedMinifiedHtml) {
        return storedMinifiedHtml == null ? "" : storedMinifiedHtml;
    }

    // ---------------- internals ----------------

    private static String preprocess(String s) {
        // 1) Unicode normalize: makes composed characters consistent (e.g., accents)
        String out = Normalizer.normalize(s, Normalizer.Form.NFC);

        // 2) Normalize line endings
        out = out.replace("\r\n", LINE_BREAK).replace("\r", LINE_BREAK);

        // 3) Remove common invisible/junk chars that editors often inject.
        //    Be careful: removing these is usually safe, but if your editor uses ZWSP intentionally,
        //    you can comment out the \u200B removal.
        out = out
            .replace("\uFEFF", "") // BOM / zero width no-break space
            .replace("\u200B", "") // zero width space
            .replace("\u200C", "") // zero width non-joiner
            .replace("\u200D", "") // zero width joiner
            .replace("\u2060", ""); // word joiner

        // 4) Trim outer whitespace (safe)
        return out.trim();
    }

    /**
     * “Safe minify” using jsoup:
     * - Parses as HTML
     * - Outputs without pretty-print (removes indentation/newlines between tags)
     * - Keeps semantics; does NOT aggressively alter whitespace inside text nodes
     *
     * Notes:
     * - This canonicalizes HTML (attribute quoting, entity escaping, etc.).
     * - If you need to keep <head>/<body> exactly as-is, consider storing fragment only.
     */
    private static String minifyHtml(String html) {
        requireNonNull(html, "html");

        // Parse as body fragment to avoid jsoup adding full html/head/body wrapper
        Document doc = Jsoup.parseBodyFragment(html, "");

        OutputSettings settings = new OutputSettings();
        settings.prettyPrint(false);                   // critical: no extra whitespace/indentation
        settings.escapeMode(org.jsoup.nodes.Entities.EscapeMode.base);
        settings.charset(StandardCharsets.UTF_8);

        doc.outputSettings(settings);

        // This returns the fragment inside <body> (not including the body tag)
        String out = doc.body().html();

        // Extra micro-cleanups (reversible and safe):
        // - remove trailing spaces on each line (in case editor pasted huge whitespace lines)
        // - collapse multiple blank lines
        out = out.replaceAll("[ \\t]+\\n", LINE_BREAK);
        out = out.replaceAll("\\n{3,}", "\n\n");

        // Optional: remove whitespace between tags only (safe and common in minifiers)
        // This will transform: </p>   <p> into </p><p>
        out = out.replaceAll(">\\s+<", "><");

        return out;
    }

    private static byte[] gzip(byte[] input) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(input);
            gzip.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            // Shouldn't happen with byte arrays, but wrap as unchecked for simplicity
            throw new UncheckedIOException("gzip failed", e);
        }
    }

    private static byte[] gunzip(byte[] gz) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(gz);
             GZIPInputStream gzip = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8 * 1024];
            int read;
            while ((read = gzip.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            return baos.toByteArray();

        } catch (IOException e) {
            throw new UncheckedIOException("gunzip failed", e);
        }
    }
}
