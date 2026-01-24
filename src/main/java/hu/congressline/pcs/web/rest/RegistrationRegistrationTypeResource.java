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

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.service.DiscountService;
import hu.congressline.pcs.service.PriceService;
import hu.congressline.pcs.service.RegistrationRegistrationTypeService;
import hu.congressline.pcs.service.dto.RegFeeDTO;
import hu.congressline.pcs.service.dto.RegistrationRegistrationTypeDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.RegistrationRegistrationTypeVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class RegistrationRegistrationTypeResource {
    private static final String ENTITY_NAME = "registrationRegistrationType";

    private final DiscountService discountService;
    private final RegistrationRegistrationTypeService service;
    private final PriceService priceService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/registration-registration-types")
    public ResponseEntity<RegistrationRegistrationTypeDTO> create(@Valid @RequestBody RegistrationRegistrationTypeVM viewModel) throws URISyntaxException {
        log.debug("REST request to save registration registration type : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new registration registration type cannot already have an ID"))
                .body(null);
        }

        RegistrationRegistrationType result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/registration-registration-types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new RegistrationRegistrationTypeDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/registration-registration-types")
    public ResponseEntity<RegistrationRegistrationTypeDTO> update(@Valid @RequestBody RegistrationRegistrationTypeVM viewModel) throws URISyntaxException {
        log.debug("REST request to update registration registration type : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        RegistrationRegistrationType result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new RegistrationRegistrationTypeDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{registrationId}/registration-types/{registrationTypeId}/calculate-reg-fee")
    public ResponseEntity<RegFeeDTO> getRegFeeByRegistrationTypes(@PathVariable Long registrationId, @PathVariable Long registrationTypeId) {
        log.debug("REST request to get all registration registration type by registration id: {}, registrationTypeId: {}", registrationId, registrationTypeId);
        return ResponseEntity.ok().body(new RegFeeDTO(service.calculateRegFeeByRegistrationTypeId(registrationId, registrationTypeId)));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registrations/{id}/registration-registration-types")
    public List<RegistrationRegistrationTypeDTO> getAllByRegistrationId(@PathVariable Long id) {
        log.debug("REST request to get all registration registration type dtos by registration id {}", id);
        return service.findAllByRegistrationId(id)
                .stream().map(rrt -> {
                    RegistrationRegistrationTypeDTO dto = new RegistrationRegistrationTypeDTO(rrt);
                    dto.setPriceWithDiscount(discountService.getPriceWithDiscount(rrt.getPayingGroupItem(), rrt.getChargeableItemPrice(), priceService.getScale(rrt)));
                    return dto;
                }).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registration-registration-types/{id}")
    public ResponseEntity<RegistrationRegistrationTypeDTO> get(@PathVariable Long id) {
        log.debug("REST request to get registration registration type : {}", id);
        return service.findById(id)
            .map(result -> {
                RegistrationRegistrationTypeDTO dto = new RegistrationRegistrationTypeDTO(result);
                dto.setPriceWithDiscount(discountService.getPriceWithDiscount(result.getPayingGroupItem(), result.getChargeableItemPrice(), priceService.getScale(result)));
                return new ResponseEntity<>(dto, HttpStatus.OK);
            })
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/registration-registration-types/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete registration registration type : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
