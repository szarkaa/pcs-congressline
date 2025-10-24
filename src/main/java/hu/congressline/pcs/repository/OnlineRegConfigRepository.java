package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.congressline.pcs.domain.OnlineRegConfig;

public interface OnlineRegConfigRepository extends JpaRepository<OnlineRegConfig, Long> {

    OnlineRegConfig findOneByCongressId(Long id);
}
