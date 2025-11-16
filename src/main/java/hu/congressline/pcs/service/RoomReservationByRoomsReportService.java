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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.service.dto.RoomReservationByRoomsDTO;
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
public class RoomReservationByRoomsReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservationByRoomsDTO> findAll(Congress congress, Hotel hotel) {
        log.debug("Request to get all RoomReservationByRoomsDTOs");
        final Query query = entityManager.createNativeQuery(composeQuery(congress, hotel));
        List result = query.getResultList();
        List<RoomReservationByRoomsDTO> rrbpList = new ArrayList<>();
        for (Object item : result) {
            RoomReservationByRoomsDTO dto = getBeanFromRow((Object[]) item);
            rrbpList.add(dto);
        }
        return rrbpList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress, Hotel hotel) throws IOException {
        final List<RoomReservationByRoomsDTO> resultList = findAll(congress, hotel);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Room type", 200);
        columns.put("Reg no.", 100);
        columns.put("Name", 200);
        columns.put("Country", 200);
        columns.put("Arrival", 100);
        columns.put("Departure", 100);
        columns.put("Nights", 100);
        columns.put("T. cost", 100);
        columns.put("T. paid", 100);
        columns.put("T. to pay", 100);
        columns.put("P. cost", 100);
        columns.put("P. paid", 100);
        columns.put("P. to pay", 100);
        columns.put("G. cost", 100);
        columns.put("G. paid", 100);
        columns.put("G. to pay", 100);
        columns.put("Currency", 100);
        columns.put("Paying group", 100);
        columns.put("Comment", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Room reservation by rooms", hotel.getName(), congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (RoomReservationByRoomsDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRoomType());
            addCell(row, wrappingCellStyle, 1, dto.getRegId());
            addCell(row, wrappingCellStyle, 2, dto.getName());
            addCell(row, wrappingCellStyle, 3, dto.getCountry());
            addCell(row, wrappingCellStyle, 4, dto.getArrivalDate());
            addCell(row, wrappingCellStyle, 5, dto.getDepartureDate());
            addCell(row, wrappingCellStyle, 6, dto.getNights());
            addCell(row, wrappingCellStyle, 7, dto.getTotalCost());
            addCell(row, wrappingCellStyle, 8, dto.getTotalPaid());
            addCell(row, wrappingCellStyle, 9, dto.getTotalToPay());
            addCell(row, wrappingCellStyle, 10, dto.getPersonCost());
            addCell(row, wrappingCellStyle, 11, dto.getPersonPaid());
            addCell(row, wrappingCellStyle, 12, dto.getPersonToPay());
            addCell(row, wrappingCellStyle, 13, dto.getGroupCost());
            addCell(row, wrappingCellStyle, 14, dto.getGroupPaid());
            addCell(row, wrappingCellStyle, 15, dto.getGroupToPay());
            addCell(row, wrappingCellStyle, 16, dto.getCurrency());
            addCell(row, wrappingCellStyle, 17, dto.getPayingGroupName());
            addCell(row, wrappingCellStyle, 18, dto.getComment());

            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the room reservation by participants report XLSX file", e);
            throw e;
        }
    }

    protected RoomReservationByRoomsDTO getBeanFromRow(Object[] row) {
        RoomReservationByRoomsDTO bean = new RoomReservationByRoomsDTO();
        bean.setId(((BigDecimal) row[0]).longValue());
        bean.setRegId((String) row[1]);
        bean.setName((String) row[2]);
        bean.setCountry((String) row[3]);
        bean.setRoomType((String) row[4]);
        bean.setArrivalDate(row[5] != null ? ((java.sql.Date) row[5]).toLocalDate() : null);
        bean.setDepartureDate(row[6] != null ? ((java.sql.Date) row[6]).toLocalDate() : null);
        //mariadb vs. mysql difference!!!
        bean.setNights(row[7] instanceof BigInteger ? ((BigInteger) row[7]).intValue() : (Integer) row[7]);
        bean.setPersonCost(ConverterUtil.getBigDecimalValue(row[8]));
        bean.setPersonPaid(ConverterUtil.getBigDecimalValue(row[9]));
        bean.setPersonToPay(ConverterUtil.getBigDecimalValue(row[10]));
        bean.setGroupCost(ConverterUtil.getBigDecimalValue(row[11]));
        bean.setGroupPaid(ConverterUtil.getBigDecimalValue(row[12]));
        bean.setGroupToPay(ConverterUtil.getBigDecimalValue(row[13]));
        bean.setTotalCost(ConverterUtil.getBigDecimalValue(row[14]));
        bean.setTotalPaid(ConverterUtil.getBigDecimalValue(row[15]));
        bean.setTotalToPay(ConverterUtil.getBigDecimalValue(row[16]));
        bean.setCurrency((String) row[17]);
        bean.setPayingGroupName((String) row[18]);
        bean.setComment((String) row[19]);
        return bean;
    }

    @SuppressWarnings("MultipleStringLiterals")
    protected String composeQuery(Congress congress, Hotel hotel) {
        return "select\n"
                + "cast((@row_number \\:= @row_number + 1) AS decimal(10, 0)) id,\n"
                + "q.reg_id,\n"
                + "q.name,\n"
                + "q.country,\n"
                + "q.room_type,\n"
                + "q.arrival_date,\n"
                + "q.departure_date,\n"
                + "q.nights,\n"
                + "q.total_cost - if(q.group_cost is null, 0, q.group_cost) person_cost,\n"
                + "q.person_paid,\n"
                + "q.total_cost - if(q.group_cost is null, 0, q.group_cost) - if(q.person_paid is null, 0, q.person_paid) person_to_pay,\n"
                + "q.group_cost,\n"
                + "q.group_paid,\n"
                + "q.group_cost - if(q.group_paid is null, 0, q.group_paid) group_to_pay,\n"
                + "q.total_cost,\n"
                + "if(q.person_paid is null, 0, q.person_paid) + if(q.group_paid is null, 0, q.group_paid) total_paid,\n"
                + "q.total_cost - if(q.person_paid is null, 0, q.person_paid) - if(q.group_paid is null, 0, q.group_paid) total_to_pay,\n"
                + "q.currency,\n"
                + "q.paying_group_name,\n"
                + "q.comment,\n"
                + "q.hotel_id\n"
                + "from (\n"
                + "select\n"
                + "(select group_concat(r.reg_id SEPARATOR ', ') from registration r join\n"
                + "room_reservation_registration rrr on r.id = rrr.registration_id where rrr.room_reservation_id = rr.id) reg_id,\n"
                + "(select group_concat(concat(UPPER(r.last_name), ' ', r.first_name) SEPARATOR ', ') from registration r join\n"
                + "room_reservation_registration rrr on r.id = rrr.registration_id where rrr.room_reservation_id = rr.id) name,\n"
                + "(select group_concat(c.name SEPARATOR ', ') from registration r\n"
                + "join room_reservation_registration rrr on r.id = rrr.registration_id\n"
                + "join country c on r.country_id = c.id\n"
                + "where rrr.room_reservation_id = rr.id\n"
                + ") country,\n"
                + "rm.room_type,\n"
                + "rr.arrival_date,\n"
                + "rr.departure_date,\n"
                + "datediff(rr.departure_date, rr.arrival_date) nights,\n"
                + "(rm.price * datediff(rr.departure_date, rr.arrival_date)) total_cost,\n"
                + "(select sum(cs.amount) from charged_service cs join room_reservation_registration rrr on rrr.id = cs.chargeable_item_id\n"
                + "where rrr.room_reservation_id = rr.id) person_paid,\n"
                + "(select sum(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rm.price * datediff(rr.departure_date, rr.arrival_date)\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id),\n"
                + "if (pgi.amount_value is null and rr.departure_date > pgi.hotel_date_from and rr.arrival_date < pgi.hotel_date_to,\n"
                + "rm.price * datediff(least(pgi.hotel_date_to, rr.departure_date), greatest(pgi.hotel_date_from, rr.arrival_date))\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)\n"
                + ", pgi.amount_value))) from registration r\n"
                + "join room_reservation_registration rrr on rrr.registration_id = r.id\n"
                + "join paying_group_item pgi on pgi.id = rrr.paying_group_item_id\n"
                + "join paying_group pg on pgi.paying_group_id = pg.id\n"
                + "where rrr.room_reservation_id = rr.id\n"
                + ") group_cost,\n"
                + "(select sum(if (ci.date_of_group_payment is not null, (if (pgi.amount_percentage is not null,\n"
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
                + "where rrr.room_reservation_id = rr.id\n"
                + ") group_paid,\n"
                + "(select group_concat(comment SEPARATOR ', ') from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id) comment,\n"
                + "curr.currency,\n"
                + "(select group_concat(pg.name SEPARATOR ', ') from room_reservation_registration rrr3\n"
                + "left join paying_group_item pgi on pgi.id = rrr3.paying_group_item_id\n"
                + "left join paying_group pg on pg.id = pgi.paying_group_id where rrr3.room_reservation_id = rr.id) paying_group_name,\n"
                + "ch.hotel_id\n"
                + "from room_reservation rr\n"
                + "inner join room rm on rm.id = rr.room_id\n"
                + "inner join currency curr on curr.id = rm.currency_id\n"
                + "inner join congress_hotel ch on ch.id = rm.congress_hotel_id\n"
                + "where ch.congress_id = " + congress.getId() + "\n"
                + " and ch.hotel_id = " + hotel.getId() + "\n"
                + "order by name) as q, (select @row_number\\:=0) as rownum\n";
    }
}
