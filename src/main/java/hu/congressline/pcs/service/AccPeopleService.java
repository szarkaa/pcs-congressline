package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.AccPeople;
import hu.congressline.pcs.repository.AccPeopleRepository;
import hu.congressline.pcs.web.rest.vm.AccPeopleVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class AccPeopleService {

    private final RegistrationRegistrationTypeService rrtService;
    private final AccPeopleRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    public AccPeople save(@NonNull AccPeople optionalText) {
        return repository.save(optionalText);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public AccPeople save(@NonNull AccPeopleVM viewModel) {
        AccPeople accPeople = viewModel.getId() != null ? getById(viewModel.getId()) : new AccPeople();
        accPeople.update(viewModel);
        if (accPeople.getRegistrationRegistrationType() == null) {
            accPeople.setRegistrationRegistrationType(rrtService.getById(viewModel.getRegistrationRegistrationTypeId()));
        }
        return repository.save(accPeople);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<AccPeople> findById(Long id) {
        log.debug("Request to find acc people : {}", id);
        return id != null ? repository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public AccPeople getById(Long id) {
        log.debug("Request to get acc people : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Acc people not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<AccPeople> findAllByRegistrationRegistrationTypeId(Long id) {
        return repository.findAllByRegistrationRegistrationTypeId(id);
    }
}
