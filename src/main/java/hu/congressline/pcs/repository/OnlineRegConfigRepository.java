package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.OnlineRegConfig;

import java.util.Optional;

public interface OnlineRegConfigRepository extends JpaRepository<OnlineRegConfig, Long> {

    Optional<OnlineRegConfig> findOneByCongressId(Long id);

    void deleteAllByCongressId(Long id);
}
