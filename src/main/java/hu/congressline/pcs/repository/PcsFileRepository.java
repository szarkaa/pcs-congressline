package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.PcsFile;

public interface PcsFileRepository extends JpaRepository<PcsFile, Long> {

}
