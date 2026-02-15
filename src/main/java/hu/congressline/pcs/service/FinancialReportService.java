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
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.dto.FinancialReportDTO;
import hu.congressline.pcs.web.rest.vm.FinancialReportVM;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class FinancialReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<FinancialReportDTO> findAll(FinancialReportVM filter) {
        log.debug("Request to get all FinancialReportDTO");
        Map<String, FinancialReportDTO> financialReportMap = new HashMap<>();
        final Congress congress = congressService.getById(Long.valueOf(filter.getCongressId()));

        // from registrations
        Query query = entityManager.createNativeQuery(composeRegistrationQuery(congress));
        List result = query.getResultList();
        for (Object item : result) {
            FinancialReportDTO dto = getDTOFromRegistrationRow((Object[]) item);
            financialReportMap.put("" + dto.getId(), dto);
        }

        // from rrt
        query = entityManager.createNativeQuery(composeRRTFinancialQuery(congress));
        result = query.getResultList();
        for (Object item : result) {
            getDataFromFinancialRows(financialReportMap, (Object[]) item);
        }

        // from rrr
        query = entityManager.createNativeQuery(composeRRRFinancialQuery(congress));
        result = query.getResultList();
        for (Object item : result) {
            getDataFromFinancialRows(financialReportMap, (Object[]) item);
        }

        // from oos
        query = entityManager.createNativeQuery(composeOOSFinancialQuery(congress));
        result = query.getResultList();
        for (Object item : result) {
            getDataFromFinancialRows(financialReportMap, (Object[]) item);
        }

        List<FinancialReportDTO> returnList = new ArrayList<>(financialReportMap.values().stream()
                .filter(filter.getParticipantsToPay() ? r -> r.getPersonToPay().compareTo(BigDecimal.ZERO) != 0 : r -> true)
                .sorted(Comparator.comparingInt(FinancialReportDTO::getRegId)).toList());

        returnList.forEach(dto -> {
            String[] payingGroupNames = dto.getPayingGroupName().split(",");
            Set<String> set = new HashSet<>(Arrays.asList(payingGroupNames));
            set.remove("");
            dto.setPayingGroupName(String.join(", ", set));
        });

        returnList.sort(Comparator.comparing(FinancialReportDTO::getName));
        return returnList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress, List<FinancialReportDTO> resultList) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = createColumns();

        final XSSFSheet sheet = createXlsxTab(workbook, "Financial report", null, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        BigDecimal totalPersonCost = BigDecimal.ZERO;
        BigDecimal totalPersonPaid = BigDecimal.ZERO;
        BigDecimal totalPersonToPay = BigDecimal.ZERO;
        BigDecimal totalGroupCost = BigDecimal.ZERO;
        BigDecimal totalGroupPaid = BigDecimal.ZERO;
        BigDecimal totalGroupToPay = BigDecimal.ZERO;
        BigDecimal totalTotalCost = BigDecimal.ZERO;
        BigDecimal totalTotalPaid = BigDecimal.ZERO;
        BigDecimal totalTotalToPay = BigDecimal.ZERO;

        for (FinancialReportDTO dto : resultList) {
            createRow(sheet, wrappingCellStyle, rowIndex, dto);
            totalPersonCost = totalPersonCost.add(dto.getPersonCost());
            totalPersonPaid = totalPersonPaid.add(dto.getPersonPaid());
            totalPersonToPay = totalPersonToPay.add(dto.getPersonToPay());
            totalGroupCost = totalGroupCost.add(dto.getGroupCost());
            totalGroupPaid = totalGroupPaid.add(dto.getGroupPaid());
            totalGroupToPay = totalGroupToPay.add(dto.getGroupToPay());
            totalTotalCost = totalTotalCost.add(dto.getTotalCost());
            totalTotalPaid = totalTotalPaid.add(dto.getTotalPaid());
            totalTotalToPay = totalTotalToPay.add(dto.getTotalToPay());
            rowIndex++;
        }

        final XSSFRow totalRow = sheet.createRow(rowIndex++);
        final XSSFCellStyle totalRowStyle = getTotalRowStyle(workbook);
        addCell(totalRow, totalRowStyle, 0, "");
        addCell(totalRow, totalRowStyle, 1, "");
        addCell(totalRow, totalRowStyle, 2, "");
        addCell(totalRow, totalRowStyle, 3, "");
        addCell(totalRow, totalRowStyle, 4, "");
        addCell(totalRow, totalRowStyle, 5, "");
        addCell(totalRow, totalRowStyle, 6, "");
        addCell(totalRow, totalRowStyle, 7, "");
        addCell(totalRow, totalRowStyle, 8, "");
        addCell(totalRow, totalRowStyle, 9, totalPersonCost);
        addCell(totalRow, totalRowStyle, 10, totalPersonPaid);
        addCell(totalRow, totalRowStyle, 11, totalPersonToPay);
        addCell(totalRow, totalRowStyle, 12, totalGroupCost);
        addCell(totalRow, totalRowStyle, 13, totalGroupPaid);
        addCell(totalRow, totalRowStyle, 14, totalGroupToPay);
        addCell(totalRow, totalRowStyle, 15, totalTotalCost);
        addCell(totalRow, totalRowStyle, 16, totalTotalPaid);
        addCell(totalRow, totalRowStyle, 17, totalTotalToPay);
        addCell(totalRow, totalRowStyle, 18, "");
        addCell(totalRow, totalRowStyle, 19, "");

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the financial report XLSX file", e);
            throw e;
        }
    }

    private void createRow(XSSFSheet sheet, XSSFCellStyle wrappingCellStyle, int rowIndex, FinancialReportDTO dto) {
        final XSSFRow row = sheet.createRow(rowIndex);
        addCell(row, wrappingCellStyle, 0, dto.getRegId());
        addCell(row, wrappingCellStyle, 1, dto.getName());
        addCell(row, wrappingCellStyle, 2, dto.getRegTypes());
        addCell(row, wrappingCellStyle, 3, dto.getPayingGroupName());
        addCell(row, wrappingCellStyle, 4, dto.getCountry());
        addCell(row, wrappingCellStyle, 5, dto.getCity());
        addCell(row, wrappingCellStyle, 6, dto.getWorkplace());
        addCell(row, wrappingCellStyle, 7, dto.getPhone());
        addCell(row, wrappingCellStyle, 8, dto.getEmail());
        addCell(row, wrappingCellStyle, 9, dto.getPersonCost());
        addCell(row, wrappingCellStyle, 10, dto.getPersonPaid());
        addCell(row, wrappingCellStyle, 11, dto.getPersonToPay());
        addCell(row, wrappingCellStyle, 12, dto.getGroupCost());
        addCell(row, wrappingCellStyle, 13, dto.getGroupPaid());
        addCell(row, wrappingCellStyle, 14, dto.getGroupToPay());
        addCell(row, wrappingCellStyle, 15, dto.getTotalCost());
        addCell(row, wrappingCellStyle, 16, dto.getTotalPaid());
        addCell(row, wrappingCellStyle, 17, dto.getTotalToPay());
        addCell(row, wrappingCellStyle, 18, dto.getCurrency());
        addCell(row, wrappingCellStyle, 19, dto.getRemark());
    }

    private Map<String, Integer> createColumns() {
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg no.", 100);
        columns.put("Name", 200);
        columns.put("Reg types", 200);
        columns.put("Paying group", 200);
        columns.put("Country", 100);
        columns.put("City", 100);
        columns.put("Working place", 200);
        columns.put("Phone", 100);
        columns.put("Email", 100);
        columns.put("P. cost", 100);
        columns.put("P. paid", 100);
        columns.put("P. to pay", 100);
        columns.put("G. cost", 100);
        columns.put("G. paid", 100);
        columns.put("G. to pay", 100);
        columns.put("T. cost", 100);
        columns.put("T. paid", 100);
        columns.put("T. to pay", 100);
        columns.put("Currency", 100);
        columns.put("Remark", 100);
        return columns;
    }

    @SuppressWarnings("MultipleStringLiterals")
    private String composeRegistrationQuery(Congress congress) {
        return "select\n"
                + "r.id,\n"
                + "r.reg_id,\n"
                + "r.last_name,\n"
                + "r.first_name,\n"
                + "(select group_concat(distinct concat_ws(',', rt.name)) from registration_type rt\n"
                + "join registration_registration_type rrt on rrt.registration_type_id = rt.id where rrt.registration_id = r.id) reg_types,\n"
                + "c.code county_code,\n"
                + "c.name county,\n"
                + "r.city,\n"
                + "wp.name workplace,\n"
                + "r.phone,\n"
                + "r.email,\n"
                + "r.remark\n"
                + "from registration r\n"
                + "left join workplace wp on wp.id = r.workplace_id\n"
                + "left join country c on c.id = r.country_id\n"
                + "where r.congress_id = " + congress.getId();
    }

    private FinancialReportDTO getDTOFromRegistrationRow(Object[] row) {
        FinancialReportDTO bean = new FinancialReportDTO();
        bean.setId(((BigInteger) row[0]).longValue());
        bean.setRegId((Integer) row[1]);
        bean.setName(row[2] + " " + row[3]);
        bean.setRegTypes((String) row[4]);
        bean.setCountryCode((String) row[5]);
        bean.setCountry((String) row[6]);
        bean.setCity((String) row[7]);
        bean.setWorkplace((String) row[8]);
        bean.setPhone((String) row[9]);
        bean.setEmail((String) row[10]);
        bean.setRemark((String) row[11]);
        bean.setPayingGroupName("");
        bean.setPersonCost(BigDecimal.ZERO);
        bean.setPersonPaid(BigDecimal.ZERO);
        bean.setPersonToPay(BigDecimal.ZERO);
        bean.setGroupCost(BigDecimal.ZERO);
        bean.setGroupPaid(BigDecimal.ZERO);
        bean.setGroupToPay(BigDecimal.ZERO);
        bean.setTotalCost(BigDecimal.ZERO);
        bean.setTotalPaid(BigDecimal.ZERO);
        bean.setTotalToPay(BigDecimal.ZERO);
        return bean;
    }

    @SuppressWarnings({"MultipleStringLiterals", "AbbreviationAsWordInName"})
    private String composeRRTFinancialQuery(Congress congress) {
        return "select\n"
                + "rrt.registration_id,\n"
                + "sum(rrt.reg_fee * rrt.acc_people) total_cost,\n"
                + "sum((select sum(cs.amount) from charged_service cs where cs.chargeable_item_id = rrt.id)) person_paid,\n"
                + "sum((select sum(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rrt.reg_fee * rrt.acc_people,\n"
                + "if (pgi.amount_value is null, rrt.reg_fee * rrt.acc_people, pgi.amount_value)))\n"
                + "from paying_group_item pgi where pgi.id = rrt.paying_group_item_id)) group_cost,\n"
                + "sum((select sum(if (ci.date_of_group_payment is not null,\n"
                + "(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rrt.reg_fee * rrt.acc_people,\n"
                + "if (pgi.amount_value is null, rrt.reg_fee * rrt.acc_people, pgi.amount_value))), null))\n"
                + "from chargeable_item ci join paying_group_item pgi where ci.id = rrt.id and pgi.id = rrt.paying_group_item_id)) group_paid,\n"
                + "rrt.currency,\n"
                + "(select pg.name from paying_group pg\n"
                + "inner join paying_group_item pgi on pg.id = pgi.paying_group_id where pgi.id = rrt.paying_group_item_id) paying_group_name\n"
                + "from registration_registration_type rrt\n"
                + "inner join registration_type rt on rt.id = rrt.registration_type_id\n"
                + "inner join registration r on r.id = rrt.registration_id\n"
                + "inner join congress c on r.congress_id = c.id\n"
                + "where c.id = " + congress.getId() + "\n"
                + "group by rrt.registration_id\n";
    }

    @SuppressWarnings({"MultipleStringLiterals", "AbbreviationAsWordInName"})
    private String composeRRRFinancialQuery(Congress congress) {
        return "select\n"
                + "rrr.registration_id,\n"
                + "sum(rm.price * datediff(rr.departure_date, rr.arrival_date) / "
                + "(select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)) total_cost,\n"
                + "sum((select sum(cs.amount) from charged_service cs\n"
                + "join room_reservation_registration rrr2 on rrr2.id = cs.chargeable_item_id where rrr2.id = rrr.id)) person_paid,\n"
                + "sum(if (rrr.paying_group_item_id is not null, (select sum(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rm.price * datediff(rr.departure_date, rr.arrival_date)\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id),\n"
                + "if (pgi.amount_value is null and rr.departure_date > pgi.hotel_date_from and rr.arrival_date < pgi.hotel_date_to,\n"
                + "rm.price * datediff(least(pgi.hotel_date_to, rr.departure_date), greatest(pgi.hotel_date_from, rr.arrival_date))\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)\n"
                + ", pgi.amount_value))) from registration r\n"
                + "join room_reservation_registration rrr on rrr.registration_id = r.id\n"
                + "join paying_group_item pgi on pgi.id = rrr.paying_group_item_id\n"
                + "join paying_group pg on pgi.paying_group_id = pg.id\n"
                + "where rrr.room_reservation_id = rr.id), 0)) group_cost,\n"
                + "sum((select sum(if (ci.date_of_group_payment is not null, (if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rm.price * datediff(rr.departure_date, rr.arrival_date)\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id),\n"
                + "if (pgi.amount_value is null and rr.departure_date > pgi.hotel_date_from and rr.arrival_date < pgi.hotel_date_to,\n"
                + "rm.price * datediff(least(pgi.hotel_date_to, rr.departure_date), greatest(pgi.hotel_date_from, rr.arrival_date))\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)\n"
                + ", pgi.amount_value))), null)) from registration r\n"
                + "join room_reservation_registration rrr on rrr.registration_id = r.id\n"
                + "join chargeable_item ci on ci.id = rrr.id\n"
                + "join paying_group_item pgi on pgi.id = rrr.paying_group_item_id\n"
                + "join paying_group pg on pgi.paying_group_id = pg.id\n"
                + "where rrr.room_reservation_id = rr.id)) group_paid,\n"
                + "curr.currency,\n"
                + "(select group_concat(concat_ws(',', pg.name)) from room_reservation_registration rrr3\n"
                + "left join paying_group_item pgi on pgi.id = rrr3.paying_group_item_id\n"
                + "left join paying_group pg on pg.id = pgi.paying_group_id where rrr3.room_reservation_id = rr.id) paying_group_name\n"
                + "from room_reservation rr\n"
                + "inner join room rm on rm.id = rr.room_id\n"
                + "inner join currency curr on curr.id = rm.currency_id\n"
                + "inner join room_reservation_registration rrr on rrr.room_reservation_id = rr.id\n"
                + "inner join registration r on r.id = rrr.registration_id\n"
                + "inner join congress c on r.congress_id = c.id\n"
                + "where c.id = " + congress.getId() + "\n"
                + "group by rrr.registration_id\n";
    }

    @SuppressWarnings({"MultipleStringLiterals", "AbbreviationAsWordInName"})
    private String composeOOSFinancialQuery(Congress congress) {
        return "select\n"
                + "oos.registration_id,\n"
                + "sum(os.price * oos.participant) total_cost,\n"
                + "sum((select sum(cs.amount) from charged_service cs where cs.chargeable_item_id = oos.id)) person_paid,\n"
                + "sum((select sum(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * os.price * oos.participant,\n"
                + "if (pgi.amount_value is null, os.price * oos.participant, pgi.amount_value))) from paying_group_item pgi\n"
                + "where pgi.id = oos.paying_group_item_id)) group_cost,\n"
                + "sum((select sum(if (ci.date_of_group_payment is not null,\n"
                + "(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * os.price * oos.participant,\n"
                + "if (pgi.amount_value is null, os.price * oos.participant, pgi.amount_value))), null))\n"
                + "from chargeable_item ci join paying_group_item pgi where ci.id = oos.id and pgi.id = oos.paying_group_item_id)) group_paid,\n"
                + "curr.currency,\n"
                + "(select pg.name from paying_group pg\n"
                + "inner join paying_group_item pgi on pg.id = pgi.paying_group_id where pgi.id = oos.paying_group_item_id) paying_group_name\n"
                + "from ordered_optional_service oos\n"
                + "inner join optional_service os on os.id = oos.optional_service_id\n"
                + "inner join currency curr on curr.id = os.currency_id\n"
                + "inner join registration r on r.id = oos.registration_id\n"
                + "inner join congress c on r.congress_id = c.id\n"
                + "where c.id = " + congress.getId() + "\n"
                + "group by oos.registration_id\n";
    }

    private void getDataFromFinancialRows(Map<String, FinancialReportDTO> map, Object[] row) {
        FinancialReportDTO dto = map.get(row[0].toString());
        if (dto != null) {
            dto.setTotalCost(row[1] != null ? dto.getTotalCost().add((BigDecimal) row[1]) : dto.getTotalCost());
            dto.setPersonPaid(row[2] != null ? dto.getPersonPaid().add((BigDecimal) row[2]) : dto.getPersonPaid());
            dto.setGroupCost(row[3] != null ? dto.getGroupCost().add((BigDecimal) row[3]) : dto.getGroupCost());
            dto.setGroupPaid(row[4] != null ? dto.getGroupPaid().add((BigDecimal) row[4]) : dto.getGroupPaid());
            dto.setCurrency((String) row[5]);
            dto.setPayingGroupName(row[6] != null ? row[6] + (dto.getPayingGroupName().length() > 0 ? "," : "") + dto.getPayingGroupName() : dto.getPayingGroupName());

            dto.setTotalPaid(dto.getGroupPaid().add(dto.getPersonPaid()));
            dto.setPersonCost(dto.getTotalCost().subtract(dto.getGroupCost()));
            dto.setPersonToPay(dto.getTotalCost().subtract(dto.getGroupCost()).subtract(dto.getPersonPaid()));
            dto.setGroupToPay(dto.getGroupCost().subtract(dto.getGroupPaid()));
            dto.setTotalToPay(dto.getTotalCost().subtract(dto.getGroupPaid()).subtract(dto.getPersonPaid()));
        }
    }

}
