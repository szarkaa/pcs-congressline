package hu.congressline.pcs.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.service.InvoiceReportService;
import hu.congressline.pcs.service.dto.InvoiceReportDTO;
import hu.congressline.pcs.service.dto.online.InvoiceNavValidationDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.InvoiceReportVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class InvoiceReportResource {
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String XLSX_SUFFIX = ".xlsx";
    private final InvoiceReportService service;
    //private final NavOnlineService navOnlineService;

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoice-report")
    public List<InvoiceReportDTO> getReport(@RequestParam String query) throws IOException {
        log.debug("REST request to get all InvoiceReportDTO");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        InvoiceReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), InvoiceReportVM.class);
        return service.findAllDTOS(reportFilter);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoice-report/show-nav-sending-status/{id}")
    public List<InvoiceNavValidationDTO> getAllNavStatus(@PathVariable Long id) {
        log.debug("REST request to get all InvoiceNavStatus record of invoice id: {}", id);
        return service.getAllInvoiceNavValidationById(id).stream().map(InvoiceNavValidationDTO::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoice-report/download-nav-xml-archive")
    public ResponseEntity<byte[]> downloadNavXmlArchive(@RequestParam String query) throws IOException {
        log.debug("REST request to download Nav xml archive");
        ObjectMapper mapper = new ObjectMapper();
        InvoiceReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), InvoiceReportVM.class);
        final List<Invoice> invoices = service.findAll(reportFilter);
        if (invoices.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        }

        final HttpHeaders headers = createHeader();
        headers.add(CONTENT_DISPOSITION, "attachment; filename=nav-xml-archive.zip");
        final byte[] zippedContent = service.downloadNavXmlArchive(invoices);
        return ResponseEntity.ok().headers(headers).contentLength(zippedContent.length)
                .contentType(MediaType.valueOf("application/zip")).body(zippedContent);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoice-report/download-report")
    public ResponseEntity<byte[]> downloadReportXls(@RequestParam String query) throws IOException {
        log.debug("REST request to download invoice report");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        InvoiceReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), InvoiceReportVM.class);
        final List<InvoiceReportDTO> dtos = service.findAllDTOS(reportFilter);
        if (dtos.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                reportXlsx = service.downloadReportXls(dtos);
                String fileName = "invoice-report-" + timestamp() + XLSX_SUFFIX;
                return new ResponseEntity<>(reportXlsx, createXlsHeader(fileName), HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occured while creating general report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoice-report/download-accountant-report")
    public ResponseEntity<byte[]> downloadAccountantReportXls(@RequestParam String query) throws IOException {
        log.debug("REST request to download RoomReservationByRoomsReport");
        byte[] reportXlsx = new byte[0];
        ObjectMapper mapper = new ObjectMapper();
        InvoiceReportVM reportFilter = mapper.readValue(new String(Base64.getDecoder().decode(query)), InvoiceReportVM.class);
        final List<InvoiceReportDTO> dtos = service.findAllDTOS(reportFilter);
        if (dtos.isEmpty()) {
            return new ResponseEntity<>((byte[]) null, HttpStatus.NOT_FOUND);
        } else {
            try {
                reportXlsx = service.downloadAccountantReportXls(dtos);
                String fileName = "invoice-accountant-report-" + timestamp() + XLSX_SUFFIX;
                return new ResponseEntity<>(reportXlsx, createXlsHeader(fileName), HttpStatus.OK);
            } catch (IOException e) {
                log.error("An error occured while creating accountant report XLSX", e);
                return new ResponseEntity<>(reportXlsx, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping(value = "/invoice-report/{invoiceId}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long invoiceId) {
        byte[] pdfBytes = service.createPdfByInvoiceId(invoiceId);

        HttpHeaders headers = createHeader();
        headers.add(CONTENT_DISPOSITION, "inline; filename=invoice.pdf");
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(pdfBytes.length)
                //.contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoice-report/send-to-nav/{id}")
    public ResponseEntity<Void> sendInvoiceToNav(@PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to send invoice i: {} to NAV", id);
        final Invoice invoice = null; //navOnlineService.postInvoiceToNav(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("invoiceReport", invoice.getInvoiceNumber())).build();
    }

    private HttpHeaders createXlsHeader(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.add(CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"", fileName));
        return headers;
    }

    private HttpHeaders createHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }

    private String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}
