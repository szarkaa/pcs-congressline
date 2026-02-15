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

import hu.congressline.pcs.service.GroupDiscountInvoiceXlsService;
import hu.congressline.pcs.service.GroupDiscountItemService;
import hu.congressline.pcs.service.dto.GroupDiscountItemDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GroupDiscountItemResource {
    private static final String ALL = "ALL";

    private final GroupDiscountItemService service;
    private final GroupDiscountInvoiceXlsService xlsService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-items/{meetingCode}/{payingGroupId}")
    public List<GroupDiscountItemDTO> getAllBy(@PathVariable String meetingCode, @PathVariable Long payingGroupId) {
        log.debug("REST request to get all GroupDiscountItems by meeting code {} and payingGroupId {}", meetingCode, payingGroupId);
        return service.findAll(meetingCode, payingGroupId, null);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/group-discount-items/{meetingCode}/{payingGroupId}/{chargeableItemType}")
    public List<GroupDiscountItemDTO> getAllBy(@PathVariable String meetingCode, @PathVariable Long payingGroupId, @PathVariable String chargeableItemType) {
        log.debug("REST request to get all GroupDiscountItems meetingCode {} payingGroupId {} chargeableItemType {}", meetingCode, payingGroupId, chargeableItemType);
        return service.findAll(meetingCode, payingGroupId, chargeableItemType);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-items/{meetingCode}/{payingGroupId}/{chargeableItemType}/download-report")
    public ResponseEntity<byte[]> downloadXls(@PathVariable String meetingCode, @PathVariable Long payingGroupId, @PathVariable String chargeableItemType) {
        log.debug("REST request to get all GroupDiscountItems XLS report");
        byte[] reportXlsx = new byte[0];

        try {
            reportXlsx = xlsService.downloadInvoiceReportXls(meetingCode, payingGroupId, ALL.equals(chargeableItemType) ? null : chargeableItemType);
            String fileName = ServiceUtil.normalizeForFilename(meetingCode) + "-group-discount-report.xlsx";
            return new ResponseEntity<>(reportXlsx, createHeaders(fileName), HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occurred while creating group invoice report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-items/{meetingCode}/{payingGroupId}/{chargeableItemType}/download-pro-forma-group-invoice-report")
    public ResponseEntity<byte[]> downloadProFormaXls(@PathVariable String meetingCode, @PathVariable Long payingGroupId, @PathVariable String chargeableItemType) {
        log.debug("REST request to get Pro forma group invoice xls report");
        byte[] reportXlsx = new byte[0];

        try {
            reportXlsx = xlsService.downloadProFormaInvoiceReportXls(meetingCode, payingGroupId, ALL.equals(chargeableItemType) ? null : chargeableItemType);
            String fileName = ServiceUtil.normalizeForFilename(meetingCode) + "-pro-forma-group-invoice-report.xlsx";
            return new ResponseEntity<>(reportXlsx, createHeaders(fileName), HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occurred while creating pro forma group invoice report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders createHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
        return headers;
    }
}
