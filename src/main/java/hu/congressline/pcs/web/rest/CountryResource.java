package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.Optional;

import hu.congressline.pcs.domain.Country;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CountryResource {
    private static final String ENTITY_NAME = "country";
    private static final String COUNTRY_CODE_EXISTS = "countrycodeexists";
    private static final String COUNTRY_CODE_EXISTS_MSG = "Country code already exist!";

    private final CountryRepository countryRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/countries")
    public ResponseEntity<Country> create(@Valid @RequestBody Country country) throws URISyntaxException {
        log.debug("REST request to save Country : {}", country);
        country.setCode(country.getCode().toUpperCase());
        if (country.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new country cannot already have an ID"))
                .body(null);
        } else if (countryRepository.findOneByCodeIgnoreCase(country.getCode()).isPresent()) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, COUNTRY_CODE_EXISTS, COUNTRY_CODE_EXISTS_MSG))
                    .body(null);
        } else {
            Country result = countryRepository.save(country);
            return ResponseEntity.created(new URI("/api/countries/" + result.getId()))
                    .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
                    .body(result);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/countries")
    public ResponseEntity<Country> update(@Valid @RequestBody Country country) throws URISyntaxException {
        log.debug("REST request to update Country : {}", country);
        country.setCode(country.getCode().toUpperCase());
        if (country.getId() == null) {
            return create(country);
        }

        final Optional<Country> existingCountry = countryRepository.findOneByCodeIgnoreCaseAndIdNot(country.getCode(), country.getId());
        if (existingCountry.isPresent() && (existingCountry.get().getCode().equals(country.getCode()))) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, COUNTRY_CODE_EXISTS, COUNTRY_CODE_EXISTS_MSG))
                .body(null);
        } else {
            Country result = countryRepository.save(country);
            return ResponseEntity.ok()
                    .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, country.getId().toString()))
                    .body(result);
        }
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/countries")
    public List<Country> getAll() {
        log.debug("REST request to get all Countries");
        return countryRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/countries/{id}")
    public ResponseEntity<Country> getById(@PathVariable Long id) {
        log.debug("REST request to get Country : {}", id);
        return countryRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/countries/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Country : {}", id);
        try {
            countryRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }
}
