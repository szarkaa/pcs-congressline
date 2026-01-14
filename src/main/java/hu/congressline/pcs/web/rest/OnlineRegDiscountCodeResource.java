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

import hu.congressline.pcs.domain.OnlineRegDiscountCode;
import hu.congressline.pcs.repository.OnlineRegDiscountCodeRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OnlineRegDiscountCodeResource {
    private static final String ENTITY_NAME = "onlineRegDiscountCode";

    private final OnlineRegDiscountCodeRepository onlineRegDiscountCodeRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/online-reg-discount-codes")
    public ResponseEntity<OnlineRegDiscountCode> create(@Valid @RequestBody OnlineRegDiscountCode onlineRegDiscountCode) throws URISyntaxException {
        log.debug("REST request to save OnlineRegDiscountCode : {}", onlineRegDiscountCode);
        if (onlineRegDiscountCode.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new onlineRegDiscountCode cannot already have an ID"))
                .body(null);
        }

        OnlineRegDiscountCode result = onlineRegDiscountCodeRepository.save(onlineRegDiscountCode);
        return ResponseEntity.created(new URI("/api/online-reg-discount-codes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/online-reg-discount-codes")
    public ResponseEntity<OnlineRegDiscountCode> update(@Valid @RequestBody OnlineRegDiscountCode onlineRegDiscountCode) throws URISyntaxException {
        log.debug("REST request to update OnlineRegDiscountCode : {}", onlineRegDiscountCode);
        if (onlineRegDiscountCode.getId() == null) {
            return create(onlineRegDiscountCode);
        }

        OnlineRegDiscountCode result = onlineRegDiscountCodeRepository.save(onlineRegDiscountCode);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, onlineRegDiscountCode.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-discount-codes/{id}/congress")
    public List<OnlineRegDiscountCode> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all OnlineRegDiscountCodes by congress id: {}", id);
        return onlineRegDiscountCodeRepository.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-discount-codes/{id}")
    public ResponseEntity<OnlineRegDiscountCode> getById(@PathVariable Long id) {
        log.debug("REST request to get OnlineRegDiscountCode : {}", id);
        return onlineRegDiscountCodeRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/online-reg-discount-codes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete OnlineRegDiscountCode : {}", id);
        try {
            onlineRegDiscountCodeRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }
}
