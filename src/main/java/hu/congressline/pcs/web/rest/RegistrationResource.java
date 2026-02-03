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

import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.service.RegistrationService;
import hu.congressline.pcs.service.dto.RegistrationBriefDTO;
import hu.congressline.pcs.service.dto.RegistrationDTO;
import hu.congressline.pcs.service.dto.RegistrationSummaryDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.PcsBatchUploadVm;
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
    public ResponseEntity<RegistrationDTO> create(@Valid @RequestBody RegistrationVM viewModel) throws URISyntaxException {
        log.debug("REST request to save Registration : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new registration cannot already have an ID"))
                .body(null);
        }
        Registration result = registrationService.save(viewModel);
        return ResponseEntity.created(new URI("/api/registrations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getRegId().toString()))
            .body(new RegistrationDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/registrations")
    public ResponseEntity<RegistrationDTO> update(@Valid @RequestBody RegistrationVM viewModel) throws URISyntaxException {
        log.debug("REST request to update registration : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        Registration result = registrationService.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, result.getRegId().toString()))
            .body(new RegistrationDTO(result));
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
    public List<RegistrationDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all registrations by congress id: {}", id);
        return registrationService.findAllByCongressId(id).stream().map(RegistrationDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/vm/congress/{id}")
    public List<RegistrationBriefDTO> getAllRegistrationBriefByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all registration briefs by congress id: {}", id);
        return registrationService.findAllByCongressId(id).stream().map(RegistrationBriefDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/default/congress/{id}")
    public ResponseEntity<RegistrationDTO> getDefaultRegistrationByCongressId(@PathVariable Long id) {
        log.debug("REST request to get default registration by congress id: {}", id);
        return registrationService.findTheFirstOneByCongressId(id)
            .map(result -> new ResponseEntity<>(new RegistrationDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/summary/congress/{id}")
    public ResponseEntity<RegistrationSummaryDTO> getSummaryByCongressId(@PathVariable Long id) {
        log.debug("REST request to get registration summary by congress id: {}", id);
        return new ResponseEntity<>(registrationService.getRegistrationSummaryByCongressId(id), HttpStatus.OK);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}")
    public ResponseEntity<RegistrationDTO> geById(@PathVariable Long id) {
        log.debug("REST request to get registration : {}", id);
        return registrationService.findById(id)
            .map(result -> new ResponseEntity<>(new RegistrationDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/registrations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete registration : {}", id);
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
