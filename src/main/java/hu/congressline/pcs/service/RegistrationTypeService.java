package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.repository.RegistrationRegistrationTypeRepository;
import hu.congressline.pcs.repository.RegistrationTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class RegistrationTypeService {

    private final RegistrationRegistrationTypeRepository rrtRepository;
    private final RegistrationTypeRepository rtRepository;
    private final RegistrationRegistrationTypeService rrtService;

    @SuppressWarnings("MissingJavadocMethod")
    public RegistrationType save(RegistrationType registrationType) {
        log.debug("Request to save RegistrationType : {}", registrationType);
        RegistrationType result = rtRepository.save(registrationType);
        if (registrationType.getId() != null) {
            recalculateRegFeeByRegistrationTypeId(registrationType.getId());
        }
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<RegistrationType> findByCongressId(Long id) {
        log.debug("Request to get all RegistrationType by congress id: {}", id);
        List<RegistrationType> result = rtRepository.findByCongressId(id);
        result.sort(Comparator.comparing(RegistrationType::getName));
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RegistrationType> findById(Long id) {
        log.debug("Request to find Congress : {}", id);
        return rtRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public RegistrationType getById(Long id) {
        log.debug("Request to get Congress : {}", id);
        return rtRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("RegistrationType not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    protected void recalculateRegFeeByRegistrationTypeId(Long id) {
        log.debug("Request to get calculate reg fee by RegistrationType : {}", id);
        List<RegistrationRegistrationType> rrtList = rrtRepository.findAllByRegistrationTypeId(id);
        rrtList.forEach(rrt -> {
            rrt.setRegFee(null);
            rrtService.setRegFee(rrt);
        });
        rrtService.save(rrtList);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete RegistrationType : {}", id);
        rtRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<RegistrationType> findOneByCode(String code, Long congressId) {
        log.debug("Request to get RegistrationType by code: {}", code);
        return rtRepository.findOneByCodeAndCongressId(code, congressId);
    }
}
