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
import java.util.Optional;
import java.util.Set;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.WorkplaceService;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.CongressMigrateWorkplaceVM;
import hu.congressline.pcs.web.rest.vm.CongressVM;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class CongressResource {
    private static final String ENTITY_NAME = "congress";
    private static final String MEETING_CODE_EXISTS = "meetingcodeexists";
    private static final String MEETING_CODE_ALREADY_EXISTS = "Meeting code already exists";

    private final CongressRepository congressRepository;
    private final CongressService congressService;
    private final WorkplaceService workplaceService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congresses")
    public ResponseEntity<Congress> create(@Valid @RequestBody Congress congress) throws URISyntaxException {
        log.debug("REST request to save Congress : {}", congress);
        if (congress.getId() != null) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new congress cannot already have an ID"))
                .body(null);
        } else if (congressRepository.findOneByMeetingCode(congress.getMeetingCode()).isPresent()) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, MEETING_CODE_EXISTS, MEETING_CODE_ALREADY_EXISTS))
                .body(null);
        }

        final Congress result = congressService.persist(congress);
        return ResponseEntity.created(new URI("/api/congresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getMeetingCode()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congresses/migrate-workplaces")
    public ResponseEntity<Void> migrateWorkplaces(@Valid @RequestBody CongressMigrateWorkplaceVM cmw) throws URISyntaxException {
        log.debug("REST request to migrate Congress workplaces from: {} to: {}", cmw.getFrom(), cmw.getTo());
        Congress from = congressService.getById(cmw.getFrom());
        workplaceService.migrate(cmw.getFrom(), cmw.getTo());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("congress.migrate", from.getName())).build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congresses")
    public ResponseEntity<Congress> update(@Valid @RequestBody Congress congress) throws URISyntaxException {
        log.debug("REST request to update Congress : {}", congress);
        if (congress.getId() == null) {
            return create(congress);
        }

        Optional<Congress> existingCongress = congressRepository.findOneByMeetingCode(congress.getMeetingCode());
        if (existingCongress.isPresent() && !existingCongress.get().getId().equals(congress.getId())) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, MEETING_CODE_EXISTS, MEETING_CODE_ALREADY_EXISTS))
                    .body(null);
        }
        Congress result = congressRepository.save(congress);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, congress.getMeetingCode()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congresses/online-reg-config")
    public ResponseEntity<OnlineRegConfig> updateOnlineRegConfig(@Valid @RequestBody OnlineRegConfig onlineRegConfig) {
        log.debug("REST request to update OnlineRegConfig : {}", onlineRegConfig);
        OnlineRegConfig result = congressService.saveConfig(onlineRegConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("onlineRegConfig", onlineRegConfig.getId().toString()))
            .body(result);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses")
    public List<Congress> getAll() {
        log.debug("REST request to get all Congresses");
        return congressRepository.findAllWithEagerRelationships();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/stripped-congresses")
    public List<CongressVM> getAllStriped() {
        log.debug("REST request to get all stripped Congresses");
        return congressService.findAllCongresses();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{id}")
    public ResponseEntity<Congress> getById(@PathVariable Long id) {
        log.debug("REST request to get Congress : {}", id);
        return congressRepository.findOneEagerlyById(id)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{id}/online-reg-config")
    public ResponseEntity<OnlineRegConfig> getOnlineRegConfigByCongressId(@PathVariable Long id) {
        log.debug("REST request to get congress reg config : {}", id);
        return congressService.findConfigByCongressId(id)
            .map(config -> {
                final Congress congress = congressRepository.findOneEagerlyById(config.getCongress().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Congress not found by id: " + id));
                config.setCongress(congress);
                return config;
            })
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{id}/online-reg-currencies")
    public Set<Currency> getOnlineRegCurrenciesByCongressId(@PathVariable Long id) {
        log.debug("REST request to get online reg currencies by congress id : {}", id);
        return congressService.getOnlineRegCurrenciesByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/congresses/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete Congress : {}", id);
        try {
            final String meetingCode = congressService.getById(id).getMeetingCode();
            congressService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, meetingCode)).build();
        } catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert(ENTITY_NAME, e)).body(null);
        }
    }
}
