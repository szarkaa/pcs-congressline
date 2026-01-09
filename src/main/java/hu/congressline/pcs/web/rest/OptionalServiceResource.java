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

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.repository.OptionalServiceRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionalServiceResource {
    private static final String ENTITY_NAME = "optionalService";
    private static final String OPTIONAL_SERVICE_CODE_EXISTS = "optionalservicecodeexists";
    private static final String OPTIONAL_SERVICE_CODE_EXISTS_MSG = "Optional service code already exists";

    private final OptionalServiceRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/optional-services")
    public ResponseEntity<OptionalService> createOptionalService(@Valid @RequestBody OptionalService optionalService) throws URISyntaxException {
        log.debug("REST request to save OptionalService : {}", optionalService);
        if (optionalService.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new optionalService cannot already have an ID"))
                .body(null);
        } else if (repository.findOneByCodeAndCongressId(optionalService.getCode(), optionalService.getCongress().getId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, OPTIONAL_SERVICE_CODE_EXISTS, OPTIONAL_SERVICE_CODE_EXISTS_MSG))
                    .body(null);
        }

        OptionalService result = repository.save(optionalService);
        return ResponseEntity.created(new URI("/api/optional-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/optional-services")
    public ResponseEntity<OptionalService> updateOptionalService(@Valid @RequestBody OptionalService optionalService) throws URISyntaxException {
        log.debug("REST request to update OptionalService : {}", optionalService);
        if (optionalService.getId() == null) {
            return createOptionalService(optionalService);
        }
        final Optional<OptionalService> existingRegType = repository.findOneByCodeAndCongressId(optionalService.getCode(), optionalService.getCongress().getId());
        if (existingRegType.isPresent() && !existingRegType.get().getId().equals(optionalService.getId())) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, OPTIONAL_SERVICE_CODE_EXISTS, OPTIONAL_SERVICE_CODE_EXISTS_MSG))
                .body(null);
        }

        OptionalService result = repository.save(optionalService);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, optionalService.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-services")
    public List<OptionalService> getAllOptionalServices() {
        log.debug("REST request to get all OptionalServices");
        List<OptionalService> optionalServices = repository.findAll();
        return optionalServices;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-services/congress/{id}")
    public List<OptionalService> getAllOptionalServicesByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all OptionalServices by congress id: {}", id);
        return repository.findByCongressIdOrderByName(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-services/{id}")
    public ResponseEntity<OptionalService> getOptionalService(@PathVariable Long id) {
        log.debug("REST request to get OptionalService : {}", id);
        return repository.findById(id)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/optional-services/{id}")
    public ResponseEntity<Void> deleteOptionalService(@PathVariable Long id) {
        log.debug("REST request to delete OptionalService : {}", id);
        try {
            repository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
