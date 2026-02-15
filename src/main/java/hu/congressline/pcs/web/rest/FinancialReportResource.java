package hu.congressline.pcs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import hu.congressline.pcs.service.FinancialReportService;
import hu.congressline.pcs.service.dto.FinancialReportDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.vm.FinancialReportVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FinancialReportResource {

    private final FinancialReportService service;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/financial-report")
    public List<FinancialReportDTO> get(@RequestParam String query) throws IOException {
        log.debug("REST request to get all registration with financial data");
        ObjectMapper mapper = new ObjectMapper();
        FinancialReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), FinancialReportVM.class);
        return service.findAll(reportFilter);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/financial-report/download")
    public ResponseEntity<byte[]> download(@RequestParam String query) throws IOException {
        log.debug("REST request to download FinancialReport");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        FinancialReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), FinancialReportVM.class);
        final List<FinancialReportDTO> dtos = service.findAll(reportFilter);
        if (dtos.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                final Congress congress = congressService.getById(Long.valueOf(reportFilter.getCongressId()));
                reportXlsx = service.downloadReportXls(congress, dtos);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
                String filename = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + "-financial-report.xlsx";
                headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", filename));
                return new ResponseEntity<>(reportXlsx, headers, HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occurred while creating room reservation by rooms report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

}
