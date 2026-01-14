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
import hu.congressline.pcs.web.rest.util.HeaderUtil;
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
    public ResponseEntity<OnlineRegCustomQuestion> create(@Valid @RequestBody OnlineRegCustomQuestion question) throws URISyntaxException {
        log.debug("REST request to save OnlineRegCustomQuestion");
        if (question.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil
                .createFailureAlert(ENTITY_NAME, "idexists", "A new online reg custom question cannot already have an ID"))
                .body(null);
        }

        OnlineRegCustomQuestion result = service.save(question);
        return ResponseEntity.created(new URI("/api/congresses/online/custom-questions/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, question.getId().toString()))
                .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @RequestMapping("/online/custom-questions")
    public ResponseEntity<OnlineRegCustomQuestion> update(@Valid @RequestBody OnlineRegCustomQuestion question) throws URISyntaxException {
        log.debug("REST request to update OnlineRegCustomQuestion : {}", question);
        if (question.getId() == null) {
            return create(question);
        }

        OnlineRegCustomQuestion result = service.save(question);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, question.getId().toString()))
                .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/{id}/online/custom-questions")
    public List<OnlineRegCustomQuestion> getQuestionsByCongressId(@PathVariable Long id) {
        log.debug("REST request to get OnlineRegCustomQuestions by congress id : {}", id);
        return service.findAllByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/online/custom-questions/{id}")
    public ResponseEntity<OnlineRegCustomQuestion> getById(@PathVariable Long id) {
        log.debug("REST request to get OnlineRegCustomQuestion by id : {}", id);
        return service.findById(id)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/online/custom-questions/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete OnlineRegCustomQuestion : {}", id);
        try {
            service.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }

    }

}
