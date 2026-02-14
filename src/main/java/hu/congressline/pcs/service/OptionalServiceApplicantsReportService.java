package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.service.dto.OptionalServiceApplicantsDTO;
import hu.congressline.pcs.service.util.ConverterUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OptionalServiceApplicantsReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private OptionalServiceService service;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<OptionalServiceApplicantsDTO> findAll(Set<Long> optionalServiceIds) {
        log.debug("Request to get all OptionalServiceApplicantDTOs");

        final Query query = entityManager.createNativeQuery(composeQuery(optionalServiceIds));
        List<?> result = query.getResultList();
        List<OptionalServiceApplicantsDTO> rrbpList = new ArrayList<>();
        for (Object item : result) {
            OptionalServiceApplicantsDTO dto = getBeanFromRow((Object[]) item);
            rrbpList.add(dto);
        }
        return rrbpList;
    }

    protected OptionalServiceApplicantsDTO getBeanFromRow(Object[] row) {
        OptionalServiceApplicantsDTO bean = new OptionalServiceApplicantsDTO();
        bean.setId((Long) row[0]);
        bean.setRegId((Integer) row[1]);
        bean.setLastName((String) row[2]);
        bean.setFirstName((String) row[3]);
        bean.setNumOfApplicants((Integer) row[4]);
        bean.setCountry((String) row[5]);
        bean.setCurrency((String) row[6]);
        bean.setOrderedPrice(ConverterUtil.getBigDecimalValue(row[7]));
        bean.setPaid(ConverterUtil.getBigDecimalValue(row[8]));
        bean.setHotel((String) row[9]);
        if (row[10] != null) {
            bean.setGroupCost(ConverterUtil.getBigDecimalValue(row[10]).setScale(2, RoundingMode.HALF_UP));
        } else {
            bean.setGroupCost(BigDecimal.ZERO);
        }
        if (row[11] != null) {
            bean.setPaidByGroup(ConverterUtil.getBigDecimalValue(row[11]).setScale(2, RoundingMode.HALF_UP));
        } else {
            bean.setPaidByGroup(BigDecimal.ZERO);
        }
        bean.setPayingGroupName((String) row[12]);
        return bean;
    }

    protected String composeQuery(Set<Long> optionalServiceIds) {
        return "select oos.id e0, r.reg_id e1,\n"
                + "r.last_name e2,\n"
                + "r.first_name e3,\n"
                + "oos.participant e4,\n"
                + "c.name e5,\n"
                + "curr.currency e6,\n"
                + "(os.price * oos.participant) e7,\n"
                + "(select sum(cs.amount) from charged_service cs where cs.chargeable_item_id = oos.id) e8,\n"
                + "(select group_concat(distinct h.name separator ', ') from room_reservation_registration rrr\n"
                + "join room_reservation rr on rrr.room_reservation_id = rr.id\n"
                + "join room rm on rm.id = rr.room_id\n"
                + "join congress_hotel ch on rm.congress_hotel_id = ch.id\n"
                + "join hotel h on ch.hotel_id = h.id\n"
                + "where rrr.registration_id = r.id) e9,\n"
                + "(if (pgi.amount_value is not null, pgi.amount_value, pgi.amount_percentage / 100 * os.price * oos.participant)) e10,\n"
                + "(if (ci.date_of_group_payment is not null, if (pgi.amount_value is not null, pgi.amount_value, \n"
                + "pgi.amount_percentage / 100 * os.price * oos.participant), 0)) e11,\n"
                + "pg.name e12\n"
                + "from ordered_optional_service oos\n"
                + "join chargeable_item ci on ci.id = oos.id\n"
                + "join registration r on oos.registration_id = r.id\n"
                + "join optional_service os on oos.optional_service_id = os.id\n"
                + "join currency curr on os.currency_id = curr.id\n"
                + "left join country c on r.country_id = c.id\n"
                + "left join paying_group_item pgi on pgi.id = oos.paying_group_item_id\n"
                + "left join paying_group pg on pg.id = pgi.paying_group_id\n"
                + "where oos.optional_service_id in (" + optionalServiceIds.stream().map(String::valueOf).collect(Collectors.joining(",")) + ")\n"
                + "order by r.last_name, r.first_name";
    }

    @SuppressWarnings({"MissingJavadocMethod", "MethodLength"})
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Set<Long> optionalServiceIds) throws IOException {
        final List<OptionalService> optionalServices = service.findAllByIds(optionalServiceIds);
        final List<OptionalServiceApplicantsDTO> resultList = findAll(optionalServiceIds);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg no.", 100);
        columns.put("Family name", 200);
        columns.put("First name", 200);
        columns.put("Applicants", 100);
        columns.put("Country", 100);
        columns.put("Currency", 100);
        columns.put("Ordered price", 100);
        columns.put("Paid", 100);
        columns.put("Hotel", 100);
        columns.put("Paying Group", 100);
        columns.put("Group cost", 100);
        columns.put("Paid by group", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Optional services by applicants",
            optionalServices.stream().map(OptionalService::getName).collect(Collectors.joining(",")),
            optionalServices.stream().findFirst().map(optionalService -> optionalService.getCongress().getName()).orElse(""),
                getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        int totalApplicants = 0;
        BigDecimal totalOrderedPrice = BigDecimal.ZERO;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalGroupCost = BigDecimal.ZERO;
        BigDecimal totalPaidByGroup = BigDecimal.ZERO;
        for (OptionalServiceApplicantsDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getLastName());
            addCell(row, wrappingCellStyle, 2, dto.getFirstName());
            addCell(row, wrappingCellStyle, 3, dto.getNumOfApplicants());
            addCell(row, wrappingCellStyle, 4, dto.getCountry());
            addCell(row, wrappingCellStyle, 5, dto.getCurrency());
            addCell(row, wrappingCellStyle, 6, dto.getOrderedPrice());
            addCell(row, wrappingCellStyle, 7, dto.getPaid());
            addCell(row, wrappingCellStyle, 8, dto.getHotel());
            addCell(row, wrappingCellStyle, 9, dto.getPayingGroupName());
            addCell(row, wrappingCellStyle, 10, dto.getGroupCost());
            addCell(row, wrappingCellStyle, 11, dto.getPaidByGroup());
            totalApplicants = totalApplicants + dto.getNumOfApplicants();
            totalOrderedPrice = totalOrderedPrice.add(dto.getOrderedPrice());
            totalPaid = totalPaid.add(dto.getPaid());
            totalGroupCost = totalGroupCost.add(dto.getGroupCost());
            totalPaidByGroup = totalPaidByGroup.add(dto.getPaidByGroup());
            rowIndex++;
        }

        // total row
        final XSSFRow row = sheet.createRow(rowIndex++);
        final XSSFCellStyle totalRowStyle = getTotalRowStyle(workbook);
        addCell(row, totalRowStyle, 0, "");
        addCell(row, totalRowStyle, 1, "");
        addCell(row, totalRowStyle, 2, "");
        addCell(row, totalRowStyle, 3, totalApplicants);
        addCell(row, totalRowStyle, 4, "");
        addCell(row, totalRowStyle, 5, "");
        addCell(row, totalRowStyle, 6, totalOrderedPrice);
        addCell(row, totalRowStyle, 7, totalPaid);
        addCell(row, totalRowStyle, 8, "");
        addCell(row, totalRowStyle, 9, "");
        addCell(row, totalRowStyle, 10, totalGroupCost);
        addCell(row, totalRowStyle, 11, totalPaidByGroup);

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the optional service by applicants report XLSX file", e);
            throw e;
        }
    }

}
