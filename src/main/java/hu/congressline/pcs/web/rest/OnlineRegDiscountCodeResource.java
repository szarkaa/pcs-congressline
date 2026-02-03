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
import hu.congressline.pcs.service.OnlineRegDiscountCodeService;
import hu.congressline.pcs.service.dto.OnlineRegDiscountCodeDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.OnlineRegDiscountCodeVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OnlineRegDiscountCodeResource {
    private static final String ENTITY_NAME = "onlineRegDiscountCode";

    private final OnlineRegDiscountCodeService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/online-reg-discount-codes")
    public ResponseEntity<OnlineRegDiscountCodeDTO> create(@Valid @RequestBody OnlineRegDiscountCodeVM viewModel) throws URISyntaxException {
        log.debug("REST request to save online reg discount code : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new online reg discount code cannot already have an ID"))
                .body(null);
        }

        OnlineRegDiscountCode result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/online-reg-discount-codes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new OnlineRegDiscountCodeDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/online-reg-discount-codes")
    public ResponseEntity<OnlineRegDiscountCodeDTO> update(@Valid @RequestBody OnlineRegDiscountCodeVM onlineRegDiscountCode) throws URISyntaxException {
        log.debug("REST request to update online reg discount code : {}", onlineRegDiscountCode);
        if (onlineRegDiscountCode.getId() == null) {
            return create(onlineRegDiscountCode);
        }

        OnlineRegDiscountCode result = service.save(onlineRegDiscountCode);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, onlineRegDiscountCode.getId().toString()))
            .body(new OnlineRegDiscountCodeDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-discount-codes/{id}/congress")
    public List<OnlineRegDiscountCodeDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all online reg discount codes by congress id: {}", id);
        return service.findAllByCongressId(id).stream().map(OnlineRegDiscountCodeDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online-reg-discount-codes/{id}")
    public ResponseEntity<OnlineRegDiscountCodeDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get online reg discount code by id: {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new OnlineRegDiscountCodeDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/online-reg-discount-codes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete online reg discount code : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }
}
