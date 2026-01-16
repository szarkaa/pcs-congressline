package hu.congressline.pcs.service.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HexFormat;
import java.util.TimeZone;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import hu.gov.nav.schemas.osa._3_0.data.InvoiceData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NavOnlineUtil {
    public static final String SHA3_512 = "SHA3-512";
    private static final String UTC = "UTC";

    private NavOnlineUtil() {

    }

    @SuppressWarnings("MissingJavadocMethod")
    public static <T> void marshallXml(Object xmlObject, Class<T> clazz, OutputStream outputStream) throws JAXBException {
        JAXBContext requestCtx = JAXBContext.newInstance(clazz);
        Marshaller m = requestCtx.createMarshaller();
        m.marshal(xmlObject, outputStream);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static <T> T unmarshallXml(Class<T> clazz, InputStream inputStream) throws JAXBException {
        JAXBContext requestCtx = JAXBContext.newInstance(clazz);
        Unmarshaller um = requestCtx.createUnmarshaller();
        return (T) um.unmarshal(inputStream);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static void logXmlAsString(Object xmlObject, Class<?> clazz) throws JAXBException {
        JAXBContext requestCtx = JAXBContext.newInstance(clazz);
        Marshaller m = requestCtx.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        m.marshal(xmlObject, sw);
        log.debug(sw.toString());
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Long generateCRC32VerifyCode(byte[] invoice) {
        Checksum checksum = new CRC32();
        checksum.update(invoice, 0, invoice.length);
        long checksumValue = checksum.getValue();
        log.debug("headless invoice xml CRC32 checksum: {}", checksumValue);
        return checksumValue;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static String marsallInvoice(InvoiceData invoice) {
        try {
            JAXBContext context = JAXBContext.newInstance(InvoiceData.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(invoice, stringWriter);
            String xml = stringWriter.toString();
            log.debug("invoice xml: {}", xml);
            return xml;
        } catch (JAXBException e) {
            log.error("Error marshalling invoice", e);
        }
        return null;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static String hashWithSHA3_512(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA3_512);
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA3-512 not supported", e);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static String hashWithSHA512(String value, String salt) {
        log.debug("value for hashing: {}", value);
        String generatedHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            if (salt != null) {
                md.update(salt.getBytes(StandardCharsets.UTF_8));
            }
            byte[] bytes = md.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            generatedHash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-512 not supported", e);
        }
        return generatedHash.toUpperCase();
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public static String decryptAES128ECB(byte[] strToDecrypt, String secret) {
        try {
            byte[] keyBytes = secret.getBytes();
            Key keySpec = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decValue = cipher.doFinal(strToDecrypt);
            return new String(decValue);
        } catch (Exception e) {
            log.error("Error while decrypting AES128ECB: {}", e.toString(), e);
        }
        return null;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Date getUtcDate() {
        TimeZone timeZone = TimeZone.getTimeZone(UTC);
        Calendar calendar = Calendar.getInstance(timeZone);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static String getStrippedUtcDate(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        TimeZone timeZone = TimeZone.getTimeZone(UTC);
        df.setTimeZone(timeZone);
        return df.format(date);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static XMLGregorianCalendar getXmlUtcDate(Date date) {
        return getXmlDate(date, ZoneOffset.UTC);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static XMLGregorianCalendar getXmlLocalDate(Date date) {
        return getXmlDate(new Date(), ZoneId.of("Europe/Budapest"));
    }

    private static XMLGregorianCalendar getXmlDate(Date date, ZoneId zoneId) {
        try {
            ZonedDateTime zdt = date.toInstant().atZone(zoneId);

            return DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(
                    zdt.getYear(),
                    zdt.getMonthValue(),
                    zdt.getDayOfMonth(),
                    zdt.getHour(),
                    zdt.getMinute(),
                    zdt.getSecond(),
                    zdt.getNano() / 1_000_000,
                    zdt.getOffset().getTotalSeconds() / 60 // timezone offset in minutes
                );
        } catch (DatatypeConfigurationException e) {
            throw new IllegalStateException("Error creating XML date", e);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public static Date convertLocalDateToDate(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        //assuming start of day
        calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
        return calendar.getTime();
    }

}
