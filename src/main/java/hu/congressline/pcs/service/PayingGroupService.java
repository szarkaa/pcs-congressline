package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.PayingGroup;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.PayingGroupItemRepository;
import hu.congressline.pcs.repository.PayingGroupRepository;
import hu.congressline.pcs.web.rest.vm.PayingGroupVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PayingGroupService {

    private final PayingGroupItemRepository payingGroupItemRepository;
    private final PayingGroupRepository repository;
    private final CountryRepository countryRepository;
    private final CurrencyRepository currencyRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public PayingGroup save(PayingGroup payingGroup) {
        log.debug("Request to save paying group : {}", payingGroup);
        return repository.save(payingGroup);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public PayingGroup save(@NonNull PayingGroupVM viewModel) {
        PayingGroup payingGroup = viewModel.getId() != null ? getById(viewModel.getId()) : new PayingGroup();
        payingGroup.update(viewModel);
        payingGroup.setCountry(viewModel.getCountryId() != null ? countryRepository.findById(viewModel.getCountryId()).orElse(null) : null);
        payingGroup.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (payingGroup.getCongress() == null) {
            payingGroup.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(payingGroup);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<PayingGroup> findById(Long id) {
        log.debug("Request to find paying group : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public PayingGroup getById(Long id) {
        log.debug("Request to get paying group : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("paying group not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete paying group : {}", id);
        payingGroupItemRepository.deleteAllByPayingGroupId(id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<PayingGroup> findAllForCongressId(Long id) {
        log.debug("Request to get all paying groups by congress id and the ones with congress null value");
        return repository.findByCongressIdOrderByName(id);
    }

}
