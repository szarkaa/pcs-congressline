package hu.congressline.pcs.web.rest;

import hu.congressline.pcs.service.WorkplaceService;
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

    private final CongressRepository congressRepository;
    private final CongressService congressService;
    private final WorkplaceService  workplaceService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congresses")
    public ResponseEntity<Congress> createCongress(@Valid @RequestBody Congress congress) throws URISyntaxException {
        log.debug("REST request to save Congress : {}", congress);
        if (congress.getId() != null) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert("congress", "idexists", "A new congress cannot already have an ID"))
                .body(null);
        } else if (congressRepository.findOneByMeetingCode(congress.getMeetingCode()).isPresent()) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert("congress", "meetingcodeexists", "Meeting code already exists"))
                .body(null);
        }

        final Congress result = congressService.persist(congress);
        return ResponseEntity.created(new URI("/api/congresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("congress", result.getId().toString()))
            .body(result);
    }


    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congresses/migrate-workplaces")
    public ResponseEntity<Void> migrateCongressWorkplaces(@Valid @RequestBody CongressMigrateWorkplaceVM cmw) throws URISyntaxException {
        log.debug("REST request to migrate Congress workplaces from: {} to: {}", cmw.getFrom(), cmw.getTo());
        Congress from = congressService.getById(cmw.getFrom());
        workplaceService.migrate(cmw.getFrom(), cmw.getTo());
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("congress.migrate", from.getName())).build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congresses")
    public ResponseEntity<Congress> updateCongress(@Valid @RequestBody Congress congress) throws URISyntaxException {
        log.debug("REST request to update Congress : {}", congress);
        if (congress.getId() == null) {
            return createCongress(congress);
        }

        Optional<Congress> existingCongress = congressRepository.findOneByMeetingCode(congress.getMeetingCode());
        if (existingCongress.isPresent() && (!existingCongress.get().getId().equals(congress.getId()))) {
            return ResponseEntity.badRequest()
                    .headers(HeaderUtil.createFailureAlert("congress", "meetingcodeexists", "Meeting code already exists"))
                    .body(null);
        }
        Congress result = congressRepository.save(congress);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("congress", congress.getId().toString()))
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
    public List<Congress> getAllCongresses() {
        log.debug("REST request to get all Congresses");
        return congressRepository.findAllWithEagerRelationships();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/stripped-congresses")
    public List<CongressVM> getAllStripedCongresses() {
        log.debug("REST request to get all stripped Congresses");
        return congressService.findAllCongresses();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{id}")
    public ResponseEntity<Congress> getCongress(@PathVariable Long id) {
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
                    .orElseThrow(() -> new IllegalArgumentException("Congress not found with id: " + id));
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
    public ResponseEntity<Void> deleteCongress(@PathVariable Long id) {
        log.debug("REST request to delete Congress : {}", id);
        try {
            congressService.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("congress", id.toString())).build();
        }
        catch (DataIntegrityViolationException e) {
            log.debug("Constraint violation exception during delete operation.", e);
            return ResponseEntity.badRequest().headers(HeaderUtil.createDeleteConstraintViolationAlert("congress", e)).body(null);
        }
    }
}
