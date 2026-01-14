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
import hu.congressline.pcs.service.OptionalProgramsMembersReportService;
import hu.congressline.pcs.service.dto.OptionalProgramsMembersDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionalProgramsMembersReportResource {

    private final OptionalProgramsMembersReportService service;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-program-members/{congressId}")
    public List<OptionalProgramsMembersDTO> getAllBy(@PathVariable Long congressId) {
        log.debug("REST request to get all OptionalProgramsMembersReports");
        Congress congress = congressService.getById(congressId);
        return service.findAll(congressId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-program-members/{congressId}/download-report")
    public ResponseEntity<byte[]> downloadXls(@PathVariable Long congressId) {
        log.debug("REST request to download OptionalProgramsMembersReport : {}", congressId);
        byte[] reportXlsx = new byte[0];

        Congress congress = congressService.getById(congressId);
        try {
            reportXlsx = service.downloadReportXls(congress);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = ServiceUtil.normalizeForFilename(congress.getName()) + "-optional-program-members-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating optional programs members report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
