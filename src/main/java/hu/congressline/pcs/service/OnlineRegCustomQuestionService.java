package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegCustomQuestion;
import hu.congressline.pcs.repository.OnlineRegCustomQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class OnlineRegCustomQuestionService {

    private final OnlineRegCustomQuestionRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<OnlineRegCustomQuestion> findById(Long id) {
        log.debug("Request to find OnlineRegCustomQuestion : {}", id);
        return repository.findById(id);
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

    public OnlineRegCustomQuestion save(OnlineRegCustomQuestion onlineRegCustomQuestion) {
        return repository.save(onlineRegCustomQuestion);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

}
