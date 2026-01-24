package hu.congressline.pcs.service;

import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.config.ApplicationProperties;
import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.PaymentRefundTransaction;
import hu.congressline.pcs.domain.PaymentTransaction;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.repository.PaymentRefundTransactionRepository;
import hu.congressline.pcs.repository.PaymentTransactionRepository;
import hu.congressline.pcs.service.dto.PaymentRefundTransactionDTO;
import hu.congressline.pcs.service.dto.PaymentTransactionReportDTO;
import hu.congressline.pcs.web.rest.vm.PaymentTransactionReportVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PaymentTransactionService extends XlsReportService {

    private ApplicationProperties properties;
    private PaymentTransactionRepository repository;
    private PaymentRefundTransactionRepository refundRepository;
    private OnlineRegService onlineRegService;

    @SuppressWarnings("MissingJavadocMethod")
    public void createPaymentTransaction(OnlineRegistration or) {
        BigDecimal regSubTotal = onlineRegService.getRegistrationTypeSubTotalAmountOfOnlineReg(or);
        BigDecimal roomSubTotal = onlineRegService.getHotelAmountOfOnlineReg(or);
        BigDecimal osSubTotal = onlineRegService.getOptionalServiceTotalAmountOfOnlineReg(or);

        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setAmount(regSubTotal.add(roomSubTotal).add(osSubTotal));
        paymentTransaction.setCurrency(or.getCurrency());
        paymentTransaction.setPaymentOrderNumber(or.getPaymentOrderNumber());
        paymentTransaction.setTransactionId(or.getPaymentTrxId());
        paymentTransaction.setMerchantId(Currency.HUF.toString().equalsIgnoreCase(or.getCurrency())
                ? properties.getPayment().getGateway().getMerchantIdForHUF() : properties.getPayment().getGateway().getMerchantIdForEUR());
        paymentTransaction.setPaymentTrxStatus(or.getPaymentTrxStatus());
        paymentTransaction.setPaymentTrxResultCode(or.getPaymentTrxResultCode());
        paymentTransaction.setPaymentTrxResultMessage(or.getPaymentTrxResultMessage());
        paymentTransaction.setPaymentTrxAuthCode(or.getPaymentTrxAuthCode());
        paymentTransaction.setPaymentTrxDate(or.getPaymentTrxDate());
        paymentTransaction.setBankAuthNumber(or.getBankAuthNumber());
        paymentTransaction.setTitle(or.getTitle());
        paymentTransaction.setLastName(or.getLastName());
        paymentTransaction.setFirstName(or.getFirstName());
        paymentTransaction.setEmail(or.getEmail());
        paymentTransaction.setCongress(or.getCongress());
        repository.save(paymentTransaction);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<PaymentTransactionReportDTO> findAll(PaymentTransactionReportVM reportFilter) {
        log.debug("Request to get all PaymentTransactionReportDTO");
        final List<PaymentTransactionReportDTO> paymentTransactionPayments = new ArrayList<>();
        final String orderNumber = reportFilter.getOrderNumber();
        final String transactionId = reportFilter.getTransactionId();
        ZonedDateTime defaultFromDate = ZonedDateTime.of(LocalDate.of(1900, Month.JANUARY, 1), LocalTime.MIN, ZoneId.systemDefault());
        ZonedDateTime defaultToDate = ZonedDateTime.of(LocalDate.of(2200, Month.JANUARY, 1), LocalTime.MIN, ZoneId.systemDefault());
        ZonedDateTime fromDate = reportFilter.getFromDate() != null ? ZonedDateTime.of(reportFilter.getFromDate(), LocalTime.MIN, ZoneId.systemDefault()) : defaultFromDate;
        ZonedDateTime toDate = reportFilter.getToDate() != null ? ZonedDateTime.of(reportFilter.getToDate(), LocalTime.MAX, ZoneId.systemDefault()) : defaultToDate;
        if (StringUtils.hasText(transactionId) && StringUtils.hasText(orderNumber)) {
            paymentTransactionPayments.addAll(repository.findAllByPaymentTrxDateBetweenAndTransactionIdAndPaymentOrderNumber(fromDate, toDate, transactionId, orderNumber)
                    .stream().map(PaymentTransactionReportDTO::new).collect(Collectors.toList()));
        } else if (StringUtils.hasText(transactionId) && !StringUtils.hasText(orderNumber)) {
            paymentTransactionPayments.addAll(repository.findAllByPaymentTrxDateBetweenAndTransactionId(fromDate, toDate, transactionId)
                    .stream().map(PaymentTransactionReportDTO::new).collect(Collectors.toList()));
        } else if (!StringUtils.hasText(transactionId) && StringUtils.hasText(orderNumber)) {
            paymentTransactionPayments.addAll(repository.findAllByPaymentTrxDateBetweenAndPaymentOrderNumber(fromDate, toDate, orderNumber)
                    .stream().map(PaymentTransactionReportDTO::new).collect(Collectors.toList()));
        } else {
            paymentTransactionPayments.addAll(repository.findAllByPaymentTrxDateBetween(fromDate, toDate)
                    .stream().map(PaymentTransactionReportDTO::new).collect(Collectors.toList()));
        }

        final Set<String> refundTrxIds = refundRepository.findByTransactionIdIn(paymentTransactionPayments
                .stream().map(PaymentTransactionReportDTO::getTransactionId).collect(Collectors.toSet())).stream().map(PaymentRefundTransaction::getTransactionId)
                .collect(Collectors.toSet());

        paymentTransactionPayments.stream().filter(pt -> refundTrxIds.contains(pt.getTransactionId())).forEach(pt -> pt.setHasRefundTransaction(true));
        return paymentTransactionPayments;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<PaymentTransaction> findById(Long id) {
        log.debug("Request to find PaymentTransaction : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public PaymentTransaction getById(Long id) {
        log.debug("Request to get Registration : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("PaymentTransaction not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<PaymentTransaction> findByTransactionId(String trxId) {
        log.debug("Request to find PaymentTransaction by trx id: {}", trxId);
        return repository.findOneByTransactionId(trxId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public PaymentTransaction getByTransactionId(String trxId) {
        log.debug("Request to get PaymentTransaction by trx id: {}", trxId);
        return repository.findOneByTransactionId(trxId).orElseThrow(() -> new IllegalArgumentException("PaymentTransaction not found with trx id: " + trxId));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public byte[] downloadReportXls(List<PaymentTransactionReportDTO> dtos) throws IOException {
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Map<String, Integer> columns = new LinkedHashMap<>();

        columns.put("Payment trx date", 100);
        columns.put("Order No", 100);
        columns.put("Transaction ID", 100);
        columns.put("Amount", 100);
        columns.put("Currency", 100);
        columns.put("Congress name", 100);

        final XSSFSheet sheet = createXlsxTab(workbook, "Payment transaction report", null, "", getColumnWidthsAsArray(columns));
        addSubHeader(sheet, columns);

        XSSFCellStyle wrappingCellStyle = workbook.createCellStyle();
        wrappingCellStyle.setWrapText(true);

        int rowIndex = 4;
        for (PaymentTransactionReportDTO dto : dtos) {
            final XSSFRow row = sheet.createRow(rowIndex);
            addCell(row, wrappingCellStyle, 0, dto.getPaymentTrxDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            addCell(row, wrappingCellStyle, 1, dto.getOrderNumber());
            addCell(row, wrappingCellStyle, 2, dto.getTransactionId());
            addCell(row, wrappingCellStyle, 3, dto.getAmount());
            addCell(row, wrappingCellStyle, 4, dto.getCurrency());
            addCell(row, wrappingCellStyle, 5, dto.getCongressName());
            rowIndex++;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("An error occured while creating the payment transaction report XLSX file", e);
            throw e;
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<PaymentRefundTransactionDTO> findAllPaymentRefundTransactionByTrxId(String trxId) {
        return refundRepository.findByTransactionId(trxId).stream().map(PaymentRefundTransactionDTO::new).collect(Collectors.toList());
    }
}
