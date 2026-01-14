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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.repository.OptionalServiceRepository;
import hu.congressline.pcs.service.OptionalServiceApplicantsReportService;
import hu.congressline.pcs.service.dto.OptionalServiceApplicantsDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionalServiceApplicantsReportResource {

    private final OptionalServiceApplicantsReportService service;
    private final OptionalServiceRepository optionalServiceRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-service-applicants/{optionalServiceId}")
    public List<OptionalServiceApplicantsDTO> getAllBy(@PathVariable Long optionalServiceId) {
        log.debug("REST request to get all OptionalServiceApplicantsReports");
        List<OptionalServiceApplicantsDTO> result = new ArrayList<>();
        optionalServiceRepository.findById(optionalServiceId).ifPresent(optionalService -> {
            result.addAll(service.findAll(optionalService));
        });
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-service-applicants/{optionalServiceId}/download-report")
    public ResponseEntity<byte[]> downloadReportXls(@PathVariable Long optionalServiceId) {
        log.debug("REST request to download OptionalServiceApplicantsReport : {}", optionalServiceId);
        byte[] reportXlsx = new byte[0];

        Optional<OptionalService> optionalService = optionalServiceRepository.findById(optionalServiceId);
        if (optionalService.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                reportXlsx = service.downloadReportXls(optionalService.orElse(null));
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                String filename = ServiceUtil.normalizeForFilename(optionalService.get().getName()) + "-optional-service-applicants-report.xlsx";
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
                return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occured while creating optional service by applicants report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
