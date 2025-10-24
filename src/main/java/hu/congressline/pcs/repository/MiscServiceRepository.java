package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.MiscService;

public interface MiscServiceRepository extends JpaRepository<MiscService, Long> {

    List<MiscService> findByCongressIdOrderByName(Long id);
}
