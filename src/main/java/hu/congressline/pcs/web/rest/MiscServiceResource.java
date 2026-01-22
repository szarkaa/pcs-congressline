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

import hu.congressline.pcs.domain.MiscService;
import hu.congressline.pcs.service.MiscServiceService;
import hu.congressline.pcs.service.dto.MiscServiceDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.MiscServiceVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MiscServiceResource {
    private static final String ENTITY_NAME = "miscService";

    private final MiscServiceService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/misc-services")
    public ResponseEntity<MiscServiceDTO> create(@Valid @RequestBody MiscServiceVM viewModel) throws URISyntaxException {
        log.debug("REST request to save MiscService : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new misc service cannot already have an ID")).body(null);
        }
        MiscService result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/misc-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new MiscServiceDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/misc-services")
    public ResponseEntity<MiscServiceDTO> update(@Valid @RequestBody MiscServiceVM viewModel) throws URISyntaxException {
        log.debug("REST request to update misc service : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }
        MiscService result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new MiscServiceDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-services/congress/{id}")
    public List<MiscServiceDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all misc services by congress id: {}", id);
        return service.findByCongressId(id).stream().map(MiscServiceDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-services/{id}")
    public ResponseEntity<MiscServiceDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get misc service : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new MiscServiceDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/misc-services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete misc service : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
