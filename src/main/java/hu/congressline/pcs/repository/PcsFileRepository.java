package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.PcsFile;

public interface PcsFileRepository extends JpaRepository<PcsFile, Long> {

    List<PcsFile> findAllByOnlineRegistrationId(Long id);

    void deleteAllByOnlineRegistrationId(Long id);
}
