package hu.congressline.pcs.web.rest;

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

import hu.congressline.pcs.domain.GroupDiscountPayment;
import hu.congressline.pcs.service.GroupDiscountPaymentService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class GroupDiscountPaymentResource {
    private static final String ENTITY_NAME = "groupDiscountPayment";

    private final GroupDiscountPaymentService groupDiscountPaymentService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/group-discount-payments")
    public ResponseEntity<GroupDiscountPayment> create(@Valid @RequestBody GroupDiscountPayment groupDiscountPayment) throws URISyntaxException {
        log.debug("REST request to save GroupDiscountPayment : {}", groupDiscountPayment);
        if (groupDiscountPayment.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new groupDiscountPayment cannot already have an ID"))
                .body(null);
        }
        GroupDiscountPayment result = groupDiscountPaymentService.save(groupDiscountPayment);
        return ResponseEntity.created(new URI("/api/group-discount-payments/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/group-discount-payments")
    public ResponseEntity<GroupDiscountPayment> update(@Valid @RequestBody GroupDiscountPayment groupDiscountPayment) throws URISyntaxException {
        log.debug("REST request to update GroupDiscountPayment : {}", groupDiscountPayment);
        if (groupDiscountPayment.getId() == null) {
            return create(groupDiscountPayment);
        }
        GroupDiscountPayment result = groupDiscountPaymentService.save(groupDiscountPayment);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, groupDiscountPayment.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-payments/congress/{id}")
    public List<GroupDiscountPayment> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all GroupDiscountPayments by congress id: {}", id);
        return groupDiscountPaymentService.findByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-payments/paying-group/{id}")
    public List<GroupDiscountPayment> getAllByPayingGroupId(@PathVariable Long id) {
        log.debug("REST request to get all GroupDiscountPayments by paying group id: {}", id);
        return groupDiscountPaymentService.findByPayingGroupId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/group-discount-payments/{id}")
    public ResponseEntity<GroupDiscountPayment> getById(@PathVariable Long id) {
        log.debug("REST request to get GroupDiscountPayment : {}", id);
        return groupDiscountPaymentService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/group-discount-payments/{id}")
    public ResponseEntity<Void> deleteGroupDiscountPayment(@PathVariable Long id) {
        log.debug("REST request to delete GroupDiscountPayment : {}", id);
        groupDiscountPaymentService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
