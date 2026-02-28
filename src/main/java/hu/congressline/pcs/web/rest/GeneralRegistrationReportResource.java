package hu.congressline.pcs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.GeneralRegistrationReportService;
import hu.congressline.pcs.service.MailService;
import hu.congressline.pcs.service.RegistrationService;
import hu.congressline.pcs.service.dto.GeneralRegistrationReportDTO;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.GeneralRegistrationReportVM;
import hu.congressline.pcs.web.rest.vm.SendGeneralEmailToAllVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GeneralRegistrationReportResource {
    private static final String FILENAME_SUFFIX = "-general-report.xlsx";

    private final GeneralRegistrationReportService service;
    private final RegistrationService registrationService;
    private final CongressService congressService;
    private final MailService mailService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/general-registration-report/send-general-email-to-all")
    public ResponseEntity<Void> sendGeneralEmailToAll(@RequestBody SendGeneralEmailToAllVM vm) {
        log.debug("REST request to send email to all registered in the filtered list");
        final Congress congress = congressService.getById(vm.getCongressId());

        final List<Registration> registrations = registrationService.findAllByCongressIdAndIds(vm.getCongressId(), vm.getRegistrationIds());
        registrations.stream().filter(dto -> StringUtils.hasText(dto.getEmail())).forEach(dto -> {
            mailService.sendEmail(congress.getContactEmail(), null, dto.getEmail(), congress.getContactEmail(), "Congressline - " + congress.getMeetingCode(),
                vm.getEmailBody(), false, true);
        });

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert("sendGeneralEmailToAllDialog", String.valueOf(registrations.size()))).build();

    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/general-registration-report")
    public List<GeneralRegistrationReportDTO> get(@RequestParam String query) throws IOException {
        log.debug("REST request to get all general registration report rows");
        ObjectMapper mapper = new ObjectMapper();
        GeneralRegistrationReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), GeneralRegistrationReportVM.class);
        return service.findAll(reportFilter);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/general-registration-report/download-report")
    public ResponseEntity<byte[]> download(@RequestParam String query) throws IOException {
        log.debug("REST request to download general registration report rows");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        GeneralRegistrationReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), GeneralRegistrationReportVM.class);
        final List<GeneralRegistrationReportDTO> dtos = service.findAll(reportFilter);
        if (dtos.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                Congress congress = congressService.getById(Long.valueOf(reportFilter.getCongressId()));
                reportXlsx = service.downloadReportXls(reportFilter.getCongressId(), dtos, false);
                String fileName = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + FILENAME_SUFFIX;
                return new ResponseEntity<>(reportXlsx, createHeader(fileName), HttpStatus.OK);
            } catch (IOException | WriterException e) {
                log.error("An error occurred while creating general report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/general-registration-report/download-report-with-qrcode")
    public ResponseEntity<byte[]> downloadWithQRCode(@RequestParam String query) throws IOException {
        log.debug("REST request to download general registration report rows with qr code");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        GeneralRegistrationReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), GeneralRegistrationReportVM.class);
        final List<GeneralRegistrationReportDTO> dtos = service.findAll(reportFilter);
        if (dtos.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                Congress congress = congressService.getById(Long.valueOf(reportFilter.getCongressId()));
                reportXlsx = service.downloadReportXls(reportFilter.getCongressId(), dtos, true);
                String fileName = ServiceUtil.normalizeForFilename(congress.getMeetingCode()) + FILENAME_SUFFIX;
                return new ResponseEntity<>(reportXlsx, createHeader(fileName), HttpStatus.OK);
            } catch (IOException | WriterException e) {
                log.error("An error occurred while creating general report XLSX with qr code", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    private HttpHeaders createHeader(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add("Content-Disposition", String.format("attachment; fileName=\"%s\"", fileName));
        return headers;
    }
}
