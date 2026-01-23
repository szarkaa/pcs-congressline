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

import hu.congressline.pcs.domain.CongressHotel;
import hu.congressline.pcs.service.CongressHotelService;
import hu.congressline.pcs.service.dto.CongressHotelDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.CongressHotelVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CongressHotelResource {
    private static final String ENTITY_NAME = "congressHotel";
    private static final String CONGRESS_HOTEL_NOT_FOUND = "CongressHotel not found with id: ";

    private final CongressHotelService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congress-hotels")
    public ResponseEntity<CongressHotelDTO> create(@RequestBody CongressHotelVM viewModel) throws URISyntaxException {
        log.debug("REST request to save congress hotel: {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new congressHotel cannot already have an ID"))
                .body(null);
        }
        final Long id = service.save(viewModel).getId();
        CongressHotel result = service.getById(id);
        return ResponseEntity.created(new URI("/api/congress-hotels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getHotel().getCode()))
            .body(new CongressHotelDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congress-hotels")
    public ResponseEntity<CongressHotelDTO> update(@RequestBody CongressHotelVM viewModel) throws URISyntaxException {
        log.debug("REST request to update congress hotel: {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }
        CongressHotel result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new CongressHotelDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress-hotels/congress/{id}")
    public List<CongressHotelDTO> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all congress hotels by congress id: {}", id);
        return service.findByCongressId(id).stream().map(CongressHotelDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress-hotels/{id}")
    public ResponseEntity<CongressHotelDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get congress hotel: {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new CongressHotelDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/congress-hotels/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete congress hotel: {}", id);
        try {
            CongressHotel congressHotel = service.getById(id);
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, congressHotel.getHotel().getCode())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
