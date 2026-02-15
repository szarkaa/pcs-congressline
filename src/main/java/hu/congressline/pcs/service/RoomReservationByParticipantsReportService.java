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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.service.dto.RoomReservationByParticipantsDTO;
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
public class RoomReservationByParticipantsReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RoomReservationByParticipantsDTO> findAll(Congress congress, Hotel hotel) {
        log.debug("Request to get all room reservation by participants");
        final Query query = entityManager.createNativeQuery(composeQuery(congress, hotel));
        List result = query.getResultList();
        List<RoomReservationByParticipantsDTO> rrbpList = new ArrayList<>();
        for (Object item : result) {
            RoomReservationByParticipantsDTO dto = getBeanFromRow((Object[]) item);
            rrbpList.add(dto);
        }
        return rrbpList;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress, Hotel hotel) throws IOException {
        final List<RoomReservationByParticipantsDTO> resultList = findAll(congress, hotel);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Room type", 200);
        columns.put("Reg no.", 100);
        columns.put("Name", 200);
        columns.put("Email", 200);
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
        columns.put("Paying group", 100);
        columns.put("Currency", 100);
        columns.put("Comment", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Room reservation by participants", hotel.getName(), congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (RoomReservationByParticipantsDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRoomType());
            addCell(row, wrappingCellStyle, 1, dto.getRegId());
            addCell(row, wrappingCellStyle, 2, String.format("%s %s", dto.getLastName(), dto.getFirstName()));
            addCell(row, wrappingCellStyle, 3, dto.getEmail());
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
            addCell(row, wrappingCellStyle, 16, dto.getPayingGroupName());
            addCell(row, wrappingCellStyle, 17, dto.getCurrency());
            addCell(row, wrappingCellStyle, 18, dto.getComment());

            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the room reservation by participants report XLSX file", e);
            throw e;
        }
    }

    protected RoomReservationByParticipantsDTO getBeanFromRow(Object[] row) {
        RoomReservationByParticipantsDTO bean = new RoomReservationByParticipantsDTO();
        bean.setId(((BigDecimal) row[0]).longValue());
        bean.setRegId((Integer) row[1]);
        bean.setLastName((String) row[2]);
        bean.setFirstName((String) row[3]);
        bean.setEmail((String) row[4]);
        bean.setRoomType((String) row[5]);
        bean.setArrivalDate(row[6] != null ? (LocalDate) row[6] : null);
        bean.setDepartureDate(row[7] != null ? (LocalDate) row[7] : null);
        bean.setNights(row[8] != null ? (Integer) row[8] : null);
        bean.setPersonCost(ConverterUtil.getBigDecimalValue(row[9]));
        bean.setPersonPaid(ConverterUtil.getBigDecimalValue(row[10]));
        bean.setPersonToPay(ConverterUtil.getBigDecimalValue(row[11]));
        bean.setGroupCost(ConverterUtil.getBigDecimalValue(row[12]));
        bean.setGroupPaid(ConverterUtil.getBigDecimalValue(row[13]));
        bean.setGroupToPay(ConverterUtil.getBigDecimalValue(row[14]));
        bean.setTotalCost(ConverterUtil.getBigDecimalValue(row[15]));
        bean.setTotalPaid(ConverterUtil.getBigDecimalValue(row[16]));
        bean.setTotalToPay(ConverterUtil.getBigDecimalValue(row[17]));
        bean.setCurrency((String) row[18]);
        bean.setPayingGroupName((String) row[19]);
        bean.setComment((String) row[20]);
        return bean;
    }

    @SuppressWarnings("MultipleStringLiterals")
    protected String composeQuery(Congress congress, Hotel hotel) {
        return "select\n"
                + "cast((@row_number \\:= @row_number + 1) AS decimal(10, 0)) id,\n"
                + "q.reg_id,\n"
                + "q.last_name,\n"
                + "q.first_name,\n"
                + "q.email,\n"
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
                + "from (select\n"
                + "r.reg_id,\n"
                + "r.last_name,\n"
                + "r.first_name,\n"
                + "r.email,\n"
                + "rm.room_type,\n"
                + "rr.arrival_date,\n"
                + "rr.departure_date,\n"
                + "datediff(rr.departure_date, rr.arrival_date) nights,\n"
                + "(if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rm.price * datediff(rr.departure_date, rr.arrival_date)\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id),\n"
                + "if (pgi.amount_value is null and rr.departure_date > pgi.hotel_date_from and rr.arrival_date < pgi.hotel_date_to,\n"
                + "rm.price * datediff(least(pgi.hotel_date_to, rr.departure_date), greatest(pgi.hotel_date_from, rr.arrival_date))\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)\n"
                + ",pgi.amount_value))) group_cost,\n"
                + "(if (ci.date_of_group_payment is not null, (if (pgi.amount_percentage is not null,\n"
                + "pgi.amount_percentage / 100 * rm.price * datediff(rr.departure_date, rr.arrival_date)\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id),\n"
                + "if (pgi.amount_value is null and rr.departure_date > pgi.hotel_date_from and rr.arrival_date < pgi.hotel_date_to,\n"
                + "rm.price * datediff(least(pgi.hotel_date_to, rr.departure_date), greatest(pgi.hotel_date_from, rr.arrival_date))\n"
                + "/ (select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id)\n"
                + ", pgi.amount_value))), null)) group_paid,\n"
                + "(rm.price * datediff(rr.departure_date, rr.arrival_date) /\n"
                + "(select count(1) from room_reservation_registration rrr2 where rrr2.room_reservation_id = rr.id))\n"
                + "total_cost,\n"
                + "(select sum(cs.amount) from charged_service cs join room_reservation_registration rrr on rrr.id = cs.chargeable_item_id\n"
                + "where cs.registration_id = r.id and rrr.room_reservation_id = rr.id) person_paid,\n"
                + "cur.currency,\n"
                + "pg.name paying_group_name,\n"
                + "rrr.comment,\n"
                + "ch.hotel_id\n"
                + "from\n"
                + "registration r\n"
                + "inner join congress c on r.congress_id = c.id\n"
                + "inner join room_reservation_registration rrr on r.id = rrr.registration_id\n"
                + "inner join chargeable_item ci on ci.id = rrr.id\n"
                + "inner join room_reservation rr on rr.id = rrr.room_reservation_id\n"
                + "inner join room rm on rm.id = rr.room_id\n"
                + "inner join currency cur on rm.currency_id = cur.id\n"
                + "inner join congress_hotel ch on ch.id = rm.congress_hotel_id\n"
                + "left join paying_group_item pgi on pgi.id = rrr.paying_group_item_id\n"
                + "left join paying_group pg on pgi.paying_group_id = pg.id\n"
                + "where ch.congress_id = " + congress.getId() + "\n"
                + " and c.id = '" + congress.getId() + "'\n"
                + " and ch.hotel_id = " + hotel.getId() + "\n"
                + "order by r.last_name, r.first_name) as q, (select @row_number\\:=0) as rownum\n";
    }
}
