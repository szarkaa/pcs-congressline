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

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.service.HotelGeneralReportService;
import hu.congressline.pcs.service.dto.HotelGeneralReportDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class HotelGeneralReportResource {

    private final HotelGeneralReportService service;
    private final CongressRepository congressRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/hotel-general-report/{meetingCode}")
    public List<HotelGeneralReportDTO> getReport(@PathVariable String meetingCode) {
        log.debug("REST request to get all registration with hotel general data");
        List<HotelGeneralReportDTO> reports = new ArrayList<>();
        congressRepository.findOneByMeetingCode(meetingCode).ifPresent(congress -> reports.addAll(service.findAll(congress)));
        return reports;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/hotel-general-report/{meetingCode}/download-report")
    public ResponseEntity<byte[]> downloadXls(@PathVariable String meetingCode) {
        log.debug("REST request to download HotelGeneralReport xls by meeting code: {}", meetingCode);
        byte[] reportXlsx = new byte[0];

        Optional<Congress> congress = congressRepository.findOneByMeetingCode(meetingCode);
        if (congress.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                reportXlsx = service.downloadReportXls(congress.get());
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                String filename = ServiceUtil.normalizeForFilename(congress.get().getMeetingCode()) + "-hotel-general-report.xlsx";
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
                return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occured while creating hotel general report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
