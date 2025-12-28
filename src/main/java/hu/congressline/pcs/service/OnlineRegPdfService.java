package hu.congressline.pcs.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfCopyFields;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.AccPeopleOnline;
import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationOptionalService;
import hu.congressline.pcs.domain.OnlineRegistrationRegistrationType;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.RegistrationTypeType;
import hu.congressline.pcs.repository.AccPeopleOnlineRepository;
import hu.congressline.pcs.repository.OnlineRegistrationOptionalServiceRepository;
import hu.congressline.pcs.repository.OnlineRegistrationRegistrationTypeRepository;
import hu.congressline.pcs.service.dto.kh.PaymentStatus;
import hu.congressline.pcs.service.pdf.OnlineRegHeaderFooter;
import hu.congressline.pcs.service.pdf.OnlineRegPdfContext;
import hu.congressline.pcs.service.pdf.PcsPdfFont;
import hu.congressline.pcs.service.pdf.PdfContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class OnlineRegPdfService extends AbstractPdfService {
    private static final String MESSAGE_SOURCE_PREFIX = "online.reg.pdf.";

    private final OnlineRegistrationOptionalServiceRepository orosRepository;
    private final OnlineRegistrationRegistrationTypeRepository orrtRepository;
    private final AccPeopleOnlineRepository accPeopleOnlineRepository;
    private final OnlineRegService onlineRegService;
    private final ApplicationProperties properties;

    @SuppressWarnings("MissingJavadocMethod")
    public byte[] getPdf(OnlineRegistration onlineReg) {
        final List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(onlineReg);
        final List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(onlineReg);

        return generatePdf(new OnlineRegPdfContext(onlineReg, orrtList, orosList));
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    public byte[] getAllPdf(List<OnlineRegistration> orList) {
        final String errorCreatingAllOnlinePdf = "Error while creating all online reg pdf";

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PdfCopyFields copy = new PdfCopyFields(baos);
            copy.open();
            for (OnlineRegistration or : orList) {
                try {
                    // assuming getPdf(or) returns byte[] for a single registration PDF
                    byte[] pdfBytes = getPdf(or);
                    PdfReader reader = new PdfReader(pdfBytes);

                    copy.addDocument(reader);   // <- this exists on PdfCopyFields
                    reader.close();
                } catch (DocumentException | IOException e) {
                    log.error(errorCreatingAllOnlinePdf, e);
                }
            }

            copy.close(); // writes final merged PDF into baos
            return baos.toByteArray();
        } catch (Exception e) {
            log.error(errorCreatingAllOnlinePdf, e);
            return null;
        }
    }

    @SuppressWarnings("IllegalCatch")
    @Override
    public byte[] generatePdf(PdfContext context) {
        OnlineRegPdfContext pdfContext = (OnlineRegPdfContext) context;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            //A4 size with predefined margins (bottom is 100, because the footer section will take that place
            //Top header section is generated on the fly, not after the pdf creation, like the footer)
            Document document = new Document(PageSize.A4, 20, 20, 40, 100);

            //set a writer
            PdfWriter writer = PdfWriter.getInstance(document, baos);

            //set the listener for events like (new page, end page, open document, close document etc.)
            OnlineRegHeaderFooter event = new OnlineRegHeaderFooter(messageSource, pdfContext.getLocale());
            writer.setPageEvent(event);
            writer.setBoxSize("art", new Rectangle(36, 54, 559, 788)); //this box contains the footer's page of pages section

            document.open();
            addMetaData(document, pdfContext);
            createContentTitle(document, pdfContext);
            createCongressTitle(document, pdfContext);
            createDataTable(document, pdfContext);
            document.close();
            writer.flush();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error while creating online reg pdf", e);
        }
        return null;
    }

    private void addMetaData(Document document, OnlineRegPdfContext pdfContext) {
        final String onlineReg = "onlineReg";
        document.addTitle(getStaticMessage(onlineReg, pdfContext.getLocale()));
        document.addSubject(getStaticMessage(onlineReg, pdfContext.getLocale()));
        document.addKeywords("");
        final String pcsSystem = "PCS System";
        document.addAuthor(pcsSystem);
        document.addCreator(pcsSystem);
    }

    private String getStaticMessage(String messageKey, Locale locale) {
        return messageSource.getMessage(MESSAGE_SOURCE_PREFIX + messageKey, new Object[]{}, locale);
    }

    private void createContentTitle(Document document, OnlineRegPdfContext pdfContext) throws DocumentException {
        Paragraph p = new Paragraph(getStaticMessage("onlineRegCapital", pdfContext.getLocale()), PcsPdfFont.H_2);
        p.setAlignment(Element.ALIGN_CENTER);
        document.add(p);
    }

    private void createCongressTitle(Document document, OnlineRegPdfContext pdfContext) throws DocumentException {
        Paragraph p = new Paragraph(pdfContext.getOnlineReg().getCongress().getMeetingCode(), PcsPdfFont.H_3);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(30);
        document.add(p);
    }

    @SuppressWarnings("MethodLength")
    private void createDataTable(Document document, OnlineRegPdfContext pdfContext) throws DocumentException {
        final OnlineRegistration onlineReg = pdfContext.getOnlineReg();
        final Locale locale = pdfContext.getLocale();

        PdfPTable table = new PdfPTable(2);
        table.setSplitRows(true);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3});
        addRow("title", onlineReg.getTitle(), locale, table);
        addRow("lastName", onlineReg.getLastName(), locale, table);
        addRow("firstName", onlineReg.getFirstName(), locale, table);
        addRow("workplace", onlineReg.getWorkplace(), locale, table);
        addRow("position", onlineReg.getPosition(), locale, table);
        addRow("department", onlineReg.getDepartment(), locale, table);
        addRow("otherData", onlineReg.getOtherData(), locale, table);
        addRow("zipCode", onlineReg.getZipCode(), locale, table);
        addRow("city", onlineReg.getCity(), locale, table);
        addRow("street", onlineReg.getStreet(), locale, table);
        addRow("country", onlineReg.getCountry() != null ? onlineReg.getCountry().getName() : null, locale, table);
        addRow("phone", onlineReg.getPhone(), locale, table);
        addRow("email", onlineReg.getEmail(), locale, table);
        addRow("registration", onlineReg.getRegistrationType() != null ? onlineReg.getRegistrationType().getName() : null, locale, table);

        List<OnlineRegistrationRegistrationType> orrtList = orrtRepository.findAllByRegistration(onlineReg);
        final List<OnlineRegistrationRegistrationType> orrtAccompaningList = orrtList.stream()
                .filter(orrt -> orrt.getRegistrationType().getRegistrationType().equals(RegistrationTypeType.ACCOMPANYING_FEE)).collect(Collectors.toList());
        IntStream.range(0, orrtAccompaningList.size()).forEach(idxOrrt -> {
            final OnlineRegistrationRegistrationType orrt = orrtAccompaningList.get(idxOrrt);
            StringBuilder sb = new StringBuilder();
            final List<AccPeopleOnline> accPeopleOnlineList = accPeopleOnlineRepository.findAllByOnlineRegistrationRegistrationType(orrt);
            IntStream.range(0, accPeopleOnlineList.size()).forEach(idxAcc -> {
                final AccPeopleOnline accPeopleOnline = accPeopleOnlineList.get(idxAcc);
                sb.append(idxAcc > 0 ? ", " : "").append(accPeopleOnline.getLastName()).append(" ").append(accPeopleOnline.getFirstName());
            });

            if (sb.length() > 0) {
                sb.insert(0, " (");
                sb.append(")");
            }
            addRow(idxOrrt > 0 ? null : "accompanyingRegistration", orrt.getRegistrationType().getName() + sb.toString(), locale, table);
        });

        final List<OnlineRegistrationRegistrationType> orrtExtraRegList = orrtList.stream()
                .filter(orrt -> orrt.getRegistrationType().getRegistrationType().equals(RegistrationTypeType.REGISTRATION_FEE)).collect(Collectors.toList());
        IntStream.range(0, orrtExtraRegList.size()).forEach(idxOrrt -> {
            final OnlineRegistrationRegistrationType orrt = orrtExtraRegList.get(idxOrrt);
            addRow(idxOrrt > 0 ? null : "extraRegistration", orrt.getRegistrationType().getName(), locale, table);
        });

        addRow("room", onlineReg.getRoom() != null ? onlineReg.getRoom().getRoomType() + "(" + onlineReg.getRoom().getCongressHotel().getHotel().getName() + ")"
                : null, locale, table);
        addRow("arrivalDate", getDate(onlineReg.getArrivalDate(), pdfContext), locale, table);
        addRow("departureDate", getDate(onlineReg.getDepartureDate(), pdfContext), locale, table);
        addRow("roommate", onlineReg.getRoommate(), locale, table);
        addRow("roomRemark", onlineReg.getRoomRemark(), locale, table);

        List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(onlineReg);
        IntStream.range(0, orosList.size()).forEach(idx -> {
            final OnlineRegistrationOptionalService oros = orosList.get(idx);
            addRow(idx > 0 ? null : "optionalServices", oros.getOptionalService().getName() + " " + oros.getParticipant() + "pax", locale, table);
        });

        addRow("invoiceName", onlineReg.getInvoiceName(), locale, table);
        addRow("invoiceAddress", onlineReg.getInvoiceAddress(), locale, table);
        addRow("invoiceReferenceNumberShort", onlineReg.getInvoiceReferenceNumber(), locale, table);
        addRow("invoiceTaxNumberShort", onlineReg.getInvoiceTaxNumber(), locale, table);

        String currency = onlineReg.getCurrency();
        addRow("paymentMethod", onlineReg.getPaymentMethod(), locale, table);
        if (onlineReg.getPaymentMethod() != null) {
            final String paymentTransactionStatus = "paymentTransactionStatus";
            final String paymentTransactionTime = "paymentTransactionTime";
            final String successful = "Successful";
            final String unsuccessful = "Unsuccessful";
            switch (onlineReg.getPaymentMethod()) {
                case "CARD" -> {
                    addRow("cardType", onlineReg.getCardType(), locale, table);
                    if ("AMEX".equals(onlineReg.getCardType())) {
                        addRow("cardNumber", onlineReg.getCardNumber(), locale, table);
                        addRow("cardHolderName", onlineReg.getCardHolderName(), locale, table);
                        addRow("cardHolderAddress", onlineReg.getCardHolderAddress(), locale, table);
                        addRow("cardExpiryMonth", onlineReg.getCardExpiryMonth(), locale, table);
                        addRow("cardExpiryYear", onlineReg.getCardExpiryYear(), locale, table);
                    }
                    addRow("paymentTransactionAmount", onlineRegService.getTotalAmountOfOnlineReg(onlineReg).toString(), locale, table);
                    addRow("paymentTransactionOrderNo", onlineReg.getPaymentOrderNumber(), locale, table);
                    addRow("paymentTransactionID", onlineReg.getPaymentTrxId(), locale, table);
                    addRow("paymentTransactionMethod", onlineReg.getPaymentMethod(), locale, table);
                    addRow("paymentMerchantID", Currency.HUF.toString().equalsIgnoreCase(currency) ? properties.getPayment().getGateway().getMerchantIdForHUF()
                            : properties.getPayment().getGateway().getMerchantIdForEUR(), locale, table);
                    addRow(paymentTransactionStatus, (onlineReg.getPaymentTrxStatus() != null ? onlineReg.getPaymentTrxStatus() : "")
                            + " " + (PaymentStatus.PAYMENT_WAITING_FOR_SETTLEMENT.toString().equals(onlineReg.getPaymentTrxStatus())
                            || PaymentStatus.PAYMENT_SETTLED.toString().equals(onlineReg.getPaymentTrxStatus()) ? successful : unsuccessful), locale, table);
                    addRow("paymentTransactionResponse", "PU Payment", locale, table);
                    addRow("paymentTransactionAuthCode", onlineReg.getPaymentTrxAuthCode(), locale, table);
                    addRow(paymentTransactionTime, onlineReg.getPaymentTrxDate() != null ? onlineReg.getPaymentTrxDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : "",
                            locale, table);
                }
                case "CHECK" -> {
                    addRow("checkName", onlineReg.getCheckName(), locale, table);
                    addRow("checkAddress", onlineReg.getCheckAddress(), locale, table);
                }
                case "STRIPE" -> {
                    addRow(paymentTransactionStatus, onlineReg.getPaymentTrxDate() != null ? successful : unsuccessful, locale, table);
                    addRow(paymentTransactionTime, onlineReg.getPaymentTrxDate() != null ? onlineReg.getPaymentTrxDate().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME) : "",
                            locale, table);
                }
                default -> throw new IllegalStateException("Unexpected payment method value: " + onlineReg.getPaymentMethod());
            }
        }

        /*
        Transaction time:   2013-10-09T14:47:11+02:00
        Transaction ID:     25
        Transaction type:   PU Payment
        Merchant ID:        576
        Status:             PAYMENT_SETTLED Successful
        Response:           1 ELFOGADVA / ENGEDELYEZVE
        Authorization Code: 592320 B
        */

        BigDecimal regSubTotal = onlineRegService.getRegistrationTypeSubTotalAmountOfOnlineReg(onlineReg);
        BigDecimal roomSubTotal = onlineRegService.getHotelAmountOfOnlineReg(onlineReg);
        BigDecimal osSubTotal = onlineRegService.getOptionalServiceTotalAmountOfOnlineReg(onlineReg);
        BigDecimal total = onlineRegService.getTotalAmountOfOnlineReg(onlineReg);
        addRow("regSubTotal", regSubTotal.compareTo(BigDecimal.ZERO) != 0 ? regSubTotal.toString() + currency : "", locale, table);
        addRow("roomSubTotal", roomSubTotal.compareTo(BigDecimal.ZERO) != 0 ? roomSubTotal.toString() + currency : "", locale, table);
        addRow("osSubTotal", osSubTotal.compareTo(BigDecimal.ZERO) != 0 ? osSubTotal.toString() + currency : "", locale, table);
        addRow("total", total.compareTo(BigDecimal.ZERO) != 0 ? total.toString() + currency : "", locale, table);

        if (onlineReg.getDiscountCode() != null) {
            addRow("discountCode", onlineReg.getDiscountCode(), locale, table);
        }
        addRow("dateOfApp", getDate(onlineReg.getDateOfApp().toLocalDate(), pdfContext), locale, table);
        final String yes = "Yes";
        addRow("termsAndConditions", yes, locale, table);
        addRow("gdpr", yes, locale, table);
        addRow("marketingEmail", Boolean.TRUE.equals(onlineReg.getNewsletter()) ? yes : "No", locale, table);
        document.add(table);
    }

    private void addRow(String fieldName, String fieldValue, Locale locale, PdfPTable table) {
        PdfPCell cell1 = createCell(new Paragraph(fieldName != null ? getStaticMessage(fieldName, locale) + ":" : "", PcsPdfFont.P_SMALL_BOLD));
        PdfPCell cell2 = createCell(new Paragraph(fieldValue != null ? fieldValue : "", PcsPdfFont.P_SMALL_NORMAL));
        table.addCell(cell1);
        table.addCell(cell2);
    }

    private String getDate(LocalDate date, PdfContext pdfContext) {
        String congressDate = "";
        if (date != null) {
            congressDate = date.format(pdfContext.getFormatter());
        }
        return congressDate;
    }

}
