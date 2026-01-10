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
import hu.congressline.pcs.repository.PayingGroupItemRepository;
import hu.congressline.pcs.repository.PayingGroupRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PayingGroupResource {
    private static final String ENTITY_NAME = "payingGroup";

    private final PayingGroupRepository payingGroupRepository;
    private final PayingGroupItemRepository payingGroupItemRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/paying-groups")
    public ResponseEntity<PayingGroup> create(@Valid @RequestBody PayingGroup payingGroup) throws URISyntaxException {
        log.debug("REST request to save PayingGroup : {}", payingGroup);
        if (payingGroup.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new payingGroup cannot already have an ID"))
                .body(null);
        }

        PayingGroup result = payingGroupRepository.save(payingGroup);
        return ResponseEntity.created(new URI("/api/paying-groups/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/paying-groups")
    public ResponseEntity<PayingGroup> update(@Valid @RequestBody PayingGroup payingGroup) throws URISyntaxException {
        log.debug("REST request to update PayingGroup : {}", payingGroup);
        if (payingGroup.getId() == null) {
            return create(payingGroup);
        }
        PayingGroup result = payingGroupRepository.save(payingGroup);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, payingGroup.getId().toString()))
            .body(result);
    }

    /*
    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/paying-groups")
    public List<PayingGroup> getAllPayingGroups() {
        log.debug("REST request to get all PayingGroups");
        return payingGroupRepository.findAll();
    }
    */

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/paying-groups/congress/{id}")
    public List<PayingGroup> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all PayingGroups by congress id: {}", id);
        return payingGroupRepository.findByCongressIdOrderByName(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/paying-groups/{id}")
    public ResponseEntity<PayingGroup> getById(@PathVariable Long id) {
        log.debug("REST request to get PayingGroup : {}", id);
        return payingGroupRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/paying-groups/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete PayingGroup : {}", id);
        try {
            payingGroupItemRepository.deleteAllByPayingGroupId(id);
            payingGroupRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
