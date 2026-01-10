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
import hu.congressline.pcs.repository.CongressHotelRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CongressHotelResource {
    private static final String ENTITY_NAME = "congressHotel";
    private static final String CONGRESS_HOTEL_NOT_FOUND = "CongressHotel not found with id: ";

    private final CongressHotelRepository congressHotelRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congress-hotels")
    public ResponseEntity<CongressHotel> create(@RequestBody CongressHotel congressHotel) throws URISyntaxException {
        log.debug("REST request to save CongressHotel : {}", congressHotel);
        if (congressHotel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new congressHotel cannot already have an ID"))
                .body(null);
        }
        final Long id = congressHotelRepository.save(congressHotel).getId();
        CongressHotel result = congressHotelRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(CONGRESS_HOTEL_NOT_FOUND + id));

        return ResponseEntity.created(new URI("/api/congress-hotels/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getHotel().getCode()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congress-hotels")
    public ResponseEntity<CongressHotel> update(@RequestBody CongressHotel congressHotel) throws URISyntaxException {
        log.debug("REST request to update CongressHotel : {}", congressHotel);
        if (congressHotel.getId() == null) {
            return create(congressHotel);
        }
        CongressHotel result = congressHotelRepository.save(congressHotel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, congressHotel.getId().toString()))
            .body(result);
    }

    /*
    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress-hotels")
    public List<CongressHotel> getAllCongressHotels() {
        log.debug("REST request to get all CongressHotels");
        return congressHotelRepository.findAll();
    }
    */

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress-hotels/congress/{id}")
    public List<CongressHotel> getAllByCongressId(@PathVariable Long id) {
        log.debug("REST request to get all CongressHotels by congress id: {}", id);
        return congressHotelRepository.findByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congress-hotels/{id}")
    public ResponseEntity<CongressHotel> getById(@PathVariable Long id) {
        log.debug("REST request to get CongressHotel : {}", id);
        return congressHotelRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/congress-hotels/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete CongressHotel : {}", id);
        try {
            CongressHotel congressHotel = congressHotelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(CONGRESS_HOTEL_NOT_FOUND + id));
            congressHotelRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, congressHotel.getHotel().getCode())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }

}
