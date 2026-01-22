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

import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.service.RegistrationTypeService;
import hu.congressline.pcs.service.dto.RegistrationTypeDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.RegistrationTypeVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RegistrationTypeResource {
    private static final String ENTITY_NAME = "registrationType";
    private static final String REGTYPE_CODE_EXISTS = "regtypecodeexists";
    private static final String REGTYPE_CODE_EXISTS_MSG = "Registration type code already exists";

    private final RegistrationTypeService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/registration-types")
    public ResponseEntity<RegistrationTypeDTO> create(@Valid @RequestBody RegistrationTypeVM viewModel) throws URISyntaxException {
        log.debug("REST request to save registration type : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new registration type cannot already have an ID"))
                .body(null);
        } else if (service.findOneByCode(viewModel.getCode(), viewModel.getCongressId()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, REGTYPE_CODE_EXISTS, REGTYPE_CODE_EXISTS_MSG))
                    .body(null);
        }

        RegistrationType result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/registration-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new RegistrationTypeDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/registration-types")
    public ResponseEntity<RegistrationTypeDTO> update(@Valid @RequestBody RegistrationTypeVM viewModel) throws URISyntaxException {
        log.debug("REST request to update registration type : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        final Optional<RegistrationType> existingRegType = service.findOneByCode(viewModel.getCode(), viewModel.getCongressId());
        if (existingRegType.isPresent() && !existingRegType.get().getId().equals(viewModel.getId())) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, REGTYPE_CODE_EXISTS, REGTYPE_CODE_EXISTS_MSG))
                .body(null);
        }

        RegistrationType result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new RegistrationTypeDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registration-types/congress/{id}")
    public List<RegistrationTypeDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all registration types by congress id: {}", id);
        return service.findByCongressId(id).stream().map(RegistrationTypeDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registration-types/{id}")
    public ResponseEntity<RegistrationTypeDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get registration type : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new RegistrationTypeDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/registration-types/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete registration type : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
