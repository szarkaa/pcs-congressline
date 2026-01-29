package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Currency;
import hu.congressline.pcs.domain.OnlineRegConfig;
import hu.congressline.pcs.repository.BankAccountRepository;
import hu.congressline.pcs.repository.CongressRepository;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.OnlineRegConfigRepository;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import hu.congressline.pcs.security.RandomUtil;
import hu.congressline.pcs.web.rest.vm.CongressVM;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class CongressService {

    private final CongressRepository congressRepository;
    private final OnlineRegConfigRepository onlineRegConfigRepository;
    private final OnlineRegCustomQuestionRepository onlineRegCustomQuestionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CurrencyService currencyService;
    private final CountryRepository countryRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public List<Congress> findAllCongresses() {
        return congressRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Congress> findById(Long id) {
        log.debug("Request to find Congress : {}", id);
        return congressRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress getById(Long id) {
        log.debug("Request to get Congress : {}", id);
        return congressRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Congress not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress getEagerById(Long id) {
        log.debug("Request to get Congress eagerly: {}", id);
        return congressRepository.findOneEagerlyById(id).orElseThrow(() -> new IllegalArgumentException("Congress eager not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Congress getByMeetingCode(String meetingCode) {
        log.debug("Request to get Congress by meeting code: {}", meetingCode);
        return congressRepository.findOneByMeetingCode(meetingCode).orElseThrow(() -> new IllegalArgumentException("Congress not found with meeting code: " + meetingCode));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        onlineRegCustomQuestionRepository.deleteAllByCongressId(id);
        onlineRegConfigRepository.deleteAllByCongressId(id);
        congressRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress persist(CongressVM viewModel) {
        Congress congress = new Congress();
        congress.setUuid(RandomUtil.generateRandomAlphanumericString());
        congress.update(viewModel);
        congress.setDefaultCountry(viewModel.getDefaultCountryId() != null ? countryRepository.findById(viewModel.getDefaultCountryId()).orElse(null) : null);
        congress.setCurrencies(currencyService.getAllByIds(viewModel.getCurrencyIds()));
        congress.setOnlineRegCurrencies(currencyService.getAllByIds(viewModel.getOnlineRegCurrencyIds()));
        congress.setBankAccounts(new HashSet<>(bankAccountRepository.findAllById(viewModel.getBankAccountIds())));
        Congress result = congressRepository.save(congress);
        OnlineRegConfig config = new OnlineRegConfig();
        config.setCongress(result);
        onlineRegConfigRepository.save(config);
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress update(CongressVM viewModel) {
        Congress congress = getById(viewModel.getId());
        congress.setUuid(RandomUtil.generateRandomAlphanumericString());
        congress.update(viewModel);
        congress.setDefaultCountry(viewModel.getDefaultCountryId() != null ? countryRepository.findById(viewModel.getDefaultCountryId()).orElse(null) : null);
        congress.setCurrencies(currencyService.getAllByIds(viewModel.getCurrencyIds()));
        congress.setOnlineRegCurrencies(currencyService.getAllByIds(viewModel.getOnlineRegCurrencyIds()));
        congress.setBankAccounts(new HashSet<>(bankAccountRepository.findAllById(viewModel.getBankAccountIds())));
        return congressRepository.save(congress);

    }

    @SuppressWarnings("MissingJavadocMethod")
    public Congress save(Congress congress) {
        return congressRepository.save(congress);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Optional<OnlineRegConfig> findConfigByCongressId(Long id) {
        return onlineRegConfigRepository.findOneByCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegConfig getConfigByCongressId(Long id) {
        return onlineRegConfigRepository.findOneByCongressId(id).orElseThrow(() -> new IllegalArgumentException("Online reg config not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegConfig saveConfig(OnlineRegConfig onlineRegConfig) {
        return onlineRegConfigRepository.save(onlineRegConfig);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Set<Currency> getOnlineRegCurrenciesByCongressId(Long id) {
        return findById(id).map(Congress::getOnlineRegCurrencies).orElse(new HashSet<>());
    }
}
