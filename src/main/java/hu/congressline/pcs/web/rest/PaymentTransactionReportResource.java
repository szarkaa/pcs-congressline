package hu.congressline.pcs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import hu.congressline.pcs.domain.PaymentTransaction;
import hu.congressline.pcs.service.PaymentTransactionService;
import hu.congressline.pcs.service.dto.PaymentRefundTransactionDTO;
import hu.congressline.pcs.service.dto.PaymentTransactionReportDTO;
import hu.congressline.pcs.web.rest.vm.PaymentRefundTransactionVM;
import hu.congressline.pcs.web.rest.vm.PaymentTransactionReportVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PaymentTransactionReportResource {

    private final PaymentTransactionService service;
    //private final PaymentRefundService paymentRefundService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/payment-transaction-report/refunds")
    public ResponseEntity<Void> refundPayment(@Valid @RequestBody PaymentRefundTransactionVM vm) {
        log.debug("REST request to refund a payment transaction");
        //paymentRefundService.refundPayment(vm.getTxId(), vm.getAmount(), vm.getCurrency());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment-transaction-report")
    public List<PaymentTransactionReportDTO> getAllReportRows(@RequestParam String query) throws IOException {
        log.debug("REST request to get all PaymentTransactionReportDTO");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        PaymentTransactionReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), PaymentTransactionReportVM.class);
        return service.findAll(reportFilter);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment-transaction-report/{trxId}/refunds")
    public List<PaymentRefundTransactionDTO> getAllRefundRowsByTrxId(@PathVariable String trxId) {
        log.debug("REST request to get all PaymentRefundTransactionDTO by trx id");
        return service.findAllPaymentRefundTransactionByTrxId(trxId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment-transaction-report/{id}")
    public ResponseEntity<PaymentTransaction> getById(@PathVariable Long id) {
        log.debug("REST request to get PaymentTransactionReport : {}", id);
        return service.findById(id)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment-transaction-report/download-report")
    public ResponseEntity<byte[]> downloadXls(@RequestParam String query) throws IOException {
        log.debug("REST request to download PaymentTransactionReport");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        PaymentTransactionReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), PaymentTransactionReportVM.class);
        final List<PaymentTransactionReportDTO> dtos = service.findAll(reportFilter);
        try {
            reportXlsx = service.downloadReportXls(dtos);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = "payment-transaction-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating general report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
