package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkplaceService {

    private final WorkplaceRepository workplaceRepository;
    private final RegistrationRepository registrationRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public Workplace save(Workplace workplace) {
        log.debug("Request to save Workplace : {}", workplace);
        return workplaceRepository.save(workplace);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Workplace> findAll() {
        log.debug("Request to get all Workplaces");
        return workplaceRepository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Workplace> findById(Long id) {
        log.debug("Request to find Workplace : {}", id);
        return workplaceRepository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Workplace getById(Long id) {
        log.debug("Request to get Workplace : {}", id);
        return workplaceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Workplace not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete Workplace : {}", id);
        workplaceRepository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteByRegistrationId(Long registrationId) {
        log.debug("Request to delete workplace by registration id: {}", registrationId);
        registrationRepository.findById(registrationId).ifPresent(registration -> {
            if (registration.getWorkplace() != null && registrationRepository.countByWorkplaceId(registration.getWorkplace().getId(), registrationId) == 0) {
                workplaceRepository.deleteById(registration.getWorkplace().getId());
            }
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Workplace> findByCongressId(Long id) {
        log.debug("Request to get all Workplaces by congress id: {}", id);
        List<Workplace> result;
        if (id == null || id.equals(0L)) {
            result = workplaceRepository.findByCongressIsNullOrderByName();
        } else {
            result = workplaceRepository.findByCongressIdOrderByName(id);
        }
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<Workplace> findAllForCongressId(Long id) {
        log.debug("Request to get all Workplaces by Congress id and the ones with congress null value");
        return workplaceRepository.findAllForCongressId(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public void merge(Long workplaceId, List<Long> selectedWorkplaceIdList) {
        Workplace workplace = getById(workplaceId);
        registrationRepository.updateMergedWorkplaceInRegistrations(workplace, selectedWorkplaceIdList);
        selectedWorkplaceIdList.forEach(this::delete);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional
    public void migrate(Long fromCongressId, Long toCongressId) {
        Congress fromCongress = congressService.getById(fromCongressId);
        Congress toCongress = congressService.getById(toCongressId);
        final List<Workplace> workplaces = findByCongressId(fromCongressId);
        workplaces.forEach(workplace -> {
            final Workplace copy = Workplace.copy(workplace);
            copy.setCongress(toCongress);
            workplaceRepository.save(copy);
        });

        toCongress.setMigratedFromCongressCode(fromCongress.getMeetingCode());
        congressService.update(toCongress);
    }
}
