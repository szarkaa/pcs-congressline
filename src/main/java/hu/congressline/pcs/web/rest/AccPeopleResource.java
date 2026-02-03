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
import hu.congressline.pcs.service.AccPeopleService;
import hu.congressline.pcs.service.dto.AccPeopleDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.AccPeopleVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AccPeopleResource {
    private static final String ENTITY_NAME = "accPeople";

    private final AccPeopleService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/acc-people")
    public ResponseEntity<AccPeopleDTO> create(@Valid @RequestBody AccPeopleVM viewModel) throws URISyntaxException {
        log.debug("REST request to save acc people : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new viewModel cannot already have an ID"))
                .body(null);
        }

        AccPeople result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/acc-people/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(new AccPeopleDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/acc-people")
    public ResponseEntity<AccPeopleDTO> update(@Valid @RequestBody AccPeopleVM viewModel) throws URISyntaxException {
        log.debug("REST request to update acc people : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        AccPeople result = service.save(viewModel);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
            .body(new AccPeopleDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/registration-registration-type/{registrationRegistrationTypeId}/acc-peoples")
    public List<AccPeopleDTO> getAllByRegistrationRegistrationTypeId(@PathVariable Long registrationRegistrationTypeId) {
        log.debug("REST request to get all acc people by registration registration type id {}", registrationRegistrationTypeId);
        return service.findAllByRegistrationRegistrationTypeId(registrationRegistrationTypeId).stream().map(AccPeopleDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/acc-people/{id}")
    public ResponseEntity<AccPeopleDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get acc people : {}", id);
        return service.findById(id)
            .map(result -> new ResponseEntity<>(new AccPeopleDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/acc-people/{id}")
    public ResponseEntity<Void> deleteAccPeople(@PathVariable Long id) {
        log.debug("REST request to delete acc people : {}", id);
        service.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

}
