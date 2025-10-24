package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import hu.congressline.pcs.domain.RegistrationType;
import hu.congressline.pcs.domain.enumeration.OnlineVisibility;

public interface RegistrationTypeRepository extends JpaRepository<RegistrationType, Long> {

    List<RegistrationType> findByCongressId(Long id);

    List<RegistrationType> findByOnlineVisibilityAndCongressUuidAndCurrencyCurrencyOrderByOnlineOrder(OnlineVisibility visibility, String uuid, String currency);

    Optional<RegistrationType> findOneByCode(String code);

    Optional<RegistrationType> findOneByCodeAndCongressId(String code, Long congressId);
}
