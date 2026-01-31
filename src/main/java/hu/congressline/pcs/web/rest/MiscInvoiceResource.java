package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.MailService;
import hu.congressline.pcs.service.MiscInvoicePdfService;
import hu.congressline.pcs.service.MiscInvoiceService;
import hu.congressline.pcs.service.NavOnlineService;
import hu.congressline.pcs.service.dto.InvoiceDTO;
import hu.congressline.pcs.service.dto.MiscInvoiceDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.MiscInvoiceVM;
import hu.congressline.pcs.web.rest.vm.SetPaymentDateVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MiscInvoiceResource {

    private final MiscInvoiceService miscInvoiceService;
    private final MiscInvoicePdfService pdfService;
    private final CongressService congressService;
    private final MailService mailService;
    private final NavOnlineService navOnlineService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/misc-invoices")
    public ResponseEntity<MiscInvoiceDTO> create(@Valid @RequestBody MiscInvoiceVM miscInvoiceVM) throws URISyntaxException {
        log.debug("REST request to save misc invoice : {}", miscInvoiceVM);
        InvoiceCongress result = miscInvoiceService.save(miscInvoiceVM);
        if (!InvoiceType.PRO_FORMA.equals(result.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(result.getInvoice().getId());
        }
        return ResponseEntity.created(new URI("/api/misc-invoices/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("miscInvoice", result.getId().toString()))
            .body(new MiscInvoiceDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/misc-invoices/save-and-send-email")
    public ResponseEntity<MiscInvoiceDTO> createAndSendEmail(@Valid @RequestBody MiscInvoiceVM miscInvoiceVM) throws URISyntaxException {
        log.debug("REST request to save misc invoice and send it via mail: {}", miscInvoiceVM);
        Congress congress = congressService.getById(miscInvoiceVM.getCongressId());
        InvoiceCongress result = miscInvoiceService.save(miscInvoiceVM);
        if (!InvoiceType.PRO_FORMA.equals(result.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(result.getInvoice().getId());
        }
        byte[] pdfBytes = pdfService.generatePdf(pdfService.createInvoicePdfContext(result));
        mailService.sendMiscInvoicePdfEmail(congress.getContactEmail(), miscInvoiceVM.getCustomInvoiceEmail(), result.getInvoice(),
            Locale.forLanguageTag(miscInvoiceVM.getLanguage()), pdfBytes);

        return ResponseEntity.created(new URI("/api/invoices/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("invoice", result.getId().toString()))
                .body(new MiscInvoiceDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/misc-invoices/set-payment-date")
    public ResponseEntity<InvoiceDTO> setPaymentDate(@Valid @RequestBody SetPaymentDateVM setPaymentDateVM) {
        log.debug("REST request to set payment date of misc invoice : {}", setPaymentDateVM.getId());
        InvoiceCongress invoice = miscInvoiceService.setPaymentDate(setPaymentDateVM);

        return Optional.ofNullable(invoice)
            .map(result -> new ResponseEntity<>(new InvoiceDTO(result.getInvoice()), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-invoices/congress/{id}")
    public List<MiscInvoiceDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all misc invoices by congress id: {}", id);
        return miscInvoiceService.findByCongressId(id).stream().map(MiscInvoiceDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-invoices/{id}")
    public ResponseEntity<MiscInvoiceDTO> getMiscInvoice(@PathVariable Long id) {
        log.debug("REST request to get misc invoice by id: {}", id);
        return miscInvoiceService.findById(id)
            .map(result -> new ResponseEntity<>(new MiscInvoiceDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping(value = "/misc-invoices/{invoiceCongressId}/pdf", produces = "application/pdf")
    public ResponseEntity<byte[]> getInvoicePdf(@PathVariable Long invoiceCongressId) {
        InvoiceCongress invoice = miscInvoiceService.getById(invoiceCongressId);
        byte[] pdfBytes = pdfService.generatePdf(pdfService.createInvoicePdfContext(invoice));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "inline;filename=misc-invoice-"
            + invoice.getInvoice().getInvoiceNumber().replace("/", "-") + ".pdf");

        return ResponseEntity.ok().headers(headers).contentLength(pdfBytes.length).body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-invoices/{id}/storno")
    public ResponseEntity<MiscInvoiceDTO> stornoInvoice(@PathVariable Long id) {
        log.debug("REST request to storno misc invoice : {}", id);
        InvoiceCongress invoice = miscInvoiceService.stornoInvoice(id);
        if (invoice != null && !InvoiceType.PRO_FORMA.equals(invoice.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(invoice.getInvoice().getId());
        }

        return Optional.ofNullable(invoice)
                .map(result -> new ResponseEntity<>(new MiscInvoiceDTO(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
