package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RegistrationTypeRepository;
import hu.congressline.pcs.repository.VatInfoRepository;
import hu.congressline.pcs.web.rest.vm.RegistrationTypeVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RegistrationTypeService {

    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final RegistrationTypeRepository repository;
    private final RegistrationRegistrationTypeService rrtService;
    private final CongressService congressService;
    private final VatInfoRepository vatInfoRepository;
    private final CurrencyRepository currencyRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public RegistrationType save(RegistrationType registrationType) {
        log.debug("Request to save registration type : {}", registrationType);
        RegistrationType result = repository.save(registrationType);
        if (registrationType.getId() != null) {
            recalculateRegFeeByRegistrationTypeId(registrationType.getId());
        }
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public RegistrationType save(@NonNull RegistrationTypeVM viewModel) {
        RegistrationType registrationType = viewModel.getId() != null ? getById(viewModel.getId()) : new RegistrationType();
        registrationType.update(viewModel);
        registrationType.setVatInfo(viewModel.getVatInfoId() != null ? vatInfoRepository.findById(viewModel.getVatInfoId()).orElse(null) : null);
        registrationType.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (registrationType.getCongress() == null) {
            registrationType.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(registrationType);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegistrationType> findByCongressId(Long id) {
        log.debug("Request to get all registration type by congress id: {}", id);
        List<RegistrationType> result = repository.findByCongressId(id);
        result.sort(Comparator.comparing(RegistrationType::getName));
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RegistrationType> findById(Long id) {
        log.debug("Request to find registration type by id: {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RegistrationType getById(Long id) {
        log.debug("Request to get registration type by id : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Registration type not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    protected void recalculateRegFeeByRegistrationTypeId(Long id) {
        log.debug("Request to get calculate reg fee by registration type : {}", id);
        List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationTypeId(id);
        rrtList.forEach(rrt -> {
            rrt.setRegFee(null);
            rrtService.calculateRegFee(rrt);
        });
        rrtService.save(rrtList);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete registration type : {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RegistrationType> findOneByCode(String code, Long congressId) {
        log.debug("Request to get registration type by code: {}", code);
        return repository.findOneByCodeAndCongressId(code, congressId);
    }
}
