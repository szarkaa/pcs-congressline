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
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Hotel;
import hu.congressline.pcs.service.dto.HotelSummaryCellDTO;
import hu.congressline.pcs.service.dto.HotelSummaryColumnDTO;
import hu.congressline.pcs.service.dto.HotelSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class HotelSummaryReportService extends XlsReportService {

    private final RoomReservationService roomReservationService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public HotelSummaryDTO findAll(Congress congress, Hotel hotel) {
        log.debug("Request to get all HotelSummaryCellDTOs");

        List<HotelSummaryCellDTO> cellList = new ArrayList<>();
        roomReservationService.findAllRoomReservationByHotelAndCongress(congress, hotel).forEach(rr -> {
            LocalDate startDate = rr.getArrivalDate();
            while (!startDate.isEqual(rr.getDepartureDate())) {
                HotelSummaryCellDTO cell = getCell(cellList, startDate, rr.getRoom().getId());
                if (cell != null) {
                    cell.setNights(cell.getNights() + 1);
                } else {
                    cellList.add(new HotelSummaryCellDTO(startDate, rr.getRoom().getId(), rr.getRoom().getRoomType()));
                }
                startDate = startDate.plusDays(1);
            }
        });
        return new HotelSummaryDTO(cellList);
    }

    @SuppressWarnings({"MissingJavadocMethod", "MultipleStringLiterals"})
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress, Hotel hotel) throws IOException {
        final HotelSummaryDTO summaryDTO = findAll(congress, hotel);
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reservation date", 100);
        summaryDTO.getColumns().forEach(column -> columns.put(column.getRoomType(), 200));
        columns.put("Total", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Hotel reservation summary", hotel.getName(), congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        Set<LocalDate> rows = new TreeSet<>(summaryDTO.getCells().stream().map(HotelSummaryCellDTO::getReservationDate).collect(Collectors.toSet()));

        int rowIndex = 4;
        int grandTotal = 0;
        for (LocalDate reservationDate : rows) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, reservationDate);
            int colIdx = 1;
            int total = 0;
            for (HotelSummaryColumnDTO column : summaryDTO.getColumns()) {
                final HotelSummaryCellDTO cell = getCell(summaryDTO.getCells(), reservationDate, column.getRoomId());
                addCell(row, wrappingCellStyle, colIdx, cell != null ? cell.getNights() : 0);
                total += cell != null ? cell.getNights() : 0;
                colIdx++;
            }
            addCell(row, wrappingCellStyle, colIdx, total);
            grandTotal += total;
            rowIndex++;
        }

        //Total row
        final XSSFRow row = sheet.createRow(rowIndex);
        final XSSFCellStyle totalRowStyle = getTotalRowStyle(workbook);
        addCell(row, totalRowStyle, 0, "Total");

        for (int colIdx = 1; colIdx < summaryDTO.getColumns().size(); colIdx++) {
            addCell(row, totalRowStyle, colIdx, "");
        }
        addCell(row, totalRowStyle, summaryDTO.getColumns().size() + 1, grandTotal);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the hotel summary report XLSX file", e);
            throw e;
        }
    }

    private HotelSummaryCellDTO getCell(List<HotelSummaryCellDTO> cellList, LocalDate reservationDate, Long roomId) {
        return cellList.stream().filter(cell -> cell.getRoomId().equals(roomId) && cell.getReservationDate().equals(reservationDate)).findFirst().orElse(null);
    }
}
