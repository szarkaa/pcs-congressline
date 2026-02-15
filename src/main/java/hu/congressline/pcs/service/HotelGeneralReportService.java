package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.dto.HotelGeneralReportDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class HotelGeneralReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<HotelGeneralReportDTO> findAll(Congress congress) {
        log.debug("Request to get all HotelGeneralReportDTO");
        final Query query = entityManager.createNativeQuery(composeQuery(congress));
        List result = query.getResultList();
        List<HotelGeneralReportDTO> list = new ArrayList<>();
        for (Object item : result) {
            HotelGeneralReportDTO dto = getBeanFromRow((Object[]) item);
            list.add(dto);
        }
        return list;
    }

    private String composeQuery(Congress congress) {
        return "select\n"
                + "rrr.id,\n"
                + "r.reg_id,\n"
                + "r.last_name,\n"
                + "r.first_name,\n"
                + "(select group_concat(distinct concat_ws(',', rt.name)) from registration_type rt\n"
                + "join registration_registration_type rrt on rrt.registration_type_id = rt.id where rrt.registration_id = r.id) reg_types,\n"
                + "pg.name paying_group_name,\n"
                + "c.name county,\n"
                + "r.city,\n"
                + "r.phone,\n"
                + "r.email,\n"
                + "h.name,\n"
                + "rm.room_type,\n"
                + "rr.arrival_date,\n"
                + "rr.departure_date,\n"
                + "(select group_concat(concat_ws(',', concat(r2.last_name, ' ', r2.first_name))) from registration r2 join\n"
                + "room_reservation_registration rrr2 on r2.id = rrr2.registration_id\n"
                + "where rrr2.room_reservation_id = rr.id and rrr2.registration_id <> r.id) roommates\n"
                + "from room_reservation rr\n"
                + "inner join room rm on rm.id = rr.room_id\n"
                + "inner join congress_hotel ch on ch.id = rm.congress_hotel_id\n"
                + "inner join hotel h on ch.hotel_id = h.id\n"
                + "inner join room_reservation_registration rrr on rrr.room_reservation_id = rr.id\n"
                + "inner join registration r on r.id = rrr.registration_id\n"
                + "left join paying_group_item pgi on pgi.id = rrr.paying_group_item_id\n"
                + "left join paying_group pg on pg.id = pgi.paying_group_id\n"
                + "left join country c on c.id = r.country_id\n"
                + "where r.congress_id = " + congress.getId() + "\n"
                + "order by r.last_name, r.first_name";
    }

    private HotelGeneralReportDTO getBeanFromRow(Object[] row) {
        HotelGeneralReportDTO bean = new HotelGeneralReportDTO();
        bean.setId((Long) row[0]);
        bean.setRegId((Integer) row[1]);
        bean.setName(row[2] + " " + row[3]);
        bean.setRegTypes((String) row[4]);
        bean.setPayingGroupName((String) row[5]);
        bean.setCountry((String) row[6]);
        bean.setCity((String) row[7]);
        bean.setPhone((String) row[8]);
        bean.setEmail((String) row[9]);
        bean.setHotelName((String) row[10]);
        bean.setRoomType((String) row[11]);
        bean.setArrivalDate(row[12] != null ? (LocalDate) row[12] : null);
        bean.setDepartureDate(row[13] != null ? (LocalDate) row[13] : null);
        bean.setRoomMates((String) row[14]);
        return bean;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress) throws IOException {
        final List<HotelGeneralReportDTO> resultList = findAll(congress);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg no.", 100);
        columns.put("Name", 200);
        columns.put("Reg types", 200);
        columns.put("Paying group", 200);
        columns.put("Country", 100);
        columns.put("City", 100);
        columns.put("Phone", 100);
        columns.put("Email", 100);
        columns.put("Hotel", 200);
        columns.put("Room type", 200);
        columns.put("Arrival date", 100);
        columns.put("Departure date", 100);
        columns.put("Room mates", 200);

        final XSSFSheet sheet = createXlsxTab(workbook, "Hotel general report", null, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (HotelGeneralReportDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getName());
            addCell(row, wrappingCellStyle, 2, dto.getRegTypes());
            addCell(row, wrappingCellStyle, 3, dto.getPayingGroupName());
            addCell(row, wrappingCellStyle, 4, dto.getCountry());
            addCell(row, wrappingCellStyle, 5, dto.getCity());
            addCell(row, wrappingCellStyle, 6, dto.getPhone());
            addCell(row, wrappingCellStyle, 7, dto.getEmail());
            addCell(row, wrappingCellStyle, 8, dto.getHotelName());
            addCell(row, wrappingCellStyle, 9, dto.getRoomType());
            addCell(row, wrappingCellStyle, 10, dto.getArrivalDate());
            addCell(row, wrappingCellStyle, 11, dto.getDepartureDate());
            addCell(row, wrappingCellStyle, 12, dto.getRoomMates());
            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the hotel general report XLSX file", e);
            throw e;
        }
    }

}
