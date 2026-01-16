package hu.congressline.pcs.web.rest;

import com.google.gson.JsonObject;

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
import java.util.Optional;

import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.domain.InvoiceCongress;
import hu.congressline.pcs.domain.InvoicePayingGroup;
import hu.congressline.pcs.domain.InvoiceRegistration;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.domain.enumeration.Currency;
import hu.congressline.pcs.domain.enumeration.InvoiceType;
import hu.congressline.pcs.service.GroupDiscountInvoicePdfService;
import hu.congressline.pcs.service.GroupDiscountInvoiceService;
import hu.congressline.pcs.service.InvoicePdfService;
import hu.congressline.pcs.service.InvoiceService;
import hu.congressline.pcs.service.MiscInvoicePdfService;
import hu.congressline.pcs.service.MiscInvoiceService;
import hu.congressline.pcs.service.NavOnlineService;
import hu.congressline.pcs.service.RegistrationService;
import hu.congressline.pcs.service.dto.InvoiceDTO;
import hu.congressline.pcs.service.dto.SetPaymentDateDTO;
import hu.congressline.pcs.service.pdf.GroupDiscountInvoicePdfContext;
import hu.congressline.pcs.service.pdf.InvoicePdfContext;
import hu.congressline.pcs.service.pdf.MiscInvoicePdfContext;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.InvoiceVM;
import hu.congressline.pcs.web.rest.vm.ResendInvoiceVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class InvoiceResource {
    private static final String ENTITY_NAME = "invoice";
    private static final String URI = "/api/invoices/";
    private static final String EMAIL_RESENT = "pcsApp.invoice.message.emailResent";

    private final InvoiceService invoiceService;
    //private final MailService mailService;
    private final InvoicePdfService invoicePdfService;
    private final RegistrationService registrationService;
    private final GroupDiscountInvoiceService invoicePayingGroupService;
    private final GroupDiscountInvoicePdfService groupDiscountInvoicePdfService;
    private final MiscInvoiceService miscInvoiceService;
    private final MiscInvoicePdfService miscInvoicePdfService;
    private final NavOnlineService navOnlineService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/invoices")
    public ResponseEntity<Invoice> create(@Valid @RequestBody InvoiceVM invoiceVM) throws URISyntaxException {
        log.debug("REST request to save Invoice : {}", invoiceVM);
        InvoiceRegistration result = invoiceService.save(invoiceVM);
        if (!InvoiceType.PRO_FORMA.equals(result.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(result.getInvoice().getId());
        }
        return ResponseEntity.created(new URI(URI + result.getInvoice().getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getInvoice().getId().toString()))
            .body(result.getInvoice());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/invoices/save-and-send-email")
    public ResponseEntity<Invoice> createAndSendEmail(@Valid @RequestBody InvoiceVM invoiceVM) throws URISyntaxException {
        log.debug("REST request to save Invoice and send it via mail: {}", invoiceVM);
        InvoiceRegistration result = invoiceService.save(invoiceVM);
        if (!InvoiceType.PRO_FORMA.equals(result.getInvoice().getInvoiceType())) {
            navOnlineService.postInvoiceToNav(result.getInvoice().getId());
        }
        final InvoicePdfContext invoicePdfContext = invoicePdfService.createInvoicePdfContext(result);
        byte[] pdfBytes = invoicePdfService.generatePdf(invoicePdfContext);
        Registration registration = registrationService.getById(invoiceVM.getRegistrationId());
        //mailService.sendInvoicePdfEmail(new Locale(invoiceVM.getLanguage()), result.getRegistration().getCongress().getContactEmail(), invoiceVM.getCustomInvoiceEmail(),
        // createInvoiceFilename(invoicePdfContext), registration, pdfBytes);
        return ResponseEntity.created(new URI(URI + result.getInvoice().getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getInvoice().getId().toString()))
                .body(result.getInvoice());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/invoices/resend-email")
    public ResponseEntity<Void> resendEmail(@Valid @RequestBody ResendInvoiceVM invoiceVM) {
        log.debug("REST request to resend Invoice mail: {}", invoiceVM);
        InvoiceRegistration invoiceRegistration = invoiceService.getInvoiceRegistrationByInvoiceId(invoiceVM.getInvoiceId());
        if (invoiceRegistration != null) {
            final InvoicePdfContext invoicePdfContext = invoicePdfService.createInvoicePdfContext(invoiceRegistration);
            byte[] pdfBytes = invoicePdfService.generatePdf(invoicePdfContext);
            //mailService.sendInvoicePdfEmail(new Locale(invoiceRegistration.getInvoice().getPrintLocale()),
            //invoiceRegistration.getRegistration().getCongress().getContactEmail(),
            //invoiceVM.getEmail(), createInvoiceFilename(invoicePdfContext), invoiceRegistration.getRegistration(), pdfBytes);
            return ResponseEntity.accepted().headers(HeaderUtil.createAlert(EMAIL_RESENT, null)).build();
        }

        InvoicePayingGroup invoicePayingGroup = invoicePayingGroupService.findInvoicePayingGroupByInvoiceId(invoiceVM.getInvoiceId());
        if (invoicePayingGroup != null) {
            final GroupDiscountInvoicePdfContext invoicePdfContext = groupDiscountInvoicePdfService.createInvoicePdfContext(invoicePayingGroup);
            byte[] pdfBytes = groupDiscountInvoicePdfService.generatePdf(invoicePdfContext);
            //mailService.sendGroupDiscountInvoicePdfEmail(new Locale(invoicePayingGroup.getInvoice().getPrintLocale()),
            //invoicePayingGroup.getPayingGroup().getCongress().getContactEmail(), invoiceVM.getEmail(),
            //invoicePayingGroup.getPayingGroup(), pdfBytes);
            return ResponseEntity.accepted().headers(HeaderUtil.createAlert(EMAIL_RESENT, null)).build();
        }

        InvoiceCongress invoiceCongress = miscInvoiceService.findInvoiceCongressByInvoiceId(invoiceVM.getInvoiceId());
        if (invoiceCongress != null) {
            final MiscInvoicePdfContext invoicePdfContext = miscInvoicePdfService.createInvoicePdfContext(invoiceCongress);
            byte[] pdfBytes = miscInvoicePdfService.generatePdf(invoicePdfContext);
            //mailService.sendMiscInvoicePdfEmail(new Locale(invoiceCongress.getInvoice().getPrintLocale()),
            //invoiceCongress.getCongress().getContactEmail(), invoiceVM.getEmail(), invoiceCongress.getInvoice(), pdfBytes);
            return ResponseEntity.accepted().headers(HeaderUtil.createAlert(EMAIL_RESENT, null)).build();
        }
        return ResponseEntity.badRequest().build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/invoices/set-payment-date")
    public ResponseEntity<InvoiceDTO> setPaymentDate(@Valid @RequestBody SetPaymentDateDTO setPaymentDateDTO) {
        log.debug("REST request to set payment date of Invoice : {}", setPaymentDateDTO.getId());
        InvoiceDTO invoice = invoiceService.setPaymentDate(setPaymentDateDTO);

        return Optional.ofNullable(invoice)
                .map(result -> new ResponseEntity<>(invoice, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        log.debug("REST request to get Invoice : {}", id);
        return invoiceService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}/invoices")
    public List<InvoiceDTO> getAllByRegistrationId(@PathVariable Long id) {
        log.debug("REST request to get all Invoices by registration id: {}", id);
        return invoiceService.findAllByRegistrationId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoices/{id}/invoiced-chargeable-items")
    public List<Long> getInvoicedChargeableItemIds(@PathVariable("id") Long registrationId) {
        log.debug("REST request to get invoiced chargeable items by registration id: {}", registrationId);
        return invoiceService.getInvoicedChargeableItemIds(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoices/{id}/invoiced-charged-services")
    public List<Long> getInvoicedChargedServiceIds(@PathVariable("id") Long registrationId) {
        log.debug("REST request to get invoiced charged services by registration id: {}", registrationId);
        return invoiceService.getInvoicedChargedServiceIds(registrationId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoices/{invoiceId}/pdf")
    public ResponseEntity<byte[]> getPdf(@PathVariable Long invoiceId) {
        InvoiceRegistration invoice = invoiceService.getInvoiceRegistrationByInvoiceId(invoiceId);
        final InvoicePdfContext invoicePdfContext = invoicePdfService.createInvoicePdfContext(invoice);
        byte[] pdfBytes = invoicePdfService.generatePdf(invoicePdfContext);

        String fileName = createInvoiceFilename(invoicePdfContext) + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "inline;filename=" + fileName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(pdfBytes.length)
                //.contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/invoices/rates/{currency}")
    public ResponseEntity<String> hasValidRate(@PathVariable String currency) {
        log.debug("REST request to check valid rate by currency: {}", currency);
        final boolean hasValidRate = Currency.HUF.toString().equalsIgnoreCase(currency) || invoicePdfService.hasValidRate(currency);
        JsonObject json = new JsonObject();
        json.addProperty("hasValidRate", hasValidRate);
        return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/invoices/{id}/storno")
    public ResponseEntity<Invoice> stornoInvoice(@PathVariable Long id) {
        log.debug("REST request to storno Invoice : {}", id);
        Invoice invoice = invoiceService.stornoInvoice(id);
        if (invoice != null && !InvoiceType.PRO_FORMA.equals(invoice.getInvoiceType())) {
            navOnlineService.postInvoiceToNav(invoice.getId());
        }

        return Optional.ofNullable(invoice).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    private String createInvoiceFilename(InvoicePdfContext context) {
        String title = "hu".equals(context.getLocale().getLanguage()) ? "számla" : ENTITY_NAME;
        return ServiceUtil.normalizeForFilename(context.getRegistration().getCongress().getMeetingCode() + "-" + title + "-reg-" + context.getRegistration().getRegId());
    }
}
