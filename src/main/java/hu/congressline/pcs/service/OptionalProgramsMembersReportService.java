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
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.dto.OptionalProgramsMembersDTO;
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
public class OptionalProgramsMembersReportService extends XlsReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final CurrencyService currencyService;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<OptionalProgramsMembersDTO> findAll(Long congressId) {
        log.debug("Request to get all OptionalProgramsMembersDTOs");
        final Query query = entityManager.createNativeQuery(composeQuery(congressId));
        List result = query.getResultList();
        List<OptionalProgramsMembersDTO> rrbpList = new ArrayList<>();
        for (Object item : result) {
            OptionalProgramsMembersDTO dto = getBeanFromRow((Object[]) item);
            rrbpList.add(dto);
        }
        return rrbpList;
    }

    protected OptionalProgramsMembersDTO getBeanFromRow(Object[] row) {
        OptionalProgramsMembersDTO bean = new OptionalProgramsMembersDTO();
        bean.setProgram((String) row[0]);
        bean.setName((String) row[1]);
        bean.setStartDate(row[2] != null ? new java.sql.Date(((Date) row[2]).getTime()).toLocalDate() : null);
        bean.setEndDate(row[3] != null ? new java.sql.Date(((Date) row[3]).getTime()).toLocalDate() : null);
        bean.setPrice(ConverterUtil.getBigDecimalValue(row[4]));
        bean.setCurrency((String) row[5]);
        bean.setNumberOfApplicants(((BigDecimal) row[6]).intValue());
        bean.setMaxPerson((Integer) row[7]);

        return bean;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(Congress congress) throws IOException {
        final List<OptionalProgramsMembersDTO> resultList = findAll(congress.getId());
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Program", 100);
        columns.put("Name", 200);
        columns.put("Start", 100);
        columns.put("End", 100);
        columns.put("Price", 100);
        columns.put("Currency", 100);
        columns.put("Applicants no.", 100);
        columns.put("Max.capacity", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Optional program members", null, congress.getName(), getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (OptionalProgramsMembersDTO dto : resultList) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getProgram());
            addCell(row, wrappingCellStyle, 1, dto.getName());
            addCell(row, wrappingCellStyle, 2, dto.getStartDate());
            addCell(row, wrappingCellStyle, 3, dto.getEndDate());
            addCell(row, wrappingCellStyle, 4, dto.getPrice());
            addCell(row, wrappingCellStyle, 5, dto.getCurrency());
            addCell(row, wrappingCellStyle, 6, dto.getNumberOfApplicants());
            addCell(row, wrappingCellStyle, 7, dto.getMaxPerson());
            rowIndex++;
        }

        addListedItemsCountRow(sheet, getTotalRowStyle(workbook), rowIndex, resultList.size());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the optional service by applicants report XLSX file", e);
            throw e;
        }
    }

    protected String composeQuery(Long congressId) {
        return "select os.code,\n"
                + "os.name,\n"
                + "os.start_date,\n"
                + "os.end_date,\n"
                + "os.price,\n"
                + "c.currency,\n"
                + "sum(oos.participant) applicant,\n"
                + "os.max_person\n"
                + "from ordered_optional_service oos\n"
                + "join optional_service os on os.id = oos.optional_service_id\n"
                + "join currency c on c.id = os.currency_id\n"
                + "where os.congress_id = " + congressId + "\n"
                + "group by os.id\n"
                + "order by os.name\n";
    }

}
