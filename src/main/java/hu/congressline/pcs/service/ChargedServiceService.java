package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.ChargedService;
import hu.congressline.pcs.domain.ChargedServiceInvoiceHistory;
import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.repository.ChargedServiceInvoiceHistoryRepository;
import hu.congressline.pcs.repository.ChargedServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class ChargedServiceService {

    private final ChargedServiceRepository chargedServiceRepository;
    private final ChargedServiceInvoiceHistoryRepository csihRepository;

    @SuppressWarnings("MissingJavadocMethod")
    public ChargedService save(ChargedService chargedService) {
        log.debug("Request to save ChargedService : {}", chargedService);
        return chargedServiceRepository.save(chargedService);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<ChargedService> findAll() {
        log.debug("Request to get all ChargedServices");
        return chargedServiceRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<ChargedService> findAllByRegistrationId(Long id) {
        log.debug("Request to get all ChargedServices by registratration id");
        return chargedServiceRepository.findAllByRegistrationId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<ChargedService> findAllByCongress(Congress congress) {
        log.debug("Request to get all ChargedServices by congress: {}", congress);
        return chargedServiceRepository.findAllByRegistrationCongress(congress);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<ChargedService> findById(Long id) {
        log.debug("Request to find ChargedService : {}", id);
        return chargedServiceRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public ChargedService getById(Long id) {
        log.debug("Request to get ChargedService : {}", id);
        return chargedServiceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Charged service not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete ChargedService : {}", id);
        // if last invoice this item is on is a storno then it is deleteable
        final List<ChargedServiceInvoiceHistory> csihList = csihRepository.findAllByChargedServiceIdOrderByIdDesc(id);
        final ChargedServiceInvoiceHistory lastHistoryItem = csihList.stream().findFirst().orElse(null);
        if (lastHistoryItem != null && lastHistoryItem.getInvoice().getStorno()) {
            csihRepository.deleteAllInBatch(csihList);
        }
        chargedServiceRepository.deleteById(id);
    }
}
