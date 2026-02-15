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
import hu.congressline.pcs.service.RegFeeDetailsReportService;
import hu.congressline.pcs.service.dto.RegFeeDetailsDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RegFeeDetailsReportResource {

    private final RegFeeDetailsReportService service;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/regfee-details/{congressId}")
    public List<RegFeeDetailsDTO> getBy(@PathVariable Long congressId) {
        log.debug("REST request to get the Registration fee details by congress id: {}", congressId);
        return service.findAll(congressId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/regfee-details/{congressId}/download-report")
    public ResponseEntity<byte[]> downloadXls(@PathVariable Long congressId) throws IOException {
        Congress congress = congressService.getById(congressId);
        byte[] reportXlsx = new byte[0];
        try {
            reportXlsx = service.downloadReportXls(congress);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            final String fileName = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + "-regfee-details-by-participant-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occurred while creating Regfee details report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
