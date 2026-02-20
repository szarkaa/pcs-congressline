package hu.congressline.pcs.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import hu.congressline.pcs.domain.PcsFile;
import hu.congressline.pcs.repository.PcsFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PcsFileService {

    private final PcsFileRepository repository;

    @SuppressWarnings("MissingJavadocMethod")
    public PcsFile save(PcsFile pcsFile) {
        log.debug("Request to save pcs file");
        return repository.save(pcsFile);
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public Optional<PcsFile> findById(Long id) {
        log.debug("Request to find pcs file by id: {}", id);
        return id != null ? repository.findById(id) : Optional.empty();
    }

    @SuppressWarnings("MissingJavadocMethod")
    @Transactional(readOnly = true)
    public PcsFile getById(Long id) {
        log.debug("Request to get pcs file by id: {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Pcs file not found by id: " + id));
    }

    @SuppressWarnings("MissingJavadocMethod")
    public void delete(Long id) {
        log.debug("Request to delete pcs files : {}", id);
        repository.deleteById(id);
    }
}
