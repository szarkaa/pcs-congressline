package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import hu.congressline.pcs.domain.OptionalService;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;

public interface OptionalServiceRepository extends JpaRepository<OptionalService, Long> {

    List<OptionalService> findByCongressIdOrderByName(Long id);

    List<OptionalService> findByOnlineVisibilityAndCongressUuidAndCurrencyCurrencyOrderByOnlineOrder(OnlineVisibility visibility, String uuid, String currency);

    Optional<OptionalService> findOneByCodeAndCongressId(String code, Long congressId);

    List<OptionalService> findAllByIdIn(Set<Long> ids);
}
