package hu.congressline.pcs.web.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.service.ConfirmationPdfService;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.FinancialReportService;
import hu.congressline.pcs.service.GeneralRegistrationReportService;
import hu.congressline.pcs.service.MailService;
import hu.congressline.pcs.service.dto.FinancialReportDTO;
import hu.congressline.pcs.service.dto.GeneralRegistrationReportDTO;
import hu.congressline.pcs.service.dto.SendAllConfirmationPdfToEmailDTO;
import hu.congressline.pcs.service.pdf.ConfirmationPdfContext;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.ConfirmationPdfVM;
import hu.congressline.pcs.web.rest.vm.ConfirmationTitleType;
import hu.congressline.pcs.web.rest.vm.FinancialReportVM;
import hu.congressline.pcs.web.rest.vm.SendAllConfirmationVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/registrations/confirmation")
public class ConfirmationResource {
    private static final String MEDIA_TYPE = "application/octet-stream";

    private final ConfirmationPdfService service;
    private final GeneralRegistrationReportService reportService;
    private final FinancialReportService financialReportService;
    private final MailService mailService;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping(value = "/pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/pdf")
    public ResponseEntity<byte[]> getConfirmationPdf(@RequestBody ConfirmationPdfVM vm) {
        final ConfirmationPdfContext context = service.createContext(vm);
        byte[] pdfBytes = service.generatePdf(context);

        String fileName = createConfirmationFilename(context);
        return ResponseEntity
            .ok()
            .headers(createHttpHeader(fileName))
            .contentLength(pdfBytes.length)
            .contentType(MediaType.parseMediaType(MEDIA_TYPE))
            .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping(value = "/send-pdf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "application/pdf")
    public ResponseEntity<byte[]> sendConfirmationPdf(@RequestBody ConfirmationPdfVM vm) {
        ConfirmationPdfContext context = service.createContext(vm);
        byte[] pdfBytes = service.generatePdf(context);
        String fileName = createConfirmationFilename(context);
        Congress congress = congressService.getById(vm.getCongressId());
        mailService.sendConfirmationPdfEmail(congress.getContactEmail(), vm.getCustomConfirmationEmail(), congress.getContactEmail(), fileName,
                context.getConfirmationTitleType(), Locale.forLanguageTag(vm.getLanguage()), context.getRegistration(), pdfBytes);
        return ResponseEntity
            .ok()
            .headers(createHttpHeader(fileName))
            .contentLength(pdfBytes.length)
            .contentType(MediaType.parseMediaType(MEDIA_TYPE))
            .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/send-to-all")
    public ResponseEntity<Void> sendConfirmationToAll(@RequestBody SendAllConfirmationVM vm) {
        log.debug("REST request to send confirmation to all");
        final Congress congress = congressService.getById(Long.valueOf(vm.getCongressId()));
        final List<GeneralRegistrationReportDTO> reportDTOList = reportService.findAll(vm);
        reportDTOList.forEach(dto -> {
            ConfirmationPdfVM pdfVM = new ConfirmationPdfVM();
            pdfVM.setLanguage(vm.getLanguage());
            pdfVM.setConfirmationTitleType(ConfirmationTitleType.CONFIRMATION);
            pdfVM.setRegistrationId(dto.getId());
            pdfVM.setOptionalText(vm.getOptionalText());
            pdfVM.setCustomConfirmationEmail(dto.getEmail());
            pdfVM.setIgnoredChargeableItemIdList(new ArrayList<>());
            pdfVM.setIgnoredChargedServiceIdList(new ArrayList<>());
            ConfirmationPdfContext context = service.createContext(pdfVM);
            byte[] pdfBytes = service.generatePdf(context);
            mailService.sendConfirmationPdfEmail(congress.getContactEmail(), pdfVM.getCustomConfirmationEmail(), congress.getContactEmail(),
                createConfirmationFilename(context), context.getConfirmationTitleType(), Locale.forLanguageTag(vm.getLanguage()), context.getRegistration(), pdfBytes);
        });

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert("sendConfirmationToAllDialog", String.valueOf(reportDTOList.size()))).build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/notice-to-all")
    public ResponseEntity<Void> sendFinancialNoticeToAll(@RequestBody SendAllConfirmationVM vm) {
        log.debug("REST request to send financial notice to all");
        final Congress congress = congressService.getById(Long.valueOf(vm.getCongressId()));

        FinancialReportVM filter = new FinancialReportVM();
        filter.setParticipantsToPay(true);
        filter.setCongressId(vm.getCongressId());
        final List<FinancialReportDTO> reportDTOList = financialReportService.findAll(filter);
        reportDTOList.forEach(dto -> {
            ConfirmationPdfVM pdfVM = new ConfirmationPdfVM();
            pdfVM.setConfirmationTitleType(ConfirmationTitleType.CONFIRMATION);
            pdfVM.setLanguage(vm.getLanguage());
            pdfVM.setRegistrationId(dto.getId());
            pdfVM.setOptionalText(vm.getOptionalText());
            pdfVM.setCustomConfirmationEmail(dto.getEmail());
            pdfVM.setIgnoredChargeableItemIdList(new ArrayList<>());
            pdfVM.setIgnoredChargedServiceIdList(new ArrayList<>());
            ConfirmationPdfContext context = service.createContext(pdfVM);
            byte[] pdfBytes = service.generatePdf(context);
            mailService.sendConfirmationPdfEmail(congress.getContactEmail(), pdfVM.getCustomConfirmationEmail(), null,
                createConfirmationFilename(context), context.getConfirmationTitleType(), Locale.forLanguageTag(pdfVM.getLanguage()), context.getRegistration(), pdfBytes);
        });

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert("sendFinancialNoticeToAllDialog", String.valueOf(reportDTOList.size()))).build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/send-all-to-email")
    public ResponseEntity<Void> sendAllConfirmationToEmail(@RequestBody SendAllConfirmationVM vm) {
        log.debug("REST request to send all confirmation to email: {}", vm.getSendAllEmail());
        final Congress congress = congressService.getById(Long.valueOf(vm.getCongressId()));

        final List<GeneralRegistrationReportDTO> reportDTOList = reportService.findAll(vm);
        List<SendAllConfirmationPdfToEmailDTO> pdfList = new ArrayList<>(reportDTOList.size());
        reportDTOList.forEach(dto -> {
            ConfirmationPdfVM pdfVM = new ConfirmationPdfVM();
            pdfVM.setLanguage(vm.getLanguage());
            pdfVM.setConfirmationTitleType(ConfirmationTitleType.CONFIRMATION);
            pdfVM.setRegistrationId(dto.getId());
            pdfVM.setOptionalText(vm.getOptionalText());
            pdfVM.setCustomConfirmationEmail(dto.getEmail());
            pdfVM.setIgnoredChargeableItemIdList(new ArrayList<>());
            pdfVM.setIgnoredChargedServiceIdList(new ArrayList<>());
            ConfirmationPdfContext context = service.createContext(pdfVM);
            byte[] pdfBytes = service.generatePdf(context);
            pdfList.add(new SendAllConfirmationPdfToEmailDTO(dto.getRegId(), pdfBytes));
        });
        mailService.sendAllConfirmationPdfToEmail(congress.getContactEmail(), vm.getSendAllEmail(), Locale.forLanguageTag(vm.getLanguage()), pdfList);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert("sendAllConfirmationToEmailDialog", String.valueOf(reportDTOList.size()))).build();
    }

    private HttpHeaders createHttpHeader(String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "inline; filename=" + fileName);
        return headers;
    }

    private String createConfirmationFilename(ConfirmationPdfContext context) {
        String title = "";
        final String lang = "hu";
        if (ConfirmationTitleType.PRO_FORMA_INVOICE.equals(context.getConfirmationTitleType())) {
            title = lang.equals(context.getLocale().getLanguage()) ? "díjbekérő" : "pro-forma-invoice";
        } else {
            title = lang.equals(context.getLocale().getLanguage()) ? "visszaigazolás" : "confirmation";
        }
        return ServiceUtil.normalizeForFilename(context.getRegistration().getCongress().getMeetingCode() + "-" + title + "-reg-" + context.getRegistration().getRegId());
    }

}
