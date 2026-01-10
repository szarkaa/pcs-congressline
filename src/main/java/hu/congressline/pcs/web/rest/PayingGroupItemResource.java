package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import hu.congressline.pcs.domain.PayingGroupItem;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.repository.PayingGroupItemRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PayingGroupItemResource {
    private static final String ENTITY_NAME = "payingGroupItem";

    private final PayingGroupItemRepository payingGroupItemRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/paying-group-items")
    public ResponseEntity<PayingGroupItem> create(@Valid @RequestBody PayingGroupItem payingGroupItem) throws URISyntaxException {
        log.debug("REST request to save PayingGroupItem : {}", payingGroupItem);
        if (payingGroupItem.getId() != null) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new payingGroupItem cannot already have an ID"))
                .body(null);
        }
        PayingGroupItem result = payingGroupItemRepository.save(payingGroupItem);
        return ResponseEntity.created(new URI("/api/paying-group-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/paying-group-items")
    public ResponseEntity<PayingGroupItem> update(@Valid @RequestBody PayingGroupItem payingGroupItem) throws URISyntaxException {
        log.debug("REST request to update PayingGroupItem : {}", payingGroupItem);
        if (payingGroupItem.getId() == null) {
            return create(payingGroupItem);
        }

        PayingGroupItem result = payingGroupItemRepository.save(payingGroupItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, payingGroupItem.getId().toString()))
            .body(result);
    }

    /*
    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/paying-group-items")
    public List<PayingGroupItem> getAllPayingGroupItems() {
        log.debug("REST request to get all PayingGroupItems");
        List<PayingGroupItem> payingGroupItems = payingGroupItemRepository.findAll();
        return payingGroupItems;
    }
    */

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/paying-group/{id}/paying-group-items")
    public List<PayingGroupItem> getAllByPayingGroupId(@PathVariable Long id) {
        log.debug("REST request to get all PayingGroupItems by payingGroup id: {}", id);
        return payingGroupItemRepository.findAllByPayingGroupId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/congress/{id}/paying-group/paying-group-items/{itemType}")
    public List<PayingGroupItem> getAllByCongressId(@PathVariable Long id, @PathVariable ChargeableItemType itemType) {
        log.debug("REST request to get all PayingGroupItems by congress id: {} and itemType: {}", id, itemType);
        return payingGroupItemRepository.findAllByChargeableItemTypeAndPayingGroupCongressId(itemType, id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/paying-group-items/{id}")
    public ResponseEntity<PayingGroupItem> getById(@PathVariable Long id) {
        log.debug("REST request to get PayingGroupItem : {}", id);
        return payingGroupItemRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/paying-group-items/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete PayingGroupItem : {}", id);
        try {
            payingGroupItemRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
