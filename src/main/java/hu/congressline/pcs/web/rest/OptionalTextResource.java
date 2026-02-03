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

import hu.congressline.pcs.domain.OptionalText;
import hu.congressline.pcs.service.OptionalTextService;
import hu.congressline.pcs.service.dto.OptionalTextDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.OptionalTextVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionalTextResource {
    private static final String ENTITY_NAME = "optionalText";

    private final OptionalTextService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/optional-texts")
    public ResponseEntity<OptionalTextDTO> create(@Valid @RequestBody OptionalTextVM viewModel) throws URISyntaxException {
        log.debug("REST request to save optional text : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new optional text cannot already have an ID"))
                .body(null);
        }
        OptionalText result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/optional-texts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new OptionalTextDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/optional-texts")
    public ResponseEntity<OptionalTextDTO> update(@Valid @RequestBody OptionalTextVM viewModel) throws URISyntaxException {
        log.debug("REST request to update optional text : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        OptionalText result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new OptionalTextDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-texts/congress/{id}")
    public List<OptionalTextDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all optional texts by congress id: {}", id);
        return service.findAllByCongressId(id).stream().map(OptionalTextDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-texts/{id}")
    public ResponseEntity<OptionalTextDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get optional text by id : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new OptionalTextDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/optional-texts/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete optional text : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
