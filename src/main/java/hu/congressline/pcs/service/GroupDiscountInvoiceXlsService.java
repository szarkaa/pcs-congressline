package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import hu.congressline.pcs.domain.ChargeableItem;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.GroupDiscountInvoiceHistory;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.domain.PayingGroup;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RoomReservationRegistration;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.repository.GroupDiscountInvoiceHistoryRepository;
import hu.congressline.pcs.repository.OrderedOptionalServiceRepository;
import hu.congressline.pcs.repository.PayingGroupRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RoomReservationRegistrationRepository;
import hu.congressline.pcs.service.dto.GroupDiscountItemDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class GroupDiscountInvoiceXlsService extends XlsReportService {

    private static final String[] TITLES = new String[]{"Registration ID", "Name", "Registration Support", "Accomodation Support", "Ordered Services Support"};

    private final DiscountService discountService;
    private final GroupDiscountInvoiceHistoryRepository groupDiscountInvoiceHistoryRepository;
    private final CongressRepository congressRepository;
    private final GroupDiscountItemService groupDiscountItemService;
    private final PayingGroupRepository payingGroupRepository;
    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final RoomReservationRegistrationRepository rrrRepository;
    private final OrderedOptionalServiceRepository oosRepository;

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Transactional(readOnly = true)
    public byte[] downloadInvoiceReportXls(String meetingCode, Long payingGroupId, String chargeableItemType) throws IOException {
        final List<GroupDiscountItemDTO> resultList = groupDiscountItemService.findAll(meetingCode, payingGroupId, chargeableItemType);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg no.", 100);
        columns.put("Family name", 200);
        columns.put("First name", 200);
        columns.put("Item type", 200);
        columns.put("Paying group", 200);
        columns.put("Paying group item", 200);
        columns.put("Amount", 100);
        columns.put("Date of payment", 200);
        columns.put("Invoice number", 200);
        columns.put("Hotel name", 200);
        columns.put("Room type", 200);
        columns.put("Room mates", 200);

        Congress congress = congressRepository.findOneByMeetingCode(meetingCode).orElse(null);
        final XSSFSheet sheet = createXlsxTab(workbook, "Group invoice report", null, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (GroupDiscountItemDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getLastName());
            addCell(row, wrappingCellStyle, 2, dto.getFirstName());
            addCell(row, wrappingCellStyle, 3, dto.getChargeableItemType().toString());
            addCell(row, wrappingCellStyle, 4, dto.getPayingGroup().getName());
            addCell(row, wrappingCellStyle, 5, dto.getPayingGroupItemName());
            addCell(row, wrappingCellStyle, 6, dto.getAmount());
            addCell(row, wrappingCellStyle, 7, dto.getDateOfPayment());
            addCell(row, wrappingCellStyle, 8, dto.getInvoiceNumber());
            addCell(row, wrappingCellStyle, 9, dto.getHotelName());
            addCell(row, wrappingCellStyle, 10, dto.getRoomType());
            addCell(row, wrappingCellStyle, 11, dto.getRoomMates());

            rowIndex++;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the room reservation by participants report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings({"MissingJavadocMethod", "MissingSwitchDefault", "MultipleStringLiterals"})
    @Transactional(readOnly = true)
    public byte[] downloadInvoiceReportXls(InvoicePayingGroup invoice) throws IOException {
        PayingGroup payingGroup = invoice.getPayingGroup();
        final List<ChargeableItem> registrationTypeList = new ArrayList<>();
        final List<ChargeableItem> roomReservationList = new ArrayList<>();
        final List<ChargeableItem> optionalServiceList = new ArrayList<>();

        final List<GroupDiscountInvoiceHistory> invoiceHistories = groupDiscountInvoiceHistoryRepository.findAllByInvoice(invoice.getInvoice());
        invoiceHistories.forEach(h -> {
            switch (h.getChargeableItem().getChargeableItemType()) {
                case REGISTRATION -> registrationTypeList.add(h.getChargeableItem());
                case HOTEL -> roomReservationList.add(h.getChargeableItem());
                case OPTIONAL_SERVICE -> optionalServiceList.add(h.getChargeableItem());
            }
        });

        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        Arrays.stream(TITLES).forEach(title -> {
            columns.put(title, 200);
        });

        final XSSFSheet sheet = createXlsxTab(workbook, "Group invoice report", payingGroup.getName(), payingGroup.getCongress().getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        final Map<Integer, GroupDiscountInvoiceDetails> groupDiscountInvoiceDetails =
                createGroupDiscountInvoiceDetails(registrationTypeList, roomReservationList, optionalServiceList);

        List<GroupDiscountInvoiceDetails> resultList = new ArrayList<>(groupDiscountInvoiceDetails.values());
        resultList.sort(Comparator.comparing(GroupDiscountInvoiceDetails::getName));
        AtomicInteger rowIndex = new AtomicInteger(4);
        resultList.forEach(details -> {
            final XSSFRow row = sheet.createRow(rowIndex.get());
            addCell(row, wrappingCellStyle, 0, details.getRegistrationId());
            addCell(row, wrappingCellStyle, 1, details.getName());
            addCell(row, wrappingCellStyle, 2, details.getRegistrationDiscount().intValue());
            addCell(row, wrappingCellStyle, 3, details.getAccomodationDiscount().intValue());
            addCell(row, wrappingCellStyle, 4, details.getOrderedServiceDiscount().intValue());
            rowIndex.getAndIncrement();
        });

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the group invoice report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals", "MissingSwitchDefault"})
    @Transactional(readOnly = true)
    public byte[] downloadProFormaInvoiceReportXls(String meetingCode, Long payingGroupId, String chargeableItemType) throws IOException {
        final List<GroupDiscountItemDTO> gdiList = groupDiscountItemService.findAll(meetingCode, payingGroupId, chargeableItemType);
        PayingGroup payingGroup = payingGroupRepository.findById(payingGroupId)
                .orElseThrow(() -> new IllegalArgumentException("Paying group not found by id: " + payingGroupId));
        final List<Long> registrationTypeIdList = new ArrayList<>();
        final List<Long> roomReservationIdList = new ArrayList<>();
        final List<Long> optionalServiceIdList = new ArrayList<>();

        gdiList.forEach(dto -> {
            switch (dto.getChargeableItemType()) {
                case REGISTRATION -> registrationTypeIdList.add(Long.valueOf(dto.getChargeableItemId()));
                case HOTEL -> roomReservationIdList.add(Long.valueOf(dto.getChargeableItemId()));
                case OPTIONAL_SERVICE -> optionalServiceIdList.add(Long.valueOf(dto.getChargeableItemId()));
            }
        });

        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        Arrays.stream(TITLES).forEach(title -> {
            columns.put(title, 200);
        });

        final XSSFSheet sheet = createXlsxTab(workbook, "Group pro forma invoice report", payingGroup.getName(), payingGroup.getCongress().getName(),
                getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        final Map<Integer, GroupDiscountInvoiceDetails> groupDiscountInvoiceDetails =
                createGroupDiscountInvoiceDetails(rrtRepository.findAllByIdIn(registrationTypeIdList), rrrRepository.findAllByIdIn(roomReservationIdList),
                        oosRepository.findAllByIdIn(optionalServiceIdList));

        List<GroupDiscountInvoiceDetails> resultList = new ArrayList<>(groupDiscountInvoiceDetails.values());
        resultList.sort(Comparator.comparing(GroupDiscountInvoiceDetails::getName));
        AtomicInteger rowIndex = new AtomicInteger(4);
        resultList.forEach(details -> {
            final XSSFRow row = sheet.createRow(rowIndex.get());
            addCell(row, wrappingCellStyle, 0, details.getRegistrationId());
            addCell(row, wrappingCellStyle, 1, details.getName());
            addCell(row, wrappingCellStyle, 2, details.getRegistrationDiscount().intValue());
            addCell(row, wrappingCellStyle, 3, details.getAccomodationDiscount().intValue());
            addCell(row, wrappingCellStyle, 4, details.getOrderedServiceDiscount().intValue());
            rowIndex.getAndIncrement();
        });

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the group invoice report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings("MultipleStringLiterals")
    private Map<Integer, GroupDiscountInvoiceDetails> createGroupDiscountInvoiceDetails(List<? extends ChargeableItem> registrationTypeList,
                                                                                            List<? extends ChargeableItem> roomReservationList,
                                                                                            List<? extends ChargeableItem> optionalServiceList) {
        final Map<Integer, GroupDiscountInvoiceDetails> map = new HashMap<Integer, GroupDiscountInvoiceDetails>();
        for (ChargeableItem item : registrationTypeList) {
            RegistrationRegistrationType rrt = (RegistrationRegistrationType) item;
            GroupDiscountInvoiceDetails details = map.get(rrt.getRegistration().getRegId());
            if (details == null) {
                final Registration registration = rrt.getRegistration();
                details = new GroupDiscountInvoiceDetails(registration.getRegId(), registration.getLastName() + ", " + registration.getFirstName());
                map.put(registration.getRegId(), details);
            }
            details.addRegistrationDiscount(discountService.getAmountOfDiscount(rrt));
        }

        for (ChargeableItem item : roomReservationList) {
            RoomReservationRegistration rrr = (RoomReservationRegistration) item;
            GroupDiscountInvoiceDetails details = map.get(rrr.getRegistration().getRegId());
            if (details == null) {
                final Registration registration = rrr.getRegistration();
                details = new GroupDiscountInvoiceDetails(registration.getRegId(), registration.getLastName() + ", " + registration.getFirstName());
                map.put(registration.getRegId(), details);

            }
            details.addAccomodationDiscount(discountService.getAmountOfDiscount(rrr));
        }

        for (ChargeableItem item : optionalServiceList) {
            OrderedOptionalService oos = (OrderedOptionalService) item;
            GroupDiscountInvoiceDetails details = map.get(oos.getRegistration().getRegId());
            if (details == null) {
                final Registration registration = oos.getRegistration();
                details = new GroupDiscountInvoiceDetails(registration.getRegId(), registration.getLastName() + ", " + registration.getFirstName());
                map.put(registration.getRegId(), details);
            }
            details.addOrderedServiceDiscount(discountService.getAmountOfDiscount(oos));
        }

        return map;
    }

    @Getter
    private static class GroupDiscountInvoiceDetails {
        private final Integer registrationId;
        private final String name;
        private BigDecimal accomodationDiscount = BigDecimal.ZERO;
        private BigDecimal registrationDiscount = BigDecimal.ZERO;
        private BigDecimal orderedServiceDiscount = BigDecimal.ZERO;

        public GroupDiscountInvoiceDetails(Integer registrationId, String name) {
            this.registrationId = registrationId;
            this.name = name;
        }

        public void addAccomodationDiscount(BigDecimal discount) {
            accomodationDiscount = accomodationDiscount.add(discount);
        }

        public void addRegistrationDiscount(BigDecimal discount) {
            registrationDiscount = registrationDiscount.add(discount);
        }

        public void addOrderedServiceDiscount(BigDecimal discount) {
            orderedServiceDiscount = orderedServiceDiscount.add(discount);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            GroupDiscountInvoiceDetails that = (GroupDiscountInvoiceDetails) o;
            return Objects.equals(getRegistrationId(), that.getRegistrationId());
        }

        @Override
        public int hashCode() {

            return Objects.hash(getRegistrationId());
        }
    }
}
