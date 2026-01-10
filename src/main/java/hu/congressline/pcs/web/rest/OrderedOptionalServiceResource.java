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
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.OrderedOptionalService;
import hu.congressline.pcs.service.DiscountService;
import hu.congressline.pcs.service.OrderedOptionalServiceService;
import hu.congressline.pcs.service.PriceService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.OrderedOptionalServiceVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OrderedOptionalServiceResource {
    private static final String ENTITY_NAME = "orderedOptionalService";

    private final OrderedOptionalServiceService service;
    private final DiscountService discountService;
    private final PriceService priceService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/ordered-optional-services")
    public ResponseEntity<OrderedOptionalService> create(@Valid @RequestBody OrderedOptionalService orderedOptionalService) throws URISyntaxException {
        log.debug("REST request to save OrderedOptionalService : {}", orderedOptionalService);
        if (orderedOptionalService.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new ordered optional service cannot already have an ID"))
                .body(null);
        }
        OrderedOptionalService result = service.save(orderedOptionalService);
        return ResponseEntity.created(new URI("/api/ordered-optional-services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/ordered-optional-services")
    public ResponseEntity<OrderedOptionalService> update(@Valid @RequestBody OrderedOptionalService orderedOptionalService) throws URISyntaxException {
        log.debug("REST request to update OrderedOptionalService : {}", orderedOptionalService);
        if (orderedOptionalService.getId() == null) {
            return create(orderedOptionalService);
        }

        OrderedOptionalService result = service.save(orderedOptionalService);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, orderedOptionalService.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}/ordered-optional-services")
    public List<OrderedOptionalService> getAllByRegistrationId(@PathVariable Long id) {
        log.debug("REST request to get all OrderedOptionalServices by registration id: {}", id);
        return service.findAllByRegistrationId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}/ordered-optional-service-vms")
    public List<OrderedOptionalServiceVM> getAllVMsByRegistrationId(@PathVariable Long id) {
        log.debug("REST request to get all OrderedOptionalServiceVMs by registration id: {}", id);
        return service.findAllByRegistrationId(id).stream().map(oos -> {
            OrderedOptionalServiceVM vm = new OrderedOptionalServiceVM(oos);
            vm.setPriceWithDiscount(discountService.getPriceWithDiscount(oos.getPayingGroupItem(), oos.getChargeableItemPrice(), priceService.getScale(oos)));
            return vm;
        }).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/ordered-optional-services/{id}")
    public ResponseEntity<OrderedOptionalService> getById(@PathVariable Long id) {
        log.debug("REST request to get OrderedOptionalService : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/ordered-optional-services/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete OrderedOptionalService : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
