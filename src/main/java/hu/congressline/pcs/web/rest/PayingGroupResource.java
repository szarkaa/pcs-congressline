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

import hu.congressline.pcs.domain.PayingGroup;
import hu.congressline.pcs.service.PayingGroupService;
import hu.congressline.pcs.service.dto.PayingGroupDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.PayingGroupVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PayingGroupResource {
    private static final String ENTITY_NAME = "payingGroup";

    private final PayingGroupService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/paying-groups")
    public ResponseEntity<PayingGroupDTO> create(@Valid @RequestBody PayingGroupVM viewModel) throws URISyntaxException {
        log.debug("REST request to save paying group : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new paying group cannot already have an ID"))
                .body(null);
        }

        PayingGroup result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/paying-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new PayingGroupDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/paying-groups")
    public ResponseEntity<PayingGroupDTO> update(@Valid @RequestBody PayingGroupVM viewModel) throws URISyntaxException {
        log.debug("REST request to update paying group : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }
        PayingGroup result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new PayingGroupDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/paying-groups/congress/{id}")
    public List<PayingGroupDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all paying groups by congress id: {}", id);
        return service.findAllForCongressId(id).stream().map(PayingGroupDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/paying-groups/{id}")
    public ResponseEntity<PayingGroupDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get paying group : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new PayingGroupDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/paying-groups/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete paying group : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
