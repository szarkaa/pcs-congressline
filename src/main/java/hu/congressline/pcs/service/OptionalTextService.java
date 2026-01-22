package hu.congressline.pcs.service;

import hu.congressline.pcs.domain.Congress;
import hu.congressline.pcs.domain.OptionalText;
import hu.congressline.pcs.repository.OptionalTextRepository;
import hu.congressline.pcs.web.rest.vm.OptionalTextVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class OptionalTextService {

    private final CongressService congressService;
    private final OptionalTextRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    public OptionalText save(@NonNull OptionalText optionalText) {
        return repository.save(optionalText);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OptionalText save(@NonNull OptionalTextVM viewModel) {
        OptionalText optionalText = viewModel.getId() != null ? getById(viewModel.getId()) : new OptionalText();
        optionalText.update(viewModel);
        if (optionalText.getCongress() == null) {
            optionalText.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(optionalText);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OptionalText> findById(Long id) {
        log.debug("Request to find Optional text : {}", id);
        return repository.findById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OptionalText getById(Long id) {
        log.debug("Request to get Optional text : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Optional text not found with id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public List<OptionalText> findAllByCongressId(Long id) {
        return repository.findAllByCongressId(id);
    }
}
