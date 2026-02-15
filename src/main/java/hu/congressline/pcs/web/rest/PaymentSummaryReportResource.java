package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.PaymentSummaryReportService;
import hu.congressline.pcs.service.dto.PaymentSummaryDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PaymentSummaryReportResource {

    private final PaymentSummaryReportService service;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment-summary/{congressId}")
    public List<PaymentSummaryDTO> getByCongressId(@PathVariable Long congressId) {
        log.debug("REST request to get the PaymentSummaryDTO congress id: {}", congressId);
        Congress congress = congressService.getEagerById(congressId);
        return service.findAll(congress);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/payment-summary/{congressId}/download-report")
    public ResponseEntity<byte[]> downloadXls(@PathVariable Long congressId) {
        log.debug("REST request to download PaymentSummaryDTO congress id: {}", congressId);
        byte[] reportXlsx = new byte[0];

        Congress congress = congressService.getEagerById(congressId);
        try {
            reportXlsx = service.downloadReportXls(congress);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            final String fileName = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + "-payment-summary-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occurred while creating room reservation by rooms report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
