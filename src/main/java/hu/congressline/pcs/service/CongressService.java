package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.repository.OnlineRegConfigRepository;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import hu.congressline.pcs.service.util.RandomUtil;
import hu.congressline.pcs.web.rest.vm.CongressVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CongressService {

    private final CongressRepository congressRepository;
    private final OnlineRegConfigRepository onlineRegConfigRepository;
    private final OnlineRegCustomQuestionRepository onlineRegCustomQuestionRepository;
    private final WorkplaceService workplaceService;

    @SuppressWarnings("MissingJavadocMethod")
    public List<CongressVM> findAllCongresses() {
        List<Congress> congresses = congressRepository.findAll();
        return congresses.stream().map(CongressVM::new).collect(Collectors.toList());
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress findOne(Long id) {
        log.debug("Request to get Congress : {}", id);
        return congressRepository.findById(id).orElse(null);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void migrateWorkplaces(Long fromCongressId, Long toCongressId) {
        workplaceService.migrate(fromCongressId, toCongressId);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        final OnlineRegConfig config = onlineRegConfigRepository.findOneByCongressId(id);
        onlineRegCustomQuestionRepository.deleteAllByCongressId(id);
        onlineRegConfigRepository.delete(config);
        congressRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress persist(Congress congress) {
        if (congress.getUuid() == null) {
            congress.setUuid(RandomUtil.generateUniqueId());
        }
        Congress result = congressRepository.save(congress);
        OnlineRegConfig config = new OnlineRegConfig();
        config.setCongress(result);
        onlineRegConfigRepository.save(config);
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress update(Congress congress) {
        return congressRepository.save(congress);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegConfig findConfigByCongressId(Long id) {
        return onlineRegConfigRepository.findOneByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegConfig saveConfig(OnlineRegConfig onlineRegConfig) {
        return onlineRegConfigRepository.save(onlineRegConfig);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Set<Currency> getOnlineRegCurrenciesByCongressId(Long id) {
        return Optional.ofNullable(congressRepository.findOneWithEagerRelationships(id)).map(Congress::getOnlineRegCurrencies).orElse(new HashSet<>());
    }
}
