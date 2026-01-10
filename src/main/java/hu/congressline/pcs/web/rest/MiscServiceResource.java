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
import hu.congressline.pcs.repository.MiscServiceRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MiscServiceResource {
    private static final String ENTITY_NAME = "miscService";

    private final MiscServiceRepository miscServiceRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/misc-services")
    public ResponseEntity<MiscService> create(@Valid @RequestBody MiscService miscService) throws URISyntaxException {
        log.debug("REST request to save MiscService : {}", miscService);
        if (miscService.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new miscService cannot already have an ID")).body(null);
        }
        MiscService result = miscServiceRepository.save(miscService);
        return ResponseEntity.created(new URI("/api/misc-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/misc-services")
    public ResponseEntity<MiscService> update(@Valid @RequestBody MiscService miscService) throws URISyntaxException {
        log.debug("REST request to update MiscService : {}", miscService);
        if (miscService.getId() == null) {
            return create(miscService);
        }
        MiscService result = miscServiceRepository.save(miscService);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, miscService.getId().toString()))
            .body(result);
    }

    /*
    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-services")
    public List<MiscService> getAllMiscServices() {
        log.debug("REST request to get all MiscServices");
        return miscServiceRepository.findAll();
    }
    */

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-services/congress/{id}")
    public List<MiscService> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all MiscServices by congress id: {}", id);
        return miscServiceRepository.findByCongressIdOrderByName(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-services/{id}")
    public ResponseEntity<MiscService> getById(@PathVariable Long id) {
        log.debug("REST request to get MiscService : {}", id);
        return miscServiceRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/misc-services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete MiscService : {}", id);
        try {
            miscServiceRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
