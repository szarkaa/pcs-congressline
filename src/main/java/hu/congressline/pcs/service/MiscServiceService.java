package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.MiscService;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.MiscServiceRepository;
import hu.congressline.pcs.repository.VatInfoRepository;
import hu.congressline.pcs.web.rest.vm.MiscServiceVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class MiscServiceService {

    private final MiscServiceRepository repository;
    private final VatInfoRepository vatInfoRepository;
    private final CurrencyRepository currencyRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public MiscService save(MiscService miscService) {
        log.debug("Request to save misc service : {}", miscService);
        return repository.save(miscService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public MiscService save(@NonNull MiscServiceVM viewModel) {
        MiscService miscService = viewModel.getId() != null ? getById(viewModel.getId()) : new MiscService();
        miscService.update(viewModel);
        miscService.setVatInfo(viewModel.getVatInfoId() != null ? vatInfoRepository.findById(viewModel.getVatInfoId()).orElse(null) : null);
        miscService.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (miscService.getCongress() == null) {
            miscService.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(miscService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<MiscService> findById(Long id) {
        log.debug("Request to find misc service : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public MiscService getById(Long id) {
        log.debug("Request to get misc service : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Misc service not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete misc service : {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<MiscService> findByCongressId(Long id) {
        log.debug("Request to get all misc services by congress id: {}", id);
        return repository.findByCongressIdOrderByName(id);
    }

}
