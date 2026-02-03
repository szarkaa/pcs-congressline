package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.service.GroupDiscountInvoicePdfService;
import hu.congressline.pcs.service.GroupDiscountInvoiceService;
import hu.congressline.pcs.service.GroupDiscountInvoiceXlsService;
import hu.congressline.pcs.service.NavOnlineService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.GroupDiscountInvoiceVM;
import hu.congressline.pcs.web.rest.vm.SetPaymentDateVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GroupDiscountInvoiceResource {
    private static final String ENTITY_NAME = "groupDiscountInvoice";
    private static final String URI = "/api/group-discount-invoices/";

    private final GroupDiscountInvoiceService invoiceService;
    private final GroupDiscountInvoiceXlsService groupDiscountInvoiceXlsService;
    private final GroupDiscountInvoicePdfService invoicePdfService;
    //private final MailService mailService;
    private final NavOnlineService navOnlineService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/group-discount-invoices")
    public ResponseEntity<InvoicePayingGroup> createGroupDiscountInvoice(@Valid @RequestBody GroupDiscountInvoiceVM invoiceVM) throws URISyntaxException {
        log.debug("REST request to save Invoice : {}", invoiceVM);
        InvoicePayingGroup result = invoiceService.save(invoiceVM);
        if (!InvoiceType.PRO_FORMA.equals(result.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(result.getInvoice().getId());
        }
        return ResponseEntity.created(new URI(URI + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/group-discount-invoices/save-and-send-email")
    public ResponseEntity<InvoicePayingGroup> createInvoiceAndSendEmail(@Valid @RequestBody GroupDiscountInvoiceVM invoiceVM) throws URISyntaxException {
        log.debug("REST request to save GroupDiscountInvoice and send it via mail: {}", invoiceVM);
        InvoicePayingGroup result = invoiceService.save(invoiceVM);
        if (!InvoiceType.PRO_FORMA.equals(result.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(result.getInvoice().getId());
        }
        String from = result.getPayingGroup().getCongress().getContactEmail();
        byte[] pdfBytes = invoicePdfService.generatePdf(invoicePdfService.createInvoicePdfContext(result));
        //mailService.sendGroupDiscountInvoicePdfEmail(new Locale(invoiceVM.getLanguage()), from, invoiceVM.getCustomInvoiceEmail(), invoiceVM.getPayingGroup(), pdfBytes);
        return ResponseEntity.created(new URI(URI + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping(value = "/group-discount-invoices/set-payment-date", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Invoice> setPaymentDate(@Valid @RequestBody SetPaymentDateVM groupSetPaymentDateVM) {
        log.debug("REST request to set payment date of GroupDiscountInvoice : {}", groupSetPaymentDateVM.getId());
        InvoicePayingGroup invoice = invoiceService.setPaymentDate(groupSetPaymentDateVM);

        return Optional.ofNullable(invoice)
            .map(result -> new ResponseEntity<>(result.getInvoice(), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-invoices/congress/{id}")
    public List<InvoicePayingGroup> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all Invoices of GroupDiscount by congress id: {}", id);
        return invoiceService.findByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-invoices/{id}")
     public ResponseEntity<Invoice> get(@PathVariable Long id) {
        log.debug("REST request to get InvoicePayingGroup : {}", id);
        InvoicePayingGroup groupDiscountInvoice = invoiceService.findOne(id);
        return Optional.ofNullable(groupDiscountInvoice)
            .map(result -> new ResponseEntity<>(result.getInvoice(), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping(value = "/group-discount-invoices/{invoiceId}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long invoiceId) {
        InvoicePayingGroup invoice = invoiceService.findOne(invoiceId);
        byte[] pdfBytes = invoicePdfService.generatePdf(invoicePdfService.createInvoicePdfContext(invoice));
        return ResponseEntity
                .ok()
                .headers(createHeaders("group-discount-invoice.pdf"))
                .contentLength(pdfBytes.length)
                //.contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping(value = "/group-discount-invoices/{invoiceId}/details/xls", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    public ResponseEntity<byte[]> getInvoiceDetailsInXls(@PathVariable Long invoiceId) throws IOException {
        InvoicePayingGroup invoicePayingGroup = invoiceService.findOne(invoiceId);
        byte[] xlsBytes = groupDiscountInvoiceXlsService.downloadInvoiceReportXls(invoicePayingGroup);

        return ResponseEntity
                .ok()
                .headers(createHeaders("group-discount-invoice-details.xlsx"))
                .contentLength(xlsBytes.length)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(xlsBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping(value = "/group-discount-invoices/{id}/storno", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Invoice> stornoInvoice(@PathVariable Long id) {
        log.debug("REST request to storno GroupDiscountInvoice : {}", id);
        Invoice invoice = invoiceService.stornoInvoice(id);
        if (invoice != null && !InvoiceType.PRO_FORMA.equals(invoice.getInvoiceType())) {
            navOnlineService.postInvoiceToNav(invoice.getId());
        }

        return Optional.ofNullable(invoice)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private HttpHeaders createHeaders(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "inline;filename=" + fileName);
        return headers;
    }
}
