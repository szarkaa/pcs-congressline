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

import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.service.WorkplaceService;
import hu.congressline.pcs.service.dto.WorkplaceMergeDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class WorkplaceResource {
    private static final String ENTITY_NAME = "workplace";

    private final WorkplaceService workplaceService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/workplaces")
    public ResponseEntity<Workplace> create(@Valid @RequestBody Workplace workplace) throws URISyntaxException {
        log.debug("REST request to save Workplace : {}", workplace);
        if (workplace.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new workplace cannot already have an ID"))
                .body(null);
        }

        Workplace result = workplaceService.save(workplace);
        return ResponseEntity.created(new URI("/api/workplaces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/workplaces")
    public ResponseEntity<Workplace> update(@Valid @RequestBody Workplace workplace) throws URISyntaxException {
        log.debug("REST request to update Workplace : {}", workplace);
        if (workplace.getId() == null) {
            return create(workplace);
        }
        Workplace result = workplaceService.save(workplace);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, workplace.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/workplaces/congress/{congressId}")
    public List<Workplace> getAllOnlyByCongressId(@PathVariable Long congressId) {
        log.debug("REST request to get all Workplaces by congress id: {}", congressId);
        return workplaceService.findByCongressId(congressId);
    }

    /*
    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/workplaces")
    public List<Workplace> getAll() {
        log.debug("REST request to get all Workplaces");
        return workplaceService.findAll();
    }
    */

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/workplaces/all/congress/{id}")
    public List<Workplace> getAllWorkplacesForCongressId(@PathVariable Long id) {
        log.debug("REST request to get all workplaces for null congress and congress id");
        return workplaceService.findAllForCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/workplaces/{id}")
    public ResponseEntity<Workplace> getById(@PathVariable Long id) {
        log.debug("REST request to get Workplace : {}", id);
        return workplaceService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/workplaces/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Workplace : {}", id);
        try {
            workplaceService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constration violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/workplaces/merge")
    public ResponseEntity<Void> merge(@Valid @RequestBody WorkplaceMergeDTO workplaceMergeDTO) {
        log.debug("REST request to merge Workplace id : {}", workplaceMergeDTO.getWorkplaceId());
        workplaceService.merge(workplaceMergeDTO.getWorkplaceId(), workplaceMergeDTO.getMergingWorkplaceIdList());
        return ResponseEntity.ok().headers(HeaderUtil.createAlert("pcsApp.workplace.merged", workplaceMergeDTO.getWorkplaceId().toString())).build();
    }
}
