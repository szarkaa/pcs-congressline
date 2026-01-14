package hu.congressline.pcs.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.Room;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import hu.congressline.pcs.repository.InvoiceChargeRepository;
import hu.congressline.pcs.repository.InvoiceItemRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.service.pdf.ConfirmationHeaderFooter;
import hu.congressline.pcs.service.pdf.ConfirmationPdfContext;
import hu.congressline.pcs.service.pdf.PcsPdfFont;
import hu.congressline.pcs.service.pdf.PdfContext;
import hu.congressline.pcs.web.rest.vm.ConfirmationPdfVM;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ConfirmationPdfService extends AbstractPdfService {

    private static final String MESSAGE_SOURCE_PREFIX = "confirmation.pdf.";

    private final CompanyService companyService;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final RegistrationService registrationService;
    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final OrderedOptionalServiceRepository oosRepository;
    private final ChargedServiceRepository chargedServiceRepository;
    private final PriceService priceService;

    @SuppressWarnings("ParameterNumber")
    public ConfirmationPdfService(InvoiceItemRepository invoiceItemRepository, InvoiceChargeRepository invoiceChargeRepository, DiscountService discountService,
                                  CompanyService companyService, RoomReservationRegistrationRepository rrrRepository,
                                  RegistrationService registrationService, RegistrationRegistrationTypeRepository rrtRepository, OrderedOptionalServiceRepository oosRepository,
                                  ChargedServiceRepository chargedServiceRepository, PriceService priceService, MessageSource messageSource) {
        super(invoiceItemRepository, invoiceChargeRepository, discountService, messageSource);
        this.companyService = companyService;
        this.rrrRepository = rrrRepository;
        this.registrationService = registrationService;
        this.rrtRepository = rrtRepository;
        this.oosRepository = oosRepository;
        this.chargedServiceRepository = chargedServiceRepository;
        this.priceService = priceService;
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    private Image createQrImage(String text) {
        BufferedImage qr;
        try {
            qr = createQrBufferedImage(text);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qr, "PNG", baos);
            return Image.getInstance(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage createQrBufferedImage(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, 100, 100);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public ConfirmationPdfContext createContext(ConfirmationPdfVM vm) {
        final Registration registration = registrationService.getById(vm.getRegistrationId());
        final Map<ChargeableItemType, BigDecimal> chargedServiceAmountsMap = new HashMap<>();
        chargedServiceAmountsMap.put(ChargeableItemType.REGISTRATION, BigDecimal.ZERO);
        chargedServiceAmountsMap.put(ChargeableItemType.HOTEL, BigDecimal.ZERO);
        chargedServiceAmountsMap.put(ChargeableItemType.OPTIONAL_SERVICE, BigDecimal.ZERO);
        chargedServiceAmountsMap.put(ChargeableItemType.MISCELLANEOUS, BigDecimal.ZERO);

        for (ChargedService chargedService : chargedServiceRepository.findAllByRegistrationId(vm.getRegistrationId())) {
            if (!vm.getIgnoredChargedServiceIdList().contains(chargedService.getId())) {
                ChargeableItemType key = chargedService.getPaymentType();
                chargedServiceAmountsMap.computeIfPresent(key, (k, amount) -> amount.add(chargedService.getAmount()));
            }
        }

        return new ConfirmationPdfContext(vm.getOptionalText(), vm.getConfirmationTitleType(), vm.getLanguage(),
            registration, vm.getIgnoredChargeableItemIdList(), chargedServiceAmountsMap);
    }

    @SuppressWarnings({"MissingJavadocMethod", "IllegalCatch"})
    @Override
    public byte[] generatePdf(PdfContext context) {
        ConfirmationPdfContext pdfContext = (ConfirmationPdfContext) context;
        pdfContext.setCompany(companyService.getCompanyProfile());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 20, 20, 40, 100);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            ConfirmationHeaderFooter event = new ConfirmationHeaderFooter(messageSource, pdfContext);
            writer.setPageEvent(event);
            writer.setBoxSize("art", new Rectangle(36, 54, 559, 788));

            document.open();
            addMetaData(document, pdfContext);
            generateContent(document, writer, pdfContext);

            document.close();
            writer.flush();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error while creating confirmation pdf", e);
        }
        return null;
    }

    @SuppressWarnings("MultipleStringLiterals")
    private void addMetaData(Document document, ConfirmationPdfContext pdfContext) {
        document.addTitle(getStaticMessage("confirmation", pdfContext.getLocale()));
        document.addSubject(getStaticMessage("confirmation", pdfContext.getLocale()));
        document.addKeywords("");
        document.addAuthor("PCS System");
        document.addCreator("PCS System");
    }

    private String getStaticMessage(String messageKey, Locale locale) {
        return messageSource.getMessage(MESSAGE_SOURCE_PREFIX + messageKey, new Object[]{}, locale);
    }

    private String getFullName(Registration registration, boolean lastNameUppercase, Locale locale) {
        String fullName;
        final String title = registration.getTitle();
        final String firstName = registration.getFirstName();
        final String lastName = registration.getLastName();
        if (locale.getLanguage().equals("hu")) {
            fullName = (title != null ? title + " " : "")
                    + (lastName != null ? (lastNameUppercase ? lastName.toUpperCase() : lastName) + " " : "") + firstName;
        } else {
            fullName = (title != null ? title + " " : "")
                    + firstName
                    + (lastName != null ? " " + (lastNameUppercase ? lastName.toUpperCase() : lastName) : "");
        }

        return fullName;
    }

    private void createContentTitle(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        String title = switch (pdfContext.getConfirmationTitleType()) {
            case CONFIRMATION -> "confirmationCapital";
            case PRO_FORMA_INVOICE -> "proFormaInvoiceCapital";
        };
        Paragraph p = new Paragraph(getStaticMessage(title, pdfContext.getLocale()), PcsPdfFont.H_1);
        p.setAlignment(Element.ALIGN_CENTER);
        p.setSpacingAfter(30);
        document.add(p);
    }

    private void createContentFirstBlock(Document document, PdfWriter writer, ConfirmationPdfContext pdfContext) throws DocumentException {
        final Locale locale = pdfContext.getLocale();
        Registration registration = pdfContext.getRegistration();

        Paragraph p = new Paragraph();
        p.add(new Chunk(getFullName(registration, true, locale), PcsPdfFont.P_SMALL_BOLD));
        p.add(Chunk.NEWLINE);

        if (registration.getWorkplace() != null) {
            p.add(new Chunk(registration.getWorkplace().getName(), PcsPdfFont.P_SMALL_NORMAL));
            p.add(Chunk.NEWLINE);
        }
        if (registration.getCity() != null) {
            p.add(new Chunk(registration.getCity(), PcsPdfFont.P_SMALL_NORMAL));
            p.add(Chunk.NEWLINE);
        }
        if (registration.getStreet() != null) {
            p.add(new Chunk(registration.getStreet(), PcsPdfFont.P_SMALL_NORMAL));
            p.add(Chunk.NEWLINE);
        }
        if (registration.getZipCode() != null) {
            p.add(new Chunk(registration.getZipCode(), PcsPdfFont.P_SMALL_NORMAL));
            p.add(Chunk.NEWLINE);
        }
        if (registration.getCountry() != null) {
            p.add(new Chunk(registration.getCountry().getName(), PcsPdfFont.P_SMALL_NORMAL));
        }
        p.add(Chunk.NEWLINE);
        p.add(Chunk.NEWLINE);
        p.add(new Chunk(getStaticMessage("registrationNumber", locale) + " " + registration.getRegId().toString(), PcsPdfFont.P_SMALL_BOLD));
        PdfPCell cell1 = new PdfPCell(p);

        Image codeQrImage = createQrImage("REG-" + new DecimalFormat("00000").format(registration.getId()));
        //codeQrImage.scaleAbsolute(100, 100);
        PdfPCell cell2 = new PdfPCell(codeQrImage);

        cell1.setBorder(0);
        cell1.setPaddingLeft(0);
        cell1.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell2.setPaddingRight(0);

        PdfPTable table = createTable(2, 100, new float[]{5, 1});
        table.setSpacingAfter(0);
        addTableCell(table, cell1, cell2);
        document.add(table);
    }

    @SuppressWarnings("MultipleStringLiterals")
    private void createContentSecondBlock(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        Locale locale = pdfContext.getLocale();
        final String congressName = getStaticMessage("event", locale) + ": " + pdfContext.getRegistration().getCongress().getName();
        PdfPTable table = createTable(1, 100, new float[]{1});
        PdfPCell cell1 = createCell(new Paragraph(congressName, PcsPdfFont.P_BOLD));
        cell1.setBorder(1);
        addTableCell(table, cell1);
        document.add(table);
        //new row
        final String congressDate = getStaticMessage("date", locale) + ": "
                + pdfContext.getRegistration().getCongress().getStartDate().format(pdfContext.getFormatter()) + " - "
                + pdfContext.getRegistration().getCongress().getEndDate().format(pdfContext.getFormatter());
        final String progNumber = getStaticMessage("progNumber", locale) + ": " + pdfContext.getRegistration().getCongress().getProgramNumber();

        table = createTable(2, 100, new float[]{1, 1});
        table.setSpacingAfter(10);
        cell1 = createCell(createParagraph(congressDate, PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell2 = createCell(createRightParagraph(progNumber, PcsPdfFont.P_SMALL_NORMAL));
        addTableCell(table, cell1, cell2);
        document.add(table);
    }

    @SuppressWarnings("MultipleStringLiterals")
    private void createContentThirdBlock(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        Registration registration = pdfContext.getRegistration();
        Locale locale = pdfContext.getLocale();
        Paragraph p = new Paragraph();
        p.add(new Paragraph(getStaticMessage("dear", locale) + " " + getFullName(registration, false, locale) + "!", PcsPdfFont.P_SMALL_BOLD));
        p.add(new Paragraph(getStaticMessage("thanksMessage", locale) + ":", PcsPdfFont.P_SMALL_NORMAL));
        addEmptyLine(p, 1); // enters
        Paragraph tempParagraph = new Paragraph(getStaticMessage("regards", locale), PcsPdfFont.P_SMALL_NORMAL);
        tempParagraph.setLeading(4);
        tempParagraph.setSpacingAfter(-4);
        p.add(tempParagraph);

        tempParagraph = new Paragraph(registration.getCongress().getContactPerson() + ", " + getStaticMessage("companyName", locale), PcsPdfFont.P_SMALL_NORMAL);
        tempParagraph.setSpacingAfter(-4);
        p.add(tempParagraph);

        tempParagraph = new Paragraph(registration.getCongress().getContactEmail(), PcsPdfFont.P_SMALL_NORMAL);
        tempParagraph.setSpacingAfter(4);
        p.add(tempParagraph);

        PdfPCell cell1 = createCell(p);
        cell1.setBorder(1);
        PdfPTable table = createTable(1, 100, new float[]{1});
        addTableCell(table, cell1);
        document.add(table);
    }

    private void createOrdersTableHeader(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        final Locale locale = pdfContext.getLocale();
        PdfPTable table = createTable(6, 100, new float[]{5, 1, 1, 1, 0.7f, 1});
        table.setSplitRows(true);
        table.setSpacingBefore(10);

        //new row
        PdfPCell cell1 = createCell(createParagraph(getStaticMessage("service", locale), PcsPdfFont.P_SMALL_BOLD));
        PdfPCell cell2 = createCell(createRightParagraph(getStaticMessage("unit", locale), PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell3 = createCell(createRightParagraph(getStaticMessage("unitPrice", locale), PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell4 = createCell(createRightParagraph(getStaticMessage("vatBase", locale), PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell5 = createCell(createRightParagraph(getStaticMessage("vat", locale), PcsPdfFont.P_SMALL_NORMAL));
        PdfPCell cell6 = createCell(createRightParagraph(getStaticMessage("sum", locale), PcsPdfFont.P_SMALL_NORMAL));

        setBorder(1, cell1, cell2, cell3, cell4, cell5, cell6);
        setPaddingBottom(3, cell1, cell2, cell3, cell4, cell5, cell6);

        addTableCell(table, cell1, cell2, cell3, cell4, cell5, cell6);
        document.add(table);
    }

    @SuppressWarnings({"MultipleStringLiterals", "MethodLength"})
    private void createOrdersTable(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        final Registration registration = pdfContext.getRegistration();
        final Locale locale = pdfContext.getLocale();
        final List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationId(registration.getId());
        final List<RoomReservationRegistration> rrrList = rrrRepository.findAllByRegistrationId(registration.getId());
        final List<OrderedOptionalService> oosList = oosRepository.findAllByRegistrationId(registration.getId());

        PdfPTable table = createTable(6, 100, new float[]{5, 1, 1, 1, 0.7f, 1});
        table.setSplitRows(true);

        if (hasAnyNonIgnoredItem(rrtList, pdfContext.getIgnoredChargeableItemIdList())) {
            final PdfPCell cell = createCell(new Paragraph(getStaticMessage("participationFee", locale) + ":", PcsPdfFont.P_SMALL_BOLD));
            cell.setColspan(6);
            table.addCell(cell);

            // Registrations
            rrtList.stream().filter(registrationRegistrationType -> !pdfContext.getIgnoredChargeableItemIdList()
                .contains(registrationRegistrationType.getId())).forEach(rrt -> {
                    addActivePayingGroupItemToSummary(pdfContext.getActivePayingGroups(), rrt.getPayingGroupItem(), rrt);

                    PdfPCell c1 = createCell(createParagraph(rrt.getChargeableItemName(), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c2 = createCell(createRightParagraph(rrt.getAccPeople().toString() + " " + getStaticMessage("person", locale),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(rrt.getRegFee(), rrt.getChargeableItemCurrency()), locale),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(rrt.getChargeableItemPrice(), rrt.getChargeableItemCurrency()),
                            locale), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c5 = createCell(createRightParagraph(rrt.getChargeableItemVAT() + "%", PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(discountService.getPriceWithDiscount(rrt),
                        rrt.getChargeableItemCurrency()), locale) + " " + rrt.getChargeableItemCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));

                    addTableCell(table, c1, c2, c3, c4, c5, c6);

                    if (pdfContext.getCurrency() == null || pdfContext.getCurrency().isEmpty()) {
                        pdfContext.setCurrency(rrt.getChargeableItemCurrency());
                    }
                });
        }

        if (hasAnyNonIgnoredItem(rrrList, pdfContext.getIgnoredChargeableItemIdList())) {
            PdfPCell cell = createCell(new Paragraph(getStaticMessage("reservation", locale) + ":", PcsPdfFont.P_SMALL_BOLD));
            cell.setColspan(6);
            table.addCell(cell);

            // Room reservations
            for (RoomReservationRegistration rrr : rrrList) {
                if (!pdfContext.getIgnoredChargeableItemIdList().contains(rrr.getId())) {
                    final PayingGroupItem payingGroupItem = rrr.getPayingGroupItem();
                    final Room room = rrr.getRoomReservation().getRoom();
                    final Hotel hotel = room.getCongressHotel().getHotel();
                    final LocalDate arrivalDate = rrr.getRoomReservation().getArrivalDate();
                    final LocalDate departureDate = rrr.getRoomReservation().getDepartureDate();
                    addActivePayingGroupItemToSummary(pdfContext.getActivePayingGroups(), payingGroupItem, rrr);

                    PdfPCell c1 = createCell(createParagraph(hotel.getName(), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c2 = createCell(createRightParagraph(ChronoUnit.DAYS.between(arrivalDate, departureDate) + " " + getStaticMessage("night", locale),
                        PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(rrr.getSharedPricePerNight(), rrr.getChargeableItemCurrency()),
                        pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(rrr.getChargeableItemPrice(), rrr.getChargeableItemCurrency()),
                        pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c5 = createCell(createRightParagraph(rrr.getChargeableItemVAT().toString() + "%", PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(discountService.getPriceWithDiscount(rrr),
                        rrr.getChargeableItemCurrency()), pdfContext.getLocale()) + " " + rrr.getChargeableItemCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                    addTableCell(table, c1, c2, c3, c4, c5, c6);

                    if (!rrr.getRoomMatesWithoutRegistration(registration, locale).isEmpty()) {
                        final Paragraph roommate = new Paragraph("(" + getStaticMessage("roommate", locale)
                            + rrr.getRoomMatesWithoutRegistration(registration, locale) + ")", PcsPdfFont.P_MINIATURE_NORMAL);
                        cell = createCell(roommate);
                        cell.setColspan(6);
                        table.addCell(cell);
                    }

                    String hotelAddress;
                    if (hotel.getCity().isEmpty()) {
                        hotelAddress = hotel.getZipCode() + " " + hotel.getStreet();
                    } else {
                        hotelAddress = hotel.getZipCode() + " " + hotel.getCity() + ", " + hotel.getStreet();
                    }

                    final Paragraph hotelAddressParagraph = new Paragraph(hotelAddress, PcsPdfFont.P_MINIATURE_NORMAL);
                    cell = createCell(hotelAddressParagraph);
                    cell.setColspan(6);
                    table.addCell(cell);

                    String arrDeptDate;
                    if (arrivalDate != null && departureDate != null) {
                        arrDeptDate = arrivalDate.format(pdfContext.getFormatter()) + " / " + departureDate.format(pdfContext.getFormatter()) + " " + room.getRoomType();
                    } else if (arrivalDate != null) {
                        arrDeptDate = arrivalDate.format(pdfContext.getFormatter()) + " / - " + room.getRoomType();
                    } else if (departureDate != null) {
                        arrDeptDate = "- / " + departureDate.format(pdfContext.getFormatter()) + " " + room.getRoomType();
                    } else {
                        arrDeptDate = "- / - " + room.getRoomType();
                    }

                    cell = createCell(new Paragraph(arrDeptDate, PcsPdfFont.P_MINIATURE_NORMAL));
                    cell.setColspan(7);
                    table.addCell(cell);

                    if (pdfContext.getCurrency() == null || pdfContext.getCurrency().isEmpty()) {
                        pdfContext.setCurrency(room.getCurrency().getCurrency());
                    }
                }
            }
        }

        if (hasAnyNonIgnoredItem(oosList, pdfContext.getIgnoredChargeableItemIdList())) {
            PdfPCell cell = createCell(new Paragraph(getStaticMessage("programs", locale) + ":", PcsPdfFont.P_SMALL_BOLD));
            cell.setColspan(6);
            table.addCell(cell);
            oosList.stream().filter(orderedOptionalService -> !pdfContext.getIgnoredChargeableItemIdList().contains(orderedOptionalService.getId()))
                .forEach(oos -> {
                    OptionalService optionalService = oos.getOptionalService();
                    addActivePayingGroupItemToSummary(pdfContext.getActivePayingGroups(), oos.getPayingGroupItem(), oos);

                    PdfPCell c1 = createCell(createParagraph(optionalService.getName(), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c2 = createCell(createRightParagraph(oos.getParticipant() + " " + getStaticMessage("person", locale), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c3 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(optionalService.getPrice(),
                        oos.getChargeableItemCurrency()), pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c4 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(oos.getChargeableItemPrice(),
                        oos.getChargeableItemCurrency()), pdfContext.getLocale()), PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c5 = createCell(createRightParagraph(oos.getChargeableItemVAT() + "%", PcsPdfFont.P_MINIATURE_NORMAL));
                    PdfPCell c6 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(discountService.getPriceWithDiscount(oos),
                        oos.getChargeableItemCurrency()), pdfContext.getLocale()) + " " + oos.getChargeableItemCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                    addTableCell(table, c1, c2, c3, c4, c5, c6);

                    if (optionalService.getStartDate() != null || optionalService.getEndDate() != null || optionalService.getName() != null) {
                        String optServiceInfo;
                        if (optionalService.getStartDate() != null && optionalService.getEndDate() != null) {
                            if (!optionalService.getStartDate().equals(optionalService.getEndDate())) {
                                optServiceInfo = optionalService.getStartDate().format(pdfContext.getFormatter()) + " / "
                                    + optionalService.getEndDate().format(pdfContext.getFormatter());
                            } else {
                                optServiceInfo = optionalService.getStartDate().format(pdfContext.getFormatter());
                            }
                        } else if (optionalService.getStartDate() != null) {
                            optServiceInfo = optionalService.getStartDate().format(pdfContext.getFormatter());
                        } else if (optionalService.getEndDate() != null) {
                            optServiceInfo = "- / " + optionalService.getEndDate().format(pdfContext.getFormatter());
                        } else {
                            optServiceInfo = optionalService.getName();
                        }

                        PdfPCell c = createCell(new Paragraph(optServiceInfo, PcsPdfFont.P_MINIATURE_NORMAL));
                        c.setColspan(6);
                        table.addCell(c);
                    }

                    if (pdfContext.getCurrency() == null || pdfContext.getCurrency().isEmpty()) {
                        pdfContext.setCurrency(optionalService.getCurrency().getCurrency());
                    }
                });
        }
        document.add(table);
    }

    private void generateContent(Document document, PdfWriter writer, ConfirmationPdfContext pdfContext) throws DocumentException {

        createContentTitle(document, pdfContext);
        createContentFirstBlock(document, writer, pdfContext);
        createContentSecondBlock(document, pdfContext);
        createContentThirdBlock(document, pdfContext);
        createOrdersTableHeader(document, pdfContext);
        createOrdersTable(document, pdfContext);
        createPaymentsTable(document, pdfContext);
        createGrandTotalTable(document, pdfContext);
        createOptionalTextTable(document, pdfContext);
    }

    private void createGrandTotalTable(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        final List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationId(pdfContext.getRegistration().getId());
        final List<RoomReservationRegistration> rrrList = rrrRepository.findAllByRegistrationId(pdfContext.getRegistration().getId());
        final List<OrderedOptionalService> oosList = oosRepository.findAllByRegistrationId(pdfContext.getRegistration().getId());
        final Locale locale = pdfContext.getLocale();

        PdfPTable table = createTable(2, 100, new float[]{2, 1});
        table.setSpacingBefore(10);
        BigDecimal sumChargedService = pdfContext.getChargedServiceAmountsMap().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal sumAmount = sumPricesWithDiscounts(pdfContext.getIgnoredChargeableItemIdList(), rrtList, rrrList, oosList).add(sumChargedService.negate());
        int total = sumAmount.compareTo(BigDecimal.ZERO);

        String titleKey = "balance";
        if (total < 0) {
            titleKey = "credit";
        } else if (total > 0) {
            titleKey = "payable";
        }
        PdfPCell cell1 = createCell(createParagraph(getStaticMessage(titleKey, locale), PcsPdfFont.P_BOLD));
        PdfPCell cell2 = createCell(createRightParagraph(formatter.format(formatter.formatByCurrency(sumAmount, pdfContext.getCurrency()), locale)
            + " " + pdfContext.getCurrency(), PcsPdfFont.P_BOLD));

        cell1.setBorder(1);
        cell2.setBorder(1);
        addTableCell(table, cell1, cell2);
        document.add(table);

        Paragraph preface = new Paragraph();
        Map<String, List<PayingGroupItem>> activePayingGroupItemsByPayingGroupName = getActivePayingGroupItemsByPayingGroupName(pdfContext.getActivePayingGroups());
        for (String key : activePayingGroupItemsByPayingGroupName.keySet()) {
            Paragraph tempParagraph = new Paragraph(getStaticMessage("payedBy", locale) + " " + key, PcsPdfFont.P_MINIATURE_BOLD);
            preface.add(tempParagraph);

            List<PayingGroupItem> payingGroupItemList = activePayingGroupItemsByPayingGroupName.get(key);
            for (PayingGroupItem payingGroupItem : payingGroupItemList) {
                BigDecimal discount = pdfContext.getActivePayingGroups().get(payingGroupItem);
                final String payingGroupCurrency = payingGroupItem.getPayingGroup().getCurrency().getCurrency();
                String prePayedValue = formatter.format(formatter.formatByCurrency(discount, payingGroupCurrency), locale) + " " + payingGroupCurrency;
                tempParagraph = new Paragraph(payingGroupItem.getName() + " (" + prePayedValue + ")", PcsPdfFont.P_MINIATURE_NORMAL);
                preface.add(tempParagraph);
            }
        }
        document.add(preface);

    }

    private void createPaymentsTable(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        final List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationId(pdfContext.getRegistration().getId());
        final List<RoomReservationRegistration> rrrList = rrrRepository.findAllByRegistrationId(pdfContext.getRegistration().getId());
        final List<OrderedOptionalService> oosList = oosRepository.findAllByRegistrationId(pdfContext.getRegistration().getId());
        final Locale locale = pdfContext.getLocale();

        BigDecimal chargedServiceAmount = pdfContext.getChargedServiceAmountsMap().values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (chargedServiceAmount.compareTo(BigDecimal.ZERO) != 0) {

            PdfPTable table = createTable(2, 100, new float[]{2, 1});
            table.setSpacingBefore(10);
            PdfPCell cell1 = createCell(createParagraph(getStaticMessage("servicesTotal", locale), PcsPdfFont.P_SMALL_BOLD));
            PdfPCell cell2 = createCell(createRightParagraph(formatter.format(sumPricesWithDiscounts(pdfContext.getIgnoredChargeableItemIdList(),
                    rrtList, rrrList, oosList), locale) + " " + pdfContext.getCurrency(), PcsPdfFont.P_SMALL_NORMAL));

            cell1.setBorder(1);
            cell2.setBorder(1);
            addTableCell(table, cell1, cell2);

            cell1 = createCell(new Paragraph(getStaticMessage("payments", locale) + ":", PcsPdfFont.P_SMALL_BOLD));
            cell1.setColspan(2);
            addTableCell(table, cell1);

            if (pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.REGISTRATION).compareTo(BigDecimal.ZERO) != 0) {
                cell1 = createCell(createParagraph(getStaticMessage("participationFee", locale), PcsPdfFont.P_MINIATURE_NORMAL));
                cell2 = createCell(createRightParagraph("-" + formatter.format(pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.REGISTRATION), locale)
                    + " " + pdfContext.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                addTableCell(table, cell1, cell2);
            }

            if (pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.HOTEL).compareTo(BigDecimal.ZERO) != 0) {
                cell1 = createCell(createParagraph(getStaticMessage("reservation", locale), PcsPdfFont.P_MINIATURE_NORMAL));
                cell2 = createCell(createRightParagraph("-" + formatter.format(pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.HOTEL), locale)
                    + " " + pdfContext.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                addTableCell(table, cell1, cell2);
            }

            if (pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.OPTIONAL_SERVICE).compareTo(BigDecimal.ZERO) != 0) {
                cell1 = createCell(createParagraph(getStaticMessage("programs", locale), PcsPdfFont.P_MINIATURE_NORMAL));
                cell2 = createCell(createRightParagraph("-" + formatter.format(pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.OPTIONAL_SERVICE), locale)
                    + " " + pdfContext.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                addTableCell(table, cell1, cell2);
            }

            if (pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.MISCELLANEOUS).compareTo(BigDecimal.ZERO) != 0) {
                cell1 = createCell(createParagraph(getStaticMessage("miscellaneous", locale), PcsPdfFont.P_MINIATURE_NORMAL));
                cell2 = createCell(createRightParagraph("-" + formatter.format(pdfContext.getChargedServiceAmountsMap().get(ChargeableItemType.MISCELLANEOUS), locale)
                    + " " + pdfContext.getCurrency(), PcsPdfFont.P_MINIATURE_NORMAL));
                addTableCell(table, cell1, cell2);
            }
            document.add(table);
        }
    }

    private void createOptionalTextTable(Document document, ConfirmationPdfContext pdfContext) throws DocumentException {
        if (!StringUtils.hasText(pdfContext.getOptionalText())) {
            return;
        }
        PdfPTable table = createTable(1, 100, new float[]{1});
        table.setSpacingBefore(10);
        PdfPCell cell1 = createCell(new Paragraph(pdfContext.getOptionalText(), PcsPdfFont.P_MINIATURE_BOLD));
        cell1.setBorder(1);
        cell1.setPaddingTop(5);
        addTableCell(table, cell1);
        document.add(table);
    }

    private String getCongressDate(Registration registration, ConfirmationPdfContext pdfContext) {
        String congressDate = "";
        if (registration.getCongress().getStartDate() != null && registration.getCongress().getEndDate() != null) {
            congressDate = registration.getCongress().getStartDate().format(pdfContext.getFormatter()) + " - "
                + registration.getCongress().getEndDate().format(pdfContext.getFormatter());
        } else if (registration.getCongress().getStartDate() != null) {
            congressDate = registration.getCongress().getStartDate().format(pdfContext.getFormatter());
        } else if (registration.getCongress().getEndDate() != null) {
            congressDate = registration.getCongress().getEndDate().format(pdfContext.getFormatter());
        }
        return congressDate;
    }

    private void addActivePayingGroupItemToSummary(Map<PayingGroupItem, BigDecimal> activePayingGroups, PayingGroupItem payingGroupItem, ChargeableItem chargeableItem) {
        BigDecimal priceWithDiscount;
        if (payingGroupItem == null) {
            return;
        }

        if (chargeableItem instanceof RoomReservationRegistration) {
            priceWithDiscount = discountService.getRoomReservationPriceWithDiscount(payingGroupItem, (RoomReservationRegistration) chargeableItem);
        } else {
            priceWithDiscount = discountService.getPriceWithDiscount(payingGroupItem, chargeableItem.getChargeableItemPrice(), priceService.getScale(chargeableItem));
        }
        BigDecimal discount = chargeableItem.getChargeableItemPrice().subtract(priceWithDiscount);

        activePayingGroups.merge(payingGroupItem, discount, (a, b) -> b.add(a));
    }

    private Map<String, List<PayingGroupItem>> getActivePayingGroupItemsByPayingGroupName(Map<PayingGroupItem, BigDecimal> activePayingGroups) {
        Map<String, List<PayingGroupItem>> retVal = new HashMap<>();
        for (PayingGroupItem payingGroupItem : activePayingGroups.keySet()) {
            String groupName = payingGroupItem.getPayingGroup().getName();
            List<PayingGroupItem> payingGroupItems = retVal.get(groupName) == null ? new ArrayList<>() : retVal.get(groupName);
            payingGroupItems.add(payingGroupItem);
            retVal.put(groupName, payingGroupItems);
        }

        return retVal;
    }

}
