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
import java.util.List;

import hu.congressline.pcs.domain.AccPeople;
import hu.congressline.pcs.repository.AccPeopleRepository;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AccPeopleResource {
    private static final String ENTITY_NAME = "accPeople";

    private final AccPeopleRepository accPeopleRepository;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/acc-people")
    public ResponseEntity<AccPeople> create(@Valid @RequestBody AccPeople accPeople) throws URISyntaxException {
        log.debug("REST request to save AccPeople : {}", accPeople);
        if (accPeople.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new accPeople cannot already have an ID"))
                .body(null);
        }

        AccPeople result = accPeopleRepository.save(accPeople);
        return ResponseEntity.created(new URI("/api/acc-people/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/acc-people")
    public ResponseEntity<AccPeople> update(@Valid @RequestBody AccPeople accPeople) throws URISyntaxException {
        log.debug("REST request to update AccPeople : {}", accPeople);
        if (accPeople.getId() == null) {
            return create(accPeople);
        }

        AccPeople result = accPeopleRepository.save(accPeople);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, accPeople.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registration-registration-type/{registrationRegistrationTypeId}/acc-peoples")
    public List<AccPeople> getAllByRegistrationRegistrationTypeId(@PathVariable Long registrationRegistrationTypeId) {
        log.debug("REST request to get all AccPeoples by registrationRegistrationType id {}", registrationRegistrationTypeId);
        return accPeopleRepository.findAllByRegistrationRegistrationTypeId(registrationRegistrationTypeId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/acc-people/{id}")
    public ResponseEntity<AccPeople> getById(@PathVariable Long id) {
        log.debug("REST request to get AccPeople : {}", id);
        return accPeopleRepository.findById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/acc-people/{id}")
    public ResponseEntity<Void> deleteAccPeople(@PathVariable Long id) {
        log.debug("REST request to delete AccPeople : {}", id);
        accPeopleRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
