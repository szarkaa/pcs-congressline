package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.ChargedServiceInvoiceHistory;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Registration;
import hu.congressline.pcs.repository.ChargeableItemRepository;
import hu.congressline.pcs.repository.ChargedServiceInvoiceHistoryRepository;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.web.rest.vm.ChargedServiceVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ChargedServiceService {

    private final RegistrationRepository registrationRepository;
    private final ChargedServiceRepository repository;
    private final ChargedServiceInvoiceHistoryRepository csihRepository;
    private final ChargeableItemRepository chargeableItemRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public ChargedService save(ChargedService chargedService) {
        log.debug("Request to save charged service : {}", chargedService);
        return repository.save(chargedService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public ChargedService save(@NonNull ChargedServiceVM viewModel) {
        ChargedService chargedService = viewModel.getId() != null ? getById(viewModel.getId()) : new ChargedService();
        chargedService.update(viewModel);
        chargedService.setChargeableItem(viewModel.getChargeableItemId() != null ? chargeableItemRepository.findById(viewModel.getChargeableItemId()).orElse(null) : null);
        if (chargedService.getRegistration() == null) {
            final Registration registration = registrationRepository.findById(viewModel.getRegistrationId())
                .orElseThrow(() -> new IllegalArgumentException("Registration not found by id: " + viewModel.getRegistrationId()));
            chargedService.setRegistration(registration);

        }
        return repository.save(chargedService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<ChargedService> findAllByRegistrationId(Long id) {
        log.debug("Request to get all charged service by registration id");
        return repository.findAllByRegistrationId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<ChargedService> findAllByCongress(Congress congress) {
        log.debug("Request to get all charged service by congress id: {}", congress.getId());
        return repository.findAllByRegistrationCongress(congress);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<ChargedService> findById(Long id) {
        log.debug("Request to find charged service : {}", id);
        return id != null ? repository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public ChargedService getById(Long id) {
        log.debug("Request to get charged service : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Charged service not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete charged service : {}", id);
        // if last invoice this item is on is a storno then it is deleteable
        final List<ChargedServiceInvoiceHistory> csihList = csihRepository.findAllByChargedServiceIdOrderByIdDesc(id);
        final ChargedServiceInvoiceHistory lastHistoryItem = csihList.stream().findFirst().orElse(null);
        if (lastHistoryItem != null && lastHistoryItem.getInvoice().getStorno()) {
            csihRepository.deleteAllInBatch(csihList);
        }
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteAllByRegistrationId(Long registrationId) {
        repository.findAllByRegistrationId(registrationId).stream().map(ChargedService::getId).forEach(this::delete);
    }
}
