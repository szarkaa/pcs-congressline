package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import hu.congressline.pcs.domain.OnlineRegConfig;

public interface OnlineRegConfigRepository extends JpaRepository<OnlineRegConfig, Long> {

    Optional<OnlineRegConfig> findOneByCongressId(Long id);

    void deleteAllByCongressId(Long id);
}
