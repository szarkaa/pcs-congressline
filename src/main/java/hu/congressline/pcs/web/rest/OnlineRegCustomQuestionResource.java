package hu.congressline.pcs.web.rest;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import hu.congressline.pcs.service.OnlineRegCustomQuestionService;
import hu.congressline.pcs.service.dto.OnlineRegCustomQuestionDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.OnlineRegCustomQuestionVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/congresses")
public class OnlineRegCustomQuestionResource {
    private static final String ENTITY_NAME = "onlineRegCustomQuestion";

    private final OnlineRegCustomQuestionService service;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/online/custom-questions")
    public ResponseEntity<OnlineRegCustomQuestionDTO> create(@Valid @RequestBody OnlineRegCustomQuestionVM viewModel) throws URISyntaxException {
        log.debug("REST request to save online reg custom question by vm : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new online reg custom viewModel cannot already have an ID"))
                .body(null);
        }

        OnlineRegCustomQuestion result = service.save(viewModel);
        return ResponseEntity.created(new URI("/api/congresses/online/custom-questions/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, viewModel.getId().toString()))
                .body(new OnlineRegCustomQuestionDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/online/custom-questions")
    public ResponseEntity<OnlineRegCustomQuestionDTO> update(@Valid @RequestBody OnlineRegCustomQuestionVM viewModel) throws URISyntaxException {
        log.debug("REST request to update online reg custom question by vm: {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        OnlineRegCustomQuestion result = service.save(viewModel);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getId().toString()))
                .body(new OnlineRegCustomQuestionDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/{id}/online/custom-questions")
    public List<OnlineRegCustomQuestionDTO> getQuestionsByCongressId(@PathVariable Long id) {
        log.debug("REST request to get online reg custom question by congress id : {}", id);
        return service.findAllByCongressId(id).stream().map(OnlineRegCustomQuestionDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online/custom-questions/{id}")
    public ResponseEntity<OnlineRegCustomQuestionDTO> getById(@PathVariable Long id) {
        log.debug("REST request to get online reg custom question by id : {}", id);
        return service.findById(id)
                .map(result -> new ResponseEntity<>(new OnlineRegCustomQuestionDTO(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/online/custom-questions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete online reg custom question : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
