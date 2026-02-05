package hu.congressline.pcs.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

import hu.congressline.pcs.domain.RegistrationRegistrationType;

public interface RegistrationRegistrationTypeRepository extends JpaRepository<RegistrationRegistrationType, Long> {

    List<RegistrationRegistrationType> findAllByRegistrationId(Long id);

    List<RegistrationRegistrationType> findAllByRegistrationCongressId(Long id);

    List<RegistrationRegistrationType> findAllByRegistrationCongressIdAndRegistrationTypeId(Long id, Long registrationTypeId);

    List<RegistrationRegistrationType> findAllByRegistrationTypeId(Long id);

    List<RegistrationRegistrationType> findAllByIdIn(List<Long> ids);

    void deleteAllByRegistrationId(Long id);

}
