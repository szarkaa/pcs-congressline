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

import hu.congressline.pcs.domain.OptionalText;
import hu.congressline.pcs.repository.OptionalTextRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class OptionalTextResource {
    private static final String ENTITY_NAME = "optionalText";

    private final OptionalTextRepository optionalTextRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/optional-texts")
    public ResponseEntity<OptionalText> createOptionalText(@Valid @RequestBody OptionalText optionalText) throws URISyntaxException {
        log.debug("REST request to save OptionalText : {}", optionalText);
        if (optionalText.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new optionalText cannot already have an ID"))
                .body(null);
        }
        OptionalText result = optionalTextRepository.save(optionalText);
        return ResponseEntity.created(new URI("/api/optional-texts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/optional-texts")
    public ResponseEntity<OptionalText> updateOptionalText(@Valid @RequestBody OptionalText optionalText) throws URISyntaxException {
        log.debug("REST request to update OptionalText : {}", optionalText);
        if (optionalText.getId() == null) {
            return createOptionalText(optionalText);
        }
        OptionalText result = optionalTextRepository.save(optionalText);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, optionalText.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-texts")
    public List<OptionalText> getAllOptionalTexts() {
        log.debug("REST request to get all OptionalTexts");
        return optionalTextRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-texts/congress/{id}")
    public List<OptionalText> getAllOptionalTextsByCongress(@PathVariable Long id) {
        log.debug("REST request to get all OptionalTexts by congress id: {}", id);
        return optionalTextRepository.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/optional-texts/{id}")
    public ResponseEntity<OptionalText> getOptionalText(@PathVariable Long id) {
        log.debug("REST request to get OptionalText : {}", id);
        return optionalTextRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/optional-texts/{id}")
    public ResponseEntity<Void> deleteOptionalText(@PathVariable Long id) {
        log.debug("REST request to delete OptionalText : {}", id);
        try {
            optionalTextRepository.deleteById(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
