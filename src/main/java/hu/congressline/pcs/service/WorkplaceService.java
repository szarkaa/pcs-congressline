package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.Workplace;
import hu.congressline.pcs.repository.CountryRepository;
import hu.congressline.pcs.repository.RegistrationRepository;
import hu.congressline.pcs.repository.WorkplaceRepository;
import hu.congressline.pcs.web.rest.vm.WorkplaceVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class WorkplaceService {

    private final WorkplaceRepository repository;
    private final RegistrationRepository registrationRepository;
    private final CountryRepository countryRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public Workplace save(Workplace workplace) {
        log.debug("Request to save Workplace : {}", workplace);
        return repository.save(workplace);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public Workplace save(@NonNull WorkplaceVM viewModel) {
        Workplace workplace = viewModel.getId() != null ? getById(viewModel.getId()) : new Workplace();
        workplace.update(viewModel);
        workplace.setCountry(viewModel.getCountryId() != null ? countryRepository.findById(viewModel.getCountryId()).orElse(null) : null);
        if (workplace.getCongress() == null && viewModel.getCongressId() != null) {
            workplace.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(workplace);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Workplace> findAll() {
        log.debug("Request to get all Workplaces");
        return repository.findAll();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<Workplace> findById(Long id) {
        log.debug("Request to find Workplace : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Workplace getById(Long id) {
        log.debug("Request to get Workplace : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Workplace not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete Workplace : {}", id);
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void deleteByRegistrationId(Long registrationId) {
        log.debug("Request to delete workplace by registration id: {}", registrationId);
        registrationRepository.findById(registrationId).ifPresent(registration -> {
            if (registration.getWorkplace() != null && registrationRepository.countByWorkplaceId(registration.getWorkplace().getId(), registrationId) == 0) {
                repository.deleteById(registration.getWorkplace().getId());
            }
        });
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public List<Workplace> findByCongressId(Long id) {
        log.debug("Request to get all Workplaces by congress id: {}", id);
        List<Workplace> result;
        if (id == null || id.equals(0L)) {
            result = repository.findByCongressIsNullOrderByName();
        } else {
            result = repository.findByCongressIdOrderByName(id);
        }
        return result;
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<Workplace> findAllForCongressId(Long id) {
        log.debug("Request to get all Workplaces by Congress id and the ones with congress null value");
        return repository.findAllForCongressId(id);
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
            repository.save(copy);
        });

        toCongress.setMigratedFromCongressCode(fromCongress.getMeetingCode());
        congressService.update(toCongress);
    }
}
