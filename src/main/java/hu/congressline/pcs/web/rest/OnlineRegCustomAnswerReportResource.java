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
import hu.congressline.pcs.service.OnlineRegCustomAnswerReportService;
import hu.congressline.pcs.service.dto.OnlineRegCustomAnswerDTO;
import hu.congressline.pcs.service.dto.OnlineRegCustomAnswerReportDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OnlineRegCustomAnswerReportResource {

    private final OnlineRegCustomAnswerReportService service;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-custom-answer-report/registration/{registrationId}")
    public List<OnlineRegCustomAnswerDTO> getAllAnswersByRegId(@PathVariable Long registrationId) {
        log.debug("REST request to get all custom answers by reg id: {}", registrationId);
        return service.getAllCustomAnswersByRegId(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-custom-answer-report/{meetingCode}/{currency}")
    public List<OnlineRegCustomAnswerReportDTO> getReport(@PathVariable String meetingCode, @PathVariable String currency) {
        log.debug("REST request to get all registration with hotel general data");
        Congress congress = congressService.getByMeetingCode(meetingCode);
        return service.findAll(congress, currency);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-custom-answer-report/{meetingCode}/{currency}/download-report")
    public ResponseEntity<byte[]> downloadXls(@PathVariable String meetingCode, @PathVariable String currency) {
        log.debug("REST request to download OnlineRegCustomAnswerReport : {}, {}", meetingCode, currency);
        byte[] reportXlsx = new byte[0];
        Congress congress = congressService.getByMeetingCode(meetingCode);
        try {
            reportXlsx = service.downloadReportXls(congress, currency);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String filename = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + "-" + currency + "-online-reg-custom-answer-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating hotel general report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
