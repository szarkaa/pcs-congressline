package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.service.RegistrationService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.PcsBatchUploadVm;
import hu.congressline.pcs.web.rest.vm.RegistrationSummaryVM;
import hu.congressline.pcs.web.rest.vm.RegistrationVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RegistrationResource {
    private static final String ENTITY_NAME = "registration";

    private final RegistrationService registrationService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/registrations")
    public ResponseEntity<Registration> create(@Valid @RequestBody Registration registration) throws URISyntaxException {
        log.debug("REST request to save Registration : {}", registration);
        if (registration.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new registration cannot already have an ID"))
                .body(null);
        }
        Registration result = registrationService.save(registration);
        return ResponseEntity.created(new URI("/api/registrations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getRegId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/registrations")
    public ResponseEntity<Registration> update(@Valid @RequestBody Registration registration) throws URISyntaxException {
        log.debug("REST request to update Registration : {}", registration);
        if (registration.getId() == null) {
            return create(registration);
        }

        Registration result = registrationService.save(registration);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, registration.getRegId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/registrations/upload")
    public ResponseEntity<List<String>> uploadBatchRegistrations(@Valid @RequestBody PcsBatchUploadVm pcsFile) throws URISyntaxException {
        log.debug("REST request to process upload batch registrations");
        List<String> messageList = registrationService.processUploadedRegistrations(pcsFile);
        return ResponseEntity.created(new URI("/api/registrations/upload")).body(messageList);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/congress/{id}")
    public List<Registration> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all Registrations by congress id: {}", id);
        return registrationService.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/vm/congress/{id}")
    public List<RegistrationVM> getAllRegistrationVMByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all Registration ids by congress id: {}", id);
        return registrationService.findAllRegistrationVMByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/default/congress/{id}")
    public ResponseEntity<Registration> getDefaultRegistrationByCongressId(@PathVariable Long id) {
        log.debug("REST request to get default Registration by congress id: {}", id);
        return registrationService.findTheFirstOneByCongressId(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/summary/congress/{id}")
    public ResponseEntity<RegistrationSummaryVM> getSummaryByCongressId(@PathVariable Long id) {
        log.debug("REST request to get Registration summary by congress id: {}", id);
        RegistrationSummaryVM dto = registrationService.getRegistrationSummaryByCongressId(id);
        return Optional.ofNullable(dto)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}")
    public ResponseEntity<Registration> geById(@PathVariable Long id) {
        log.debug("REST request to get Registration : {}", id);
        return registrationService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/registrations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Registration : {}", id);
        try {
            final Registration registration = registrationService.getById(id);
            registrationService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, registration.getRegId().toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
