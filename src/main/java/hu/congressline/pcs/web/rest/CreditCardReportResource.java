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

import hu.congressline.pcs.service.CreditCardReportService;
import hu.congressline.pcs.service.dto.CreditCardReportDTO;
import hu.congressline.pcs.web.rest.vm.CreditCardReportVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CreditCardReportResource {

    private final CreditCardReportService service;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/credit-card-report")
    public List<CreditCardReportDTO> get(@RequestParam String query) throws IOException {
        log.debug("REST request to get all CreditCardReportDTO");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        CreditCardReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), CreditCardReportVM.class);
        return service.findAll(reportFilter);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/credit-card-report/download-report")
    public ResponseEntity<byte[]> download(@RequestParam String query) throws IOException {
        log.debug("REST request to download RoomReservationByRoomsReport");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        CreditCardReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), CreditCardReportVM.class);
        final List<CreditCardReportDTO> dtos = service.findAll(reportFilter);
        if (dtos.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                reportXlsx = service.downloadReportXls(dtos);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                String filename = "credit-card-report.xlsx";
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
                return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occured while creating general report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
