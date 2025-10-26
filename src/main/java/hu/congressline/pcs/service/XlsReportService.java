package hu.congressline.pcs.service;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import hu.congressline.pcs.service.util.PoiPixelUtil;
import lombok.extern.slf4j.Slf4j;

import static hu.congressline.pcs.service.util.DateUtil.DATE_FORMAT_EN;

@Slf4j
@Service
public abstract class XlsReportService {

    protected static final int XLSX_HEADER_HEIGHT = 81;
    protected static final Color XLSX_MAIN_HEADER_COLOR = new Color(103, 106, 108);
    protected static final Color XLSX_SUB_HEADER_COLOR = new Color(204, 204, 255);

    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_EN);

    @Autowired
    private CompanyService companyService;

    protected void addCell(XSSFRow row, XSSFCellStyle cellStyle, int columnIndex, String value) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    protected void addCell(XSSFRow row, XSSFCellStyle cellStyle, int columnIndex, Integer value) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    protected void addCell(XSSFRow row, XSSFCellStyle cellStyle, int columnIndex, Long value) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    protected void addCell(XSSFRow row, XSSFCellStyle cellStyle, int columnIndex, BigDecimal value) {
        XSSFCell cell = row.createCell(columnIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellType(CellType.NUMERIC);
        if (value != null) {
            cell.setCellValue(value.doubleValue());
        }
    }

    protected void addCell(XSSFRow row, XSSFCellStyle cellStyle, int columnIndex, LocalDate value) {
        addCell(row, cellStyle, columnIndex, value != null ? value.format(DateTimeFormatter.ISO_LOCAL_DATE) : "");
    }

    protected void addCell(XSSFRow row, XSSFCellStyle cellStyle, int columnIndex, ZonedDateTime value) {
        addCell(row, cellStyle, columnIndex, value != null ? value.format(DateTimeFormatter.ISO_ZONED_DATE_TIME) : "");
    }

    protected XSSFSheet createXlsxTab(final XSSFWorkbook workbook, String title, String subTitle, String congressName, int[] columnSizes) {
        final XSSFSheet sheet = workbook.createSheet("report tab");
        final XSSFCellStyle mainHeaderStyle = workbook.createCellStyle();
        final DefaultIndexedColorMap defaultIndexedColorMap = new DefaultIndexedColorMap();
        mainHeaderStyle.setFillForegroundColor(new XSSFColor(XLSX_MAIN_HEADER_COLOR, defaultIndexedColorMap));
        mainHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont mainHeaderFont = workbook.createFont();
        mainHeaderFont.setBold(true);
        mainHeaderFont.setFontHeightInPoints((short) 11);
        mainHeaderStyle.setFont(mainHeaderFont);
        final short headerHeight = (short) (XLSX_HEADER_HEIGHT * 5);

        final String dash = " - ";
        String[] rowTitle = {companyService.getCompanyProfile().getName() + dash + title + dash + FORMATTER.format(LocalDate.now()), congressName};
        int rowIndex = 0;
        for (; rowIndex < 2; rowIndex++) {
            XSSFRow mainHeaderRow = sheet.createRow(rowIndex);
            mainHeaderRow.setHeight(headerHeight);

            int i = 0;
            for (int columnSize : columnSizes) {
                XSSFCell cell = mainHeaderRow.createCell(i);
                cell.setCellStyle(mainHeaderStyle);
                setColumnWidth(sheet, i, columnSize);
                if (i == 0) {
                    cell.setCellValue(rowTitle[rowIndex]);
                }
                i++;
            }
        }

        // Subtitle
        XSSFRow subHeaderRow = sheet.createRow(rowIndex);
        subHeaderRow.setHeight(headerHeight);
        final XSSFCellStyle subHeaderStyle = workbook.createCellStyle();
        subHeaderStyle.setFillForegroundColor(new XSSFColor(XLSX_MAIN_HEADER_COLOR, defaultIndexedColorMap));
        subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        XSSFFont subHeaderFont = workbook.createFont();
        subHeaderFont.setBold(false);
        subHeaderFont.setFontHeightInPoints((short) 11);
        subHeaderStyle.setFont(subHeaderFont);

        int i = 0;
        for (int columnSize : columnSizes) {
            XSSFCell cell = subHeaderRow.createCell(i);
            cell.setCellStyle(subHeaderStyle);
            setColumnWidth(sheet, i, columnSize);
            if (i == 0 && subTitle != null) {
                cell.setCellValue(subTitle);
            }
            i++;
        }
        return sheet;
    }

    protected void addSubHeader(XSSFSheet sheet, Map<String, Integer> columns) {
        XSSFWorkbook workbook = sheet.getWorkbook();
        final XSSFCellStyle subHeaderStyle = workbook.createCellStyle();
        subHeaderStyle.setFillForegroundColor(new XSSFColor(XLSX_SUB_HEADER_COLOR, new DefaultIndexedColorMap()));
        subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        XSSFFont subHeaderFont = workbook.createFont();
        subHeaderFont.setBold(true);
        subHeaderFont.setFontHeightInPoints((short) 11);
        subHeaderStyle.setFont(subHeaderFont);

        final XSSFRow subHeaderRow = sheet.createRow(3);
        int i = 0;
        for (String columnName : columns.keySet()) {
            XSSFCell cell = subHeaderRow.createCell(i);
            cell.setCellStyle(subHeaderStyle);
            cell.setCellValue(columnName);
            i++;
        }
    }

    protected XSSFCellStyle getTotalRowStyle(XSSFWorkbook workbook) {
        final XSSFCellStyle totalRowStyle = workbook.createCellStyle();
        XSSFFont totalRowFont = workbook.createFont();
        totalRowFont.setBold(true);
        totalRowStyle.setFont(totalRowFont);
        return totalRowStyle;
    }

    protected void addListedItemsCountRow(XSSFSheet sheet, XSSFCellStyle wrappingCellStyle, int rowIndex, int itemCount) {
        final XSSFRow row = sheet.createRow(rowIndex);
        addCell(row, wrappingCellStyle, 0, "The list contains " + itemCount + " items");
    }

    protected int[] getColumnWidthsAsArray(Map<String, Integer> columns) {
        int[] result = new int[columns.size()];
        int i = 0;
        for (Integer columnWidth : columns.values()) {
            result[i++] = columnWidth;
        }
        return result;
    }

    protected void setColumnWidth(XSSFSheet sheet, int index, int columnSizePx) {
        sheet.setColumnWidth(index, PoiPixelUtil.pixel2WidthUnits(columnSizePx));
    }

}
