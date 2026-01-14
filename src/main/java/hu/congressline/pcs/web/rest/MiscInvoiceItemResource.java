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

import hu.congressline.pcs.domain.MiscInvoiceItem;
import hu.congressline.pcs.repository.MiscInvoiceItemRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MiscInvoiceItemResource {
    private static final String ENTITY_NAME = "miscInvoiceItem";

    private final MiscInvoiceItemRepository miscInvoiceItemRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/misc-invoice-items")
    public ResponseEntity<MiscInvoiceItem> create(@Valid @RequestBody MiscInvoiceItem miscInvoiceItem) throws URISyntaxException {
        log.debug("REST request to save MiscInvoiceItem : {}", miscInvoiceItem);
        if (miscInvoiceItem.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new miscInvoiceItem cannot already have an ID"))
                .body(null);
        }
        MiscInvoiceItem result = miscInvoiceItemRepository.save(miscInvoiceItem);
        return ResponseEntity.created(new URI("/api/misc-invoice-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/misc-invoice-items")
    public ResponseEntity<MiscInvoiceItem> update(@Valid @RequestBody MiscInvoiceItem miscInvoiceItem) throws URISyntaxException {
        log.debug("REST request to update MiscInvoiceItem : {}", miscInvoiceItem);
        if (miscInvoiceItem.getId() == null) {
            return create(miscInvoiceItem);
        }
        MiscInvoiceItem result = miscInvoiceItemRepository.save(miscInvoiceItem);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, miscInvoiceItem.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/misc-invoice-items/{id}")
    public ResponseEntity<MiscInvoiceItem> getById(@PathVariable Long id) {
        log.debug("REST request to get MiscInvoiceItem : {}", id);
        return miscInvoiceItemRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/misc-invoice-items/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete MiscInvoiceItem : {}", id);
        miscInvoiceItemRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
