package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.RegistrationRegistrationType;
import hu.congressline.pcs.domain.RegistrationType;

public interface RegistrationRegistrationTypeRepository extends JpaRepository<RegistrationRegistrationType, Long> {

    List<RegistrationRegistrationType> findAllByRegistrationId(Long id);

    List<RegistrationRegistrationType> findAllByRegistrationCongressId(Long id);

    List<RegistrationRegistrationType> findAllByRegistrationCongressIdAndRegistrationType(Long id, RegistrationType registrationType);

    List<RegistrationRegistrationType> findAllByRegistrationTypeId(Long id);

    List<RegistrationRegistrationType> findAllByIdIn(List<Long> ids);

    void deleteAllByRegistrationId(Long id);

}
