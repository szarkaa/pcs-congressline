package hu.congressline.pcs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.RegFeeDetailsByParticipantReportService;
import hu.congressline.pcs.service.dto.RegFeeDetailsByParticipantDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.vm.RegFeeDetailsByParticipantsVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RegFeeDetailsByParticipantReportResource {

    private final RegFeeDetailsByParticipantReportService service;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/regfee-details-by-participant")
    public List<RegFeeDetailsByParticipantDTO> getRegFeeDetailsByParticipants(@RequestParam String query) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        RegFeeDetailsByParticipantsVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), RegFeeDetailsByParticipantsVM.class);

        log.debug("REST request to get the Registration fee details by participant by congress id: {}", reportFilter.getCongressId());
        return service.findAll(reportFilter);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/regfee-details-by-participant/download-report")
    public ResponseEntity<byte[]> downloadXls(@RequestParam String query) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        RegFeeDetailsByParticipantsVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), RegFeeDetailsByParticipantsVM.class);

        Congress congress = congressService.getById(reportFilter.getCongressId());
        byte[] reportXlsx = new byte[0];
        try {
            reportXlsx = service.downloadReportXls(reportFilter);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            final String fileName = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + "-regfee-details-by-participant-report.xlsx";
            headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("An error occured while creating Regfee details by participants report XLSX", e);
            return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
