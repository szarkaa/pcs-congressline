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
import hu.congressline.pcs.service.OptionalServiceService;
import hu.congressline.pcs.service.dto.OptionalServiceDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.OptionalServiceVM;
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

    private final OptionalServiceService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/optional-services")
    public ResponseEntity<OptionalServiceDTO> create(@Valid @RequestBody OptionalServiceVM viewModel) throws URISyntaxException {
        log.debug("REST request to save optional service : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new optional service cannot already have an ID"))
                .body(null);
        } else if (service.findByCodeAndCongressId(viewModel.getCode(), viewModel.getCongressId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, OPTIONAL_SERVICE_CODE_EXISTS, OPTIONAL_SERVICE_CODE_EXISTS_MSG))
                    .body(null);
        }

        OptionalService result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/optional-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new OptionalServiceDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/optional-services")
    public ResponseEntity<OptionalServiceDTO> update(@Valid @RequestBody OptionalServiceVM viewModel) throws URISyntaxException {
        log.debug("REST request to update optional service : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }
        final Optional<OptionalService> existingRegType = service.findByCodeAndCongressId(viewModel.getCode(), viewModel.getCongressId());
        if (existingRegType.isPresent() && !existingRegType.get().getId().equals(viewModel.getId())) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, OPTIONAL_SERVICE_CODE_EXISTS, OPTIONAL_SERVICE_CODE_EXISTS_MSG))
                .body(null);
        }

        OptionalService result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new OptionalServiceDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-services/congress/{id}")
    public List<OptionalServiceDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all optional services by congress id: {}", id);
        return service.findByCongressId(id).stream().map(OptionalServiceDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-services/{id}")
    public ResponseEntity<OptionalServiceDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get optional service : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new OptionalServiceDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/optional-services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete optional service : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
