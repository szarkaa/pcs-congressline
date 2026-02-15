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
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.service.CongressHotelService;
import hu.congressline.pcs.service.CongressService;
import hu.congressline.pcs.service.OptionalServiceService;
import hu.congressline.pcs.service.OptionalTextService;
import hu.congressline.pcs.service.RegistrationTypeService;
import hu.congressline.pcs.service.WorkplaceService;
import hu.congressline.pcs.service.dto.CongressDTO;
import hu.congressline.pcs.service.dto.CurrencyDTO;
import hu.congressline.pcs.service.dto.OnlineRegConfigDTO;
import hu.congressline.pcs.service.dto.StrippedCongressDTO;
import hu.congressline.pcs.web.rest.util.HeaderUtil;
import hu.congressline.pcs.web.rest.vm.CongressMigrateItemVM;
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
    private final CongressHotelService congressHotelService;
    private final WorkplaceService workplaceService;
    private final RegistrationTypeService registrationTypeService;
    private final OptionalServiceService optionalServiceService;
    private final OptionalTextService optionalTextService;

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congresses")
    public ResponseEntity<CongressDTO> create(@Valid @RequestBody CongressVM viewModel) throws URISyntaxException {
        log.debug("REST request to save congress view model : {}", viewModel);
        if (viewModel.getId() != null) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new congress cannot already have an ID"))
                .body(null);
        } else if (congressRepository.findOneByMeetingCode(viewModel.getMeetingCode()).isPresent()) {
            return ResponseEntity
                .badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, MEETING_CODE_EXISTS, MEETING_CODE_ALREADY_EXISTS))
                .body(null);
        }

        final Congress result = congressService.persist(viewModel);
        return ResponseEntity.created(new URI("/api/congresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getMeetingCode()))
            .body(new CongressDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congresses")
    public ResponseEntity<CongressDTO> update(@Valid @RequestBody CongressVM viewModel) throws URISyntaxException {
        log.debug("REST request to update congress : {}", viewModel);
        if (viewModel.getId() == null) {
            return create(viewModel);
        }

        Optional<Congress> existingCongress = congressRepository.findOneByMeetingCode(viewModel.getMeetingCode());
        if (existingCongress.isPresent() && !existingCongress.get().getId().equals(viewModel.getId())) {
            return ResponseEntity.badRequest()
                .headers(HeaderUtil.createFailureAlert(ENTITY_NAME, MEETING_CODE_EXISTS, MEETING_CODE_ALREADY_EXISTS))
                .body(null);
        }
        Congress result = congressService.update(viewModel);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, viewModel.getMeetingCode()))
            .body(new CongressDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PostMapping("/congresses/migrate-items")
    public ResponseEntity<Void> migrateItems(@Valid @RequestBody CongressMigrateItemVM cmw) {
        log.debug("REST request to migrate congress items from: {} to: {}", cmw.getFrom(), cmw.getTo());
        workplaceService.migrate(cmw.getFrom(), cmw.getTo());
        registrationTypeService.migrate(cmw.getFrom(), cmw.getTo());
        congressHotelService.migrate(cmw.getFrom(), cmw.getTo());
        optionalServiceService.migrate(cmw.getFrom(), cmw.getTo());
        optionalTextService.migrate(cmw.getFrom(), cmw.getTo());
        Congress from = congressService.getById(cmw.getFrom());
        Congress to = congressService.getById(cmw.getTo());
        to.setMigratedFromCongressCode(from.getMeetingCode());
        congressService.save(to);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityCreationAlert("congress.migrate", from.getName())).build();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses")
    public List<CongressDTO> getAll() {
        log.debug("REST request to get all congresses");
        return congressRepository.findAllWithEagerRelationships().stream().map(CongressDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/stripped-congresses")
    public List<StrippedCongressDTO> getAllStriped() {
        log.debug("REST request to get all stripped congresses");
        return congressService.findAllCongresses().stream().map(StrippedCongressDTO::new).toList();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{id}")
    public ResponseEntity<CongressDTO> getBy(@PathVariable Long id) {
        log.debug("REST request to get congress : {}", id);
        return congressRepository.findOneEagerlyById(id)
            .map(result -> new ResponseEntity<>(new CongressDTO(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @PutMapping("/congresses/online-reg-config")
    public ResponseEntity<OnlineRegConfigDTO> updateOnlineRegConfig(@Valid @RequestBody OnlineRegConfigDTO onlineRegConfig) {
        log.debug("REST request to update online reg config : {}", onlineRegConfig);
        OnlineRegConfig result = congressService.saveOnlineRegConfig(onlineRegConfig);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("onlineRegConfig", onlineRegConfig.getId().toString()))
            .body(new OnlineRegConfigDTO(result));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{congressId}/online-reg-config")
    public ResponseEntity<OnlineRegConfigDTO> getOnlineRegConfigBy(@PathVariable Long congressId) {
        log.debug("REST request to get online reg config by congress id: {}", congressId);
        return congressService.findConfigByCongressId(congressId)
            .map(OnlineRegConfigDTO::new)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @GetMapping("/congresses/{congressId}/online-reg-currencies")
    public Set<CurrencyDTO> getOnlineRegCurrenciesBy(@PathVariable Long congressId) {
        log.debug("REST request to get online reg currencies by congress id : {}", congressId);
        return congressService.getOnlineRegCurrenciesByCongressId(congressId).stream().map(CurrencyDTO::new).collect(Collectors.toSet());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @DeleteMapping("/congresses/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("REST request to delete congress : {}", id);
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
