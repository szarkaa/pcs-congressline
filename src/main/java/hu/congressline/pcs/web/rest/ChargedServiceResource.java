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
import java.util.Map;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.Invoice;
import hu.congressline.pcs.service.ChargedServiceService;
import hu.congressline.pcs.service.InvoiceService;
import hu.congressline.pcs.service.dto.ChargedServiceDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ChargedServiceResource {
    private static final String ENTITY_NAME = "chargedService";

    private final ChargedServiceService chargedServiceService;
    private final InvoiceService invoiceService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/charged-services")
    public ResponseEntity<ChargedService> create(@Valid @RequestBody ChargedService chargedService) throws URISyntaxException {
        log.debug("REST request to save ChargedService : {}", chargedService);
        if (chargedService.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new chargedService cannot already have an ID"))
                .body(null);
        }

        ChargedService result = chargedServiceService.save(chargedService);
        return ResponseEntity.created(new URI("/api/charged-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/charged-services")
    public ResponseEntity<ChargedService> update(@Valid @RequestBody ChargedService chargedService) throws URISyntaxException {
        log.debug("REST request to update ChargedService : {}", chargedService);
        if (chargedService.getId() == null) {
            return create(chargedService);
        }
        ChargedService result = chargedServiceService.save(chargedService);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, chargedService.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}/charged-services")
    public List<ChargedServiceDTO> getAllByRegistrationId(@PathVariable Long id) {
        log.debug("REST request to get all ChargedServices by registration id: {}", id);
        final List<ChargedService> result = chargedServiceService.findAllByRegistrationId(id);
        final Map<Long, Invoice> invoiceMap = invoiceService.getInvoicedChargedServices(id);
        return result.stream().map(cs -> {
            ChargedServiceDTO dto = new ChargedServiceDTO(cs);
            dto.setInvoiceNumber(invoiceMap.get(cs.getId()) != null ? invoiceMap.get(cs.getId()).getInvoiceNumber() : null);
            return dto;
        }).collect(Collectors.toList());

    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/charged-services/{id}")
    public ResponseEntity<ChargedService> getById(@PathVariable Long id) {
        log.debug("REST request to get ChargedService : {}", id);
        return chargedServiceService.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/charged-services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete ChargedService : {}", id);
        try {
            chargedServiceService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
