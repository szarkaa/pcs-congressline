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
import java.util.Set;

import hu.congressline.pcs.service.OptionalServiceApplicantsReportService;
import hu.congressline.pcs.service.dto.OptionalServiceApplicantsDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionalServiceApplicantsReportResource {

    private final OptionalServiceApplicantsReportService service;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-service-applicants/{optionalServiceIds}")
    public List<OptionalServiceApplicantsDTO> getAllBy(@PathVariable Set<Long> optionalServiceIds) {
        log.debug("REST request to get all OptionalServiceApplicantsReports");
        return service.findAll(optionalServiceIds);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-service-applicants/{optionalServiceIds}/download-report")
    public ResponseEntity<byte[]> downloadReportXls(@PathVariable Set<Long> optionalServiceIds) {
        log.debug("REST request to download OptionalServiceApplicantsReport : {}", optionalServiceIds);
        byte[] reportXlsx = new byte[0];

        if (optionalServiceIds.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                reportXlsx = service.downloadReportXls(optionalServiceIds);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                //String filename = ServiceUtil.normalizeForFilename(optionalService.get().getName()) + "-optional-service-applicants-report.xlsx";
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", "optional-service-applicants-report.xlsx"));
                return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occurred while creating optional service by applicants report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
