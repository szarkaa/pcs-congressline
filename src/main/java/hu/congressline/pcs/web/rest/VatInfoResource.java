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

import hu.congressline.pcs.domain.VatInfo;
import hu.congressline.pcs.domain.enumeration.ChargeableItemType;
import hu.congressline.pcs.service.VatInfoService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class VatInfoResource {
    private static final String ENTITY_NAME = "vatInfo";

    private final VatInfoService vatInfoService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/vat-infos")
    public ResponseEntity<VatInfo> create(@Valid @RequestBody VatInfo vatInfo) throws URISyntaxException {
        log.debug("REST request to save VatInfo : {}", vatInfo);
        if (vatInfo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new vatInfo cannot already have an ID")).body(null);
        }
        VatInfo result = vatInfoService.save(vatInfo);
        return ResponseEntity.created(new URI("/api/vat-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/vat-infos")
    public ResponseEntity<VatInfo> update(@Valid @RequestBody VatInfo vatInfo) throws URISyntaxException {
        log.debug("REST request to update VatInfo : {}", vatInfo);
        if (vatInfo.getId() == null) {
            return create(vatInfo);
        }
        VatInfo result = vatInfoService.save(vatInfo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, vatInfo.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/vat-infos")
    public List<VatInfo> getAll() {
        log.debug("REST request to get all VatInfos");
        return vatInfoService.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/vat-infos/{id}")
    public ResponseEntity<VatInfo> getById(@PathVariable Long id) {
        log.debug("REST request to get VatInfo : {}", id);
        return vatInfoService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/vat-infos/congress/{id}")
    public List<VatInfo> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all VatInfos by congress id");
        return vatInfoService.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/vat-infos/all/congress/{id}")
    public List<VatInfo> getAllForCongressId(@PathVariable Long id) {
        log.debug("REST request to get all VatInfos for null congress and congress id");
        return vatInfoService.findAllForCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/vat-infos/all/congress/{id}/item-type/{itemType}")
    public List<VatInfo> getAllForCongressIdAndItemType(@PathVariable Long id, @PathVariable ChargeableItemType itemType) {
        log.debug("REST request to get all VatInfos for null congress and congress id and item type");
        return vatInfoService.findAllForCongressIdAndItemType(id, itemType);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/vat-infos/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete VatInfo : {}", id);
        try {
            vatInfoService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
