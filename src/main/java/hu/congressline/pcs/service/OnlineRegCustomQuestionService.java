package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import hu.congressline.pcs.repository.CurrencyRepository;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import hu.congressline.pcs.web.rest.vm.OnlineRegCustomQuestionVM;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OnlineRegCustomQuestionService {

    private final OnlineRegCustomQuestionRepository repository;
    private final CurrencyRepository currencyRepository;
    private final CongressService congressService;

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegCustomQuestion save(@NonNull OnlineRegCustomQuestionVM viewModel) {
        OnlineRegCustomQuestion customQuestion = viewModel.getId() != null ? getById(viewModel.getId()) : new OnlineRegCustomQuestion();
        customQuestion.update(viewModel);
        customQuestion.setCurrency(viewModel.getCurrencyId() != null ? currencyRepository.findById(viewModel.getCurrencyId()).orElse(null) : null);
        if (customQuestion.getCongress() == null) {
            customQuestion.setCongress(congressService.getById(viewModel.getCongressId()));
        }
        return repository.save(customQuestion);
    }

    @SuppressWarnings("MissingJavadocMethod")
    public OnlineRegCustomQuestion save(OnlineRegCustomQuestion onlineRegCustomQuestion) {
        return repository.save(onlineRegCustomQuestion);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OnlineRegCustomQuestion> findById(Long id) {
        log.debug("Request to find OnlineRegCustomQuestion : {}", id);
        return id != null ? repository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public OnlineRegCustomQuestion getById(Long id) {
        log.debug("Request to get OnlineRegCustomQuestion : {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("OnlineRegCustomQuestion not found by id: " + id));
    }

    public List<OnlineRegCustomQuestion> findAllByCongressId(Long id) {
        return repository.findAllByCongressIdOrderByQuestionOrder(id);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

}
