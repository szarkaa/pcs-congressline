package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.OptionalText;

public interface OptionalTextRepository extends JpaRepository<OptionalText, Long> {

    List<OptionalText> findAllByCongressId(Long id);
}
