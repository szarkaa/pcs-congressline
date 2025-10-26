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
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.enumeration.ChargedServicePaymentMode;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import hu.congressline.pcs.service.dto.CreditCardReportDTO;
import hu.congressline.pcs.web.rest.vm.CreditCardReportVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CreditCardReportService extends XlsReportService {

    private final ChargedServiceRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<CreditCardReportDTO> findAll(CreditCardReportVM reportFilter) {
        log.debug("Request to get all InvoiceReportDTO");
        final List<CreditCardReportDTO> creditCardPayments = new ArrayList<>();
        final String trxId = reportFilter.getTransactionId();
        final String programNumber = reportFilter.getProgramNumber();
        LocalDate defaultFromDate = LocalDate.of(1900, Month.JANUARY, 1);
        LocalDate defaultToDate = LocalDate.of(2200, Month.JANUARY, 1);
        LocalDate fromDate = reportFilter.getFromDate() != null ? reportFilter.getFromDate() : defaultFromDate;
        LocalDate toDate = reportFilter.getToDate() != null ? reportFilter.getToDate() : defaultToDate;
        if (programNumber == null && trxId == null) {
            creditCardPayments.addAll(repository.findAllByDateOfPaymentBetween(fromDate, toDate)
                    .stream().filter(o -> o.getPaymentMode().equals(ChargedServicePaymentMode.CARD)).map(CreditCardReportDTO::new).collect(Collectors.toList()));
        } else {
            if (trxId == null) {
                creditCardPayments.addAll(repository.findAllByDateOfPaymentBetweenAndRegistrationCongressProgramNumber(fromDate, toDate, programNumber)
                        .stream().filter(o -> o.getPaymentMode().equals(ChargedServicePaymentMode.CARD)).map(CreditCardReportDTO::new).collect(Collectors.toList()));
            } else if (programNumber == null) {
                creditCardPayments.addAll(repository.findAllByDateOfPaymentBetweenAndTransactionId(fromDate, toDate, trxId)
                        .stream().filter(o -> o.getPaymentMode().equals(ChargedServicePaymentMode.CARD)).map(CreditCardReportDTO::new).collect(Collectors.toList()));
            } else {
                creditCardPayments.addAll(repository.findAllByDateOfPaymentBetweenAndRegistrationCongressProgramNumberAndTransactionId(fromDate, toDate, programNumber, trxId)
                        .stream().filter(o -> o.getPaymentMode().equals(ChargedServicePaymentMode.CARD)).map(CreditCardReportDTO::new).collect(Collectors.toList()));
            }
        }

        return creditCardPayments;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(List<CreditCardReportDTO> dtos) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();
        columns.put("Reg Id", 100);
        columns.put("Name", 100);
        columns.put("Amount", 100);
        columns.put("Currency", 100);
        columns.put("Date of payment", 100);
        columns.put("Card number", 100);
        columns.put("Transaction ID", 100);
        columns.put("Congress name", 100);
        columns.put("Program number", 100);
        columns.put("Comment", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Credit card report", null, "", getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (CreditCardReportDTO dto : dtos) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getRegId());
            addCell(row, wrappingCellStyle, 1, dto.getName());
            addCell(row, wrappingCellStyle, 2, dto.getAmount());
            addCell(row, wrappingCellStyle, 3, dto.getCurrency());
            addCell(row, wrappingCellStyle, 4, dto.getDateOfPayment());
            addCell(row, wrappingCellStyle, 5, dto.getCardNumber());
            addCell(row, wrappingCellStyle, 6, dto.getTransactionId());
            addCell(row, wrappingCellStyle, 7, dto.getCongressName());
            addCell(row, wrappingCellStyle, 8, dto.getProgramNumber());
            addCell(row, wrappingCellStyle, 9, dto.getComment());
            rowIndex++;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occurred while creating the credit card report XLSX file", e);
            throw e;
        }
    }
}
