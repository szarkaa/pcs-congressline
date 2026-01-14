package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;

import hu.congressline.pcs.domain.OnlineRegistration;
import hu.congressline.pcs.domain.OnlineRegistrationCustomAnswer;
import hu.congressline.pcs.domain.OnlineRegistrationOptionalService;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.repository.OnlineRegistrationCustomAnswerRepository;
import hu.congressline.pcs.repository.OnlineRegistrationOptionalServiceRepository;
import hu.congressline.pcs.service.OnlineRegPdfService;
import hu.congressline.pcs.service.OnlineRegService;
import hu.congressline.pcs.service.OrderedOptionalServiceService;
import hu.congressline.pcs.service.RoomReservationService;
import hu.congressline.pcs.service.util.ServiceUtil;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.OnlineRegFilterVM;
import hu.congressline.pcs.web.rest.vm.OnlineRegistrationVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class BackendOnlineRegResource {
    private static final String ENTITY_NAME = "backendOnlineReg";

    private final OnlineRegService onlineRegService;
    private final OnlineRegPdfService onlineRegPdfService;
    private final RoomReservationService rrService;
    private final OnlineRegistrationOptionalServiceRepository orosRepository;
    private final OnlineRegistrationCustomAnswerRepository orcaRepository;
    private final OrderedOptionalServiceService oosService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/backend-online-regs")
    public ResponseEntity<Registration> acceptOnlineReg(@Valid @RequestBody OnlineRegistrationVM onlineReg) {
        log.debug("REST request to accept OnlineRegistration : {}", onlineReg);
        Registration result = onlineRegService.accept(onlineReg);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getRegId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/backend-online-regs/confirmation/all")
    public ResponseEntity<Void> acceptAllOnlineReg(@Valid @RequestBody OnlineRegFilterVM onlineRegFilter) {
        log.debug("REST request to accept all selected OnlineRegistration");
        onlineRegService.acceptAll(onlineRegFilter);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, String.valueOf(onlineRegFilter.getOnlineRegIdList().size())))
            .build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/backend-online-regs/delete/all")
    public ResponseEntity<Void> deleteAllOnlineReg(@Valid @RequestBody OnlineRegFilterVM onlineRegFilter) {
        log.debug("REST request to delete all selected OnlineRegistration");
        onlineRegService.deleteAll(onlineRegFilter);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createAlert("pcsApp.backendOnlineReg.deletedAll", String.valueOf(onlineRegFilter.getOnlineRegIdList().size())))
            .build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping(value = "/backend-online-regs/pdf/all", produces = "application/zip")
    public ResponseEntity<byte[]> getAllOnlineRegPdf(@RequestBody OnlineRegFilterVM onlineRegFilter) {
        List<OnlineRegistration> onlineRegs = onlineRegService.findAllByIds(onlineRegFilter.getOnlineRegIdList());
        String congressCode = !onlineRegs.isEmpty() ? onlineRegs.getFirst().getCongress().getMeetingCode().toLowerCase() : "";
        byte[] pdfBytes = onlineRegPdfService.getAllPdf(onlineRegs);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        String fileName = ServiceUtil.normalizeForFilename(congressCode + "all-online-reg") + ".pdf";
        headers.add("Content-Disposition", "inline; filename=" + fileName);

        return ResponseEntity
            .ok()
            .headers(headers)
            .contentLength(pdfBytes.length)
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(pdfBytes);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/backend-online-regs/congress/{id}")
    public List<OnlineRegistration> getAllOnlineRegsById(@PathVariable Long id) {
        log.debug("REST request to get all OnlineRegistrations by congress id: {}", id);
        return onlineRegService.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/backend-online-regs/{id}/custom-answers")
    public List<OnlineRegistrationCustomAnswer> getAllOnlineRegCustomAnswersByOnlineRegId(@PathVariable Long id) {
        log.debug("REST request to get all custom answers by registration id: {}", id);
        return orcaRepository.findAllByOnlineRegistrationIdOrderByQuestionQuestionOrderAsc(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/backend-online-regs/{id}")
    public ResponseEntity<OnlineRegistrationVM> getOnlineReg(@PathVariable Long id) {
        log.debug("REST request to get OnlineReg : {}", id);
        return onlineRegService.findById(id)
            .map(result -> new ResponseEntity<>(onlineRegService.createVM(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/backend-online-regs/{id}")
    public ResponseEntity<Void> deleteOnlineReg(@PathVariable Long id) {
        log.debug("REST request to delete OnlineReg : {}", id);
        try {
            OnlineRegistration onlineRegistration = onlineRegService.getById(id);
            if (onlineRegistration.getRoom() != null) {
                final Stream<LocalDate> range = Stream.iterate(onlineRegistration.getArrivalDate(), d -> d.plusDays(1))
                        .limit(ChronoUnit.DAYS.between(onlineRegistration.getArrivalDate(), onlineRegistration.getDepartureDate()));
                range.forEach(localDate -> rrService.decreaseRoomReservedNumber(onlineRegistration.getRoom(), localDate));
            }

            final List<OnlineRegistrationOptionalService> orosList = orosRepository.findAllByRegistration(onlineRegistration);
            orosList.forEach(oros -> {
                oosService.decreaseOptionalServiceReservedNumber(oros.getOptionalService(), oros.getParticipant());
            });
            onlineRegService.delete(onlineRegistration.getId());
            orcaRepository.deleteAllByOnlineRegistrationId(onlineRegistration.getId());
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert("onlineReg", e)).body(null);
        }
    }
}
