package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.OptionalServiceRepository;
import hu.congressline.pcs.repository.VatInfoRepository;
import hu.congressline.pcs.web.rest.vm.OptionalServiceVM;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OptionalServiceService {

    private final OptionalServiceRepository repository;
    private final VatInfoRepository vatInfoRepository;
    private final CurrencyRepository currencyRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public OptionalService save(OptionalService optionalService) {
        log.debug("Request to save optional service : {}", optionalService);
        return repository.save(optionalService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OptionalService save(@NonNull OptionalServiceVM viewModel) {
        OptionalService optionalService = viewModel.getId() != null ? getById(viewModel.getId()) : new OptionalService();
        optionalService.update(viewModel);
        optionalService.setVatInfo(viewModel.getVatInfoId() != null ? vatInfoRepository.findById(viewModel.getVatInfoId()).orElse(null) : null);
        optionalService.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (optionalService.getCongress() == null) {
            optionalService.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(optionalService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OptionalService> findById(Long id) {
        log.debug("Request to find optional service : {}", id);
        return id != null ? repository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OptionalService getById(Long id) {
        log.debug("Request to get optional service : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Optional service not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete optional service : {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<OptionalService> findByCongressId(Long id) {
        log.debug("Request to get all optional services by congress id: {}", id);
        return repository.findByCongressIdOrderByName(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Optional<OptionalService> findByCodeAndCongressId(@NotNull String code, @NotNull Long congressId) {
        return repository.findOneByCodeAndCongressId(code, congressId);
    }
}
